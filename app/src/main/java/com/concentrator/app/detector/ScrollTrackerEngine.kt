package com.concentrator.app.detector

import android.content.Context
import com.concentrator.app.data.local.AppDatabase
import com.concentrator.app.data.local.ScrollEntity
import com.concentrator.app.data.model.PlatformType
import com.concentrator.app.data.preferences.UserPreferencesRepository
import com.concentrator.app.service.ScrollNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class ScrollTrackerEngine private constructor(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val db = AppDatabase.getInstance(context)
    private val dao = db.scrollDao()
    private val preferencesRepository = UserPreferencesRepository(context)
    private val notificationHelper = ScrollNotificationHelper(context)

    // Observable live counts
    private val _reelsCountToday = MutableStateFlow(0)
    val reelsCountToday: StateFlow<Int> = _reelsCountToday.asStateFlow()

    private val _shortsCountToday = MutableStateFlow(0)
    val shortsCountToday: StateFlow<Int> = _shortsCountToday.asStateFlow()

    private val _currentForegroundPackage = MutableStateFlow<String?>(null)
    val currentForegroundPackage: StateFlow<String?> = _currentForegroundPackage.asStateFlow()

    // Deduplication and debounce state
    private var lastReelSignature: String? = null
    private var lastReelTimestamp: Long = 0L

    private var lastShortSignature: String? = null
    private var lastShortTimestamp: Long = 0L

    private var notifiedReelsLimitReached = false
    private var notifiedShortsLimitReached = false

    companion object {
        private const val MIN_SCROLL_INTERVAL_MS = 1000L // Min 1.0 second between scrolls
        private const val SAME_VIDEO_LOOP_WINDOW_MS = 12000L // Same signature won't re-trigger within 12s

        @Volatile
        private var INSTANCE: ScrollTrackerEngine? = null

        fun getInstance(context: Context): ScrollTrackerEngine {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ScrollTrackerEngine(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    init {
        loadTodayCounts()
    }

    private fun loadTodayCounts() {
        scope.launch {
            val todayKey = LocalDate.now().toString()
            val reels = dao.getCountForPlatformToday(todayKey, PlatformType.INSTAGRAM_REELS.name)
            val shorts = dao.getCountForPlatformToday(todayKey, PlatformType.YOUTUBE_SHORTS.name)
            _reelsCountToday.value = reels
            _shortsCountToday.value = shorts
        }
    }

    fun onForegroundPackageChanged(packageName: String?) {
        _currentForegroundPackage.value = packageName
    }

    @Synchronized
    fun onReelDetected(signature: String, author: String?) {
        val now = System.currentTimeMillis()
        val elapsed = now - lastReelTimestamp

        // Ignore if scrolled too quickly (less than 1s)
        if (elapsed < MIN_SCROLL_INTERVAL_MS) return

        // Ignore if it's the exact same reel playing on loop within 12 seconds
        if (signature == lastReelSignature && elapsed < SAME_VIDEO_LOOP_WINDOW_MS) return

        lastReelSignature = signature
        lastReelTimestamp = now

        recordScroll(PlatformType.INSTAGRAM_REELS, signature, author)
    }

    @Synchronized
    fun onShortDetected(signature: String, channel: String?, title: String?) {
        val now = System.currentTimeMillis()
        val elapsed = now - lastShortTimestamp

        // Ignore if scrolled too quickly (less than 1s)
        if (elapsed < MIN_SCROLL_INTERVAL_MS) return

        // Ignore if it's the exact same short playing on loop within 12 seconds
        if (signature == lastShortSignature && elapsed < SAME_VIDEO_LOOP_WINDOW_MS) return

        lastShortSignature = signature
        lastShortTimestamp = now

        val creatorInfo = if (title != null && channel != null) "$channel - $title" else channel ?: title
        recordScroll(PlatformType.YOUTUBE_SHORTS, signature, creatorInfo)
    }

    private fun recordScroll(platform: PlatformType, signature: String, creator: String?) {
        scope.launch {
            val now = System.currentTimeMillis()
            val todayKey = LocalDate.now().toString()
            val currentHour = LocalTime.now().hour

            val entity = ScrollEntity(
                platform = platform.name,
                timestamp = now,
                dateKey = todayKey,
                hourOfDay = currentHour,
                contentSignature = signature,
                creatorName = creator
            )

            dao.insert(entity)

            // Update in-memory counts
            when (platform) {
                PlatformType.INSTAGRAM_REELS -> {
                    val newCount = _reelsCountToday.value + 1
                    _reelsCountToday.value = newCount
                    checkLimitAlert(platform, newCount)
                }
                PlatformType.YOUTUBE_SHORTS -> {
                    val newCount = _shortsCountToday.value + 1
                    _shortsCountToday.value = newCount
                    checkLimitAlert(platform, newCount)
                }
            }
        }
    }

    private suspend fun checkLimitAlert(platform: PlatformType, currentCount: Int) {
        val prefs = preferencesRepository.userPreferencesFlow.first()
        if (!prefs.isNotificationsEnabled) return

        when (platform) {
            PlatformType.INSTAGRAM_REELS -> {
                if (currentCount >= prefs.reelsLimit && !notifiedReelsLimitReached) {
                    notifiedReelsLimitReached = true
                    notificationHelper.triggerLimitAlert(
                        platform,
                        currentCount,
                        prefs.reelsLimit,
                        prefs.isVibrationEnabled
                    )
                }
            }
            PlatformType.YOUTUBE_SHORTS -> {
                if (currentCount >= prefs.shortsLimit && !notifiedShortsLimitReached) {
                    notifiedShortsLimitReached = true
                    notificationHelper.triggerLimitAlert(
                        platform,
                        currentCount,
                        prefs.shortsLimit,
                        prefs.isVibrationEnabled
                    )
                }
            }
        }
    }

    fun resetDailyNotificationFlags() {
        notifiedReelsLimitReached = false
        notifiedShortsLimitReached = false
    }
}

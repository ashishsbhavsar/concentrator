package com.concentrator.app.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.concentrator.app.data.local.AppDatabase
import com.concentrator.app.data.model.DailyStatSummary
import com.concentrator.app.data.model.HourlyCount
import com.concentrator.app.data.preferences.UserPreferencesRepository
import com.concentrator.app.detector.ScrollTrackerEngine
import com.concentrator.app.service.FloatingOverlayService
import com.concentrator.app.service.ScrollTrackerAccessibilityService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.scrollDao()
    private val engine = ScrollTrackerEngine.getInstance(application)
    private val preferencesRepo = UserPreferencesRepository(application)

    private val _isAccessibilityGranted = MutableStateFlow(false)
    val isAccessibilityGranted: StateFlow<Boolean> = _isAccessibilityGranted.asStateFlow()

    private val _isOverlayGranted = MutableStateFlow(false)
    val isOverlayGranted: StateFlow<Boolean> = _isOverlayGranted.asStateFlow()

    private val _isNotificationsGranted = MutableStateFlow(true)
    val isNotificationsGranted: StateFlow<Boolean> = _isNotificationsGranted.asStateFlow()

    private val _isOverlayRunning = MutableStateFlow(false)
    val isOverlayRunning: StateFlow<Boolean> = _isOverlayRunning.asStateFlow()

    val statSummary: StateFlow<DailyStatSummary> = combine(
        engine.reelsCountToday,
        engine.shortsCountToday,
        dao.observeHourlyStatsForDate(LocalDate.now().toString()),
        preferencesRepo.userPreferencesFlow
    ) { reels, shorts, hourlyStats, prefs ->
        val total = reels + shorts
        // Average 25 seconds per reel/short
        val estimatedMins = (total * 25) / 60

        val mappedHourly = (0..23).map { hour ->
            val stat = hourlyStats.firstOrNull { it.hourOfDay == hour }
            HourlyCount(hourOfDay = hour, count = stat?.count ?: 0)
        }

        DailyStatSummary(
            reelsCount = reels,
            shortsCount = shorts,
            totalCount = total,
            estimatedMinutes = estimatedMins,
            reelsLimit = prefs.reelsLimit,
            shortsLimit = prefs.shortsLimit,
            totalLimit = prefs.totalLimit,
            hourlyCounts = mappedHourly
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DailyStatSummary()
    )

    init {
        refreshPermissionStates()
    }

    fun refreshPermissionStates() {
        val context = getApplication<Application>()
        _isAccessibilityGranted.value = ScrollTrackerAccessibilityService.isAccessibilityServiceEnabled(context)
        _isOverlayGranted.value = Settings.canDrawOverlays(context)
        _isNotificationsGranted.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } else {
            true
        }
        _isOverlayRunning.value = FloatingOverlayService.isOverlayRunning
    }

    fun toggleFloatingOverlay() {
        val context = getApplication<Application>()
        if (FloatingOverlayService.isOverlayRunning) {
            FloatingOverlayService.stop(context)
            _isOverlayRunning.value = false
        } else {
            if (Settings.canDrawOverlays(context)) {
                FloatingOverlayService.start(context)
                _isOverlayRunning.value = true
            }
        }
    }

    // Helper simulation method for quick demo and testing
    fun simulateTestReel() {
        viewModelScope.launch {
            val randomId = (1000..9999).random()
            engine.onReelDetected("sim_reel_$randomId", "creator_$randomId")
        }
    }

    fun simulateTestShort() {
        viewModelScope.launch {
            val randomId = (1000..9999).random()
            engine.onShortDetected("sim_short_$randomId", "ShortChannel_$randomId", "Interesting Short #$randomId")
        }
    }
}

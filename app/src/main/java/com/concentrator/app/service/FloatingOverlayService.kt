package com.concentrator.app.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import com.concentrator.app.R
import com.concentrator.app.data.model.PlatformType
import com.concentrator.app.data.preferences.UserPreferencesRepository
import com.concentrator.app.detector.ScrollTrackerEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FloatingOverlayService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var engine: ScrollTrackerEngine
    private lateinit var preferencesRepo: UserPreferencesRepository
    private lateinit var notificationHelper: ScrollNotificationHelper

    companion object {
        var isOverlayRunning = false
            private set

        fun start(context: Context) {
            val intent = Intent(context, FloatingOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, FloatingOverlayService::class.java)
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        engine = ScrollTrackerEngine.getInstance(this)
        preferencesRepo = UserPreferencesRepository(this)
        notificationHelper = ScrollNotificationHelper(this)

        startForeground(
            ScrollNotificationHelper.NOTIFICATION_ID_OVERLAY,
            notificationHelper.buildOverlayServiceNotification(0, 0)
        )

        isOverlayRunning = true

        if (Settings.canDrawOverlays(this)) {
            initOverlayView()
            observeEngineAndPreferences()
        } else {
            stopSelf()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initOverlayView() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 60
            y = 320
        }

        // Create overlay container programmatically with sleek glassmorphism design
        val container = FrameLayout(this).apply {
            setPadding(28, 16, 28, 16)
            background = createPillBackground()
            elevation = 16f
        }

        val textView = TextView(this).apply {
            id = View.generateViewId()
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 13f
            text = "📸 0  •  🎬 0"
        }
        container.addView(textView)

        // Make draggable
        container.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = layoutParams.x
                        initialY = layoutParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(container, layoutParams)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        serviceScope.launch {
                            preferencesRepo.updateOverlayPosition(layoutParams.x, layoutParams.y)
                        }
                        return true
                    }
                }
                return false
            }
        })

        overlayView = container
        windowManager?.addView(container, layoutParams)
    }

    private fun createPillBackground(): android.graphics.drawable.GradientDrawable {
        return android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            cornerRadius = 48f
            setColor(0xE61E293B.toInt()) // Deep slate with 90% opacity
            setStroke(2, 0x4D6366F1.toInt()) // Subtle indigo border
        }
    }

    private fun observeEngineAndPreferences() {
        serviceScope.launch {
            // Restore saved position
            val initialPrefs = preferencesRepo.userPreferencesFlow.first()
            layoutParams.x = initialPrefs.overlayPositionX
            layoutParams.y = initialPrefs.overlayPositionY
            overlayView?.let { windowManager?.updateViewLayout(it, layoutParams) }

            // Observe counts and foreground package
            combine(
                engine.reelsCountToday,
                engine.shortsCountToday,
                engine.currentForegroundPackage,
                preferencesRepo.userPreferencesFlow
            ) { reels, shorts, foregroundPackage, prefs ->
                OverlayState(reels, shorts, foregroundPackage, prefs.autoHideOverlayOutsideSocial, prefs.reelsLimit, prefs.shortsLimit)
            }.collect { state ->
                updateOverlayContent(state)
            }
        }
    }

    private fun updateOverlayContent(state: OverlayState) {
        val view = overlayView ?: return
        val textView = (view as? FrameLayout)?.getChildAt(0) as? TextView ?: return

        // Auto-hide outside of Instagram and YouTube if enabled
        val isSocialForeground = state.foregroundPackage == PlatformType.INSTAGRAM_REELS.packageName ||
                state.foregroundPackage == PlatformType.YOUTUBE_SHORTS.packageName

        if (state.autoHide && !isSocialForeground) {
            view.visibility = View.GONE
            return
        } else {
            view.visibility = View.VISIBLE
        }

        val isExceeded = state.reels >= state.reelsLimit || state.shorts >= state.shortsLimit
        val isWarning = state.reels >= state.reelsLimit * 0.8 || state.shorts >= state.shortsLimit * 0.8

        val statusIndicator = when {
            isExceeded -> "⚠️"
            isWarning -> "⏳"
            else -> "✨"
        }

        textView.text = "$statusIndicator 📸 ${state.reels}  •  🎬 ${state.shorts}"

        // Update foreground service persistent notification
        val updatedNotification = notificationHelper.buildOverlayServiceNotification(state.reels, state.shorts)
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notifManager.notify(ScrollNotificationHelper.NOTIFICATION_ID_OVERLAY, updatedNotification)
    }

    override fun onDestroy() {
        super.onDestroy()
        isOverlayRunning = false
        serviceScope.cancel()
        overlayView?.let {
            windowManager?.removeView(it)
            overlayView = null
        }
    }

    private data class OverlayState(
        val reels: Int,
        val shorts: Int,
        val foregroundPackage: String?,
        val autoHide: Boolean,
        val reelsLimit: Int,
        val shortsLimit: Int
    )
}

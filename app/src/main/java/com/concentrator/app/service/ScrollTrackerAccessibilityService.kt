package com.concentrator.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.concentrator.app.data.model.PlatformType
import com.concentrator.app.detector.ReelDetector
import com.concentrator.app.detector.ScrollTrackerEngine
import com.concentrator.app.detector.ShortDetector

class ScrollTrackerAccessibilityService : AccessibilityService() {

    private lateinit var engine: ScrollTrackerEngine
    private val reelDetector = ReelDetector()
    private val shortDetector = ShortDetector()

    companion object {
        @Volatile
        var isRunning: Boolean = false
            private set

        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val expectedComponentName = ComponentName(context, ScrollTrackerAccessibilityService::class.java)
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)

            while (colonSplitter.hasNext()) {
                val componentNameString = colonSplitter.next()
                val enabledComponent = ComponentName.unflattenFromString(componentNameString)
                if (enabledComponent != null && enabledComponent == expectedComponentName) {
                    return true
                }
            }
            return false
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isRunning = true
        engine = ScrollTrackerEngine.getInstance(this)

        val info = serviceInfo ?: AccessibilityServiceInfo()
        info.packageNames = arrayOf(
            PlatformType.INSTAGRAM_REELS.packageName,
            PlatformType.YOUTUBE_SHORTS.packageName
        )
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        info.notificationTimeout = 100
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        // Update foreground state on window transition
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            engine.onForegroundPackageChanged(packageName)
        }

        var root: AccessibilityNodeInfo? = null
        try {
            root = rootInActiveWindow ?: return

            when (packageName) {
                PlatformType.INSTAGRAM_REELS.packageName -> {
                    val result = reelDetector.inspect(root)
                    if (result != null && result.isReel) {
                        engine.onReelDetected(result.signature, result.author)
                    }
                }
                PlatformType.YOUTUBE_SHORTS.packageName -> {
                    val result = shortDetector.inspect(root)
                    if (result != null && result.isShort) {
                        engine.onShortDetected(result.signature, result.channel, result.title)
                    }
                }
            }
        } catch (_: Exception) {
            // Handle transient accessibility tree exceptions gracefully
        } finally {
            root?.recycle()
        }
    }

    override fun onInterrupt() {
        isRunning = false
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }
}

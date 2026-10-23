package com.concentrator.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.concentrator.app.MainActivity
import com.concentrator.app.R
import com.concentrator.app.data.model.PlatformType

class ScrollNotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID_OVERLAY = "channel_floating_overlay"
        const val CHANNEL_ID_ALERTS = "channel_limit_alerts"
        const val NOTIFICATION_ID_OVERLAY = 1001
        const val NOTIFICATION_ID_REEL_ALERT = 2001
        const val NOTIFICATION_ID_SHORT_ALERT = 2002
        const val NOTIFICATION_ID_TOTAL_ALERT = 2003
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Foreground Service Channel
            val overlayChannel = NotificationChannel(
                CHANNEL_ID_OVERLAY,
                context.getString(R.string.overlay_notification_title),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps the floating scroll counter active"
                setShowBadge(false)
            }

            // Limit Alerts Channel (High Priority)
            val alertChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                context.getString(R.string.limit_alert_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.limit_alert_channel_description)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }

            notificationManager.createNotificationChannel(overlayChannel)
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    fun buildOverlayServiceNotification(reelsCount: Int, shortsCount: Int): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = "Scrolled today: 📸 $reelsCount Reels  •  🎬 $shortsCount Shorts"

        return NotificationCompat.Builder(context, CHANNEL_ID_OVERLAY)
            .setContentTitle(context.getString(R.string.overlay_notification_title))
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    fun triggerLimitAlert(platform: PlatformType, count: Int, limit: Int, vibrate: Boolean) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            platform.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, body, notifId) = when (platform) {
            PlatformType.INSTAGRAM_REELS -> Triple(
                context.getString(R.string.reel_limit_reached_title),
                context.getString(R.string.reel_limit_reached_body, count),
                NOTIFICATION_ID_REEL_ALERT
            )
            PlatformType.YOUTUBE_SHORTS -> Triple(
                context.getString(R.string.short_limit_reached_title),
                context.getString(R.string.short_limit_reached_body, count),
                NOTIFICATION_ID_SHORT_ALERT
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notifId, notification)

        if (vibrate) {
            triggerHapticVibration()
        }
    }

    private fun triggerHapticVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 250, 150, 250), -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 250, 150, 250), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
        } catch (_: Exception) {
            // Ignore if permission or hardware unavailable
        }
    }
}

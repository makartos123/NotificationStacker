package com.makartos.systemui_logger.logger

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import java.lang.reflect.Method

class SystemUILogger {

    fun captureNotificationService(context: Context): String {
        val sb = StringBuilder()
        sb.append("\n=== NOTIFICATION SERVICE ANALYSIS ===\n")

        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Try to get active notifications (requires NotificationListenerService)
            sb.append("\n[API Level: ${Build.VERSION.SDK_INT}]\n")
            sb.append("[Device: ${Build.MANUFACTURER} ${Build.MODEL}]\n")
            sb.append("[Android: ${Build.VERSION.RELEASE}]\n\n")

            // Reflection to access package-private methods
            try {
                val getActiveNotificationsMethod: Method? = try {
                    notificationManager.javaClass.getMethod("getActiveNotifications")
                } catch (e: Exception) {
                    null
                }

                if (getActiveNotificationsMethod != null) {
                    getActiveNotificationsMethod.isAccessible = true
                    val activeNotifications = getActiveNotificationsMethod.invoke(notificationManager) as? Array<*>
                    
                    if (activeNotifications != null && activeNotifications.isNotEmpty()) {
                        sb.append("Found ${activeNotifications.size} active notifications:\n")
                        activeNotifications.forEachIndexed { index, notif ->
                            sb.append("\n--- Notification #$index ---\n")
                            sb.append(analyzeStatusBarNotification(notif))
                        }
                    } else {
                        sb.append("No active notifications found (or method not accessible)\n")
                    }
                } else {
                    sb.append("[WARNING] getActiveNotifications method not found\n")
                }
            } catch (e: Exception) {
                sb.append("[ERROR] Could not access active notifications: ${e.message}\n")
            }

            // Try to get notification channels info
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sb.append("\n=== NOTIFICATION CHANNELS ===\n")
                try {
                    val channels = notificationManager.notificationChannels
                    sb.append("Total channels: ${channels.size}\n")
                    channels.forEach { channel ->
                        sb.append("  - ${channel.id}: ${channel.name}\n")
                        sb.append("    Importance: ${channel.importance}\n")
                        sb.append("    Sound: ${channel.sound}\n")
                    }
                } catch (e: Exception) {
                    sb.append("[ERROR] Could not get channels: ${e.message}\n")
                }
            }

        } catch (e: Exception) {
            sb.append("[ERROR] ${e.javaClass.simpleName}: ${e.message}\n")
        }

        return sb.toString()
    }

    private fun analyzeStatusBarNotification(notif: Any): String {
        val sb = StringBuilder()
        try {
            val javaClass = notif.javaClass
            val keyMethod = javaClass.getMethod("getKey")
            val notificationMethod = javaClass.getMethod("getNotification")
            val packageNameMethod = javaClass.getMethod("getPackageName")
            val idMethod = javaClass.getMethod("getId")
            val tagMethod = javaClass.getMethod("getTag")
            val postTimeMethod = javaClass.getMethod("getPostTime")

            keyMethod.isAccessible = true
            notificationMethod.isAccessible = true
            packageNameMethod.isAccessible = true
            idMethod.isAccessible = true
            tagMethod.isAccessible = true
            postTimeMethod.isAccessible = true

            val key = keyMethod.invoke(notif) as? String ?: "unknown"
            val notification = notificationMethod.invoke(notif) as? Notification
            val packageName = packageNameMethod.invoke(notif) as? String ?: "unknown"
            val id = idMethod.invoke(notif) as? Int ?: -1
            val tag = tagMethod.invoke(notif) as? String ?: "(no tag)"
            val postTime = postTimeMethod.invoke(notif) as? Long ?: 0L

            sb.append("Key: $key\n")
            sb.append("Package: $packageName\n")
            sb.append("ID: $id\n")
            sb.append("Tag: $tag\n")
            sb.append("PostTime: $postTime\n")

            if (notification != null) {
                sb.append("\nNotification Details:\n")
                sb.append("  ContentTitle: ${notification.extras?.getCharSequence(Notification.EXTRA_TITLE)}\n")
                sb.append("  ContentText: ${notification.extras?.getCharSequence(Notification.EXTRA_TEXT)}\n")
                sb.append("  Priority: ${notification.priority}\n")
                sb.append("  Flags: ${notification.flags}\n")
                sb.append("  SmallIcon: ${notification.smallIcon}\n")
                sb.append("  LargeIcon: ${notification.largeIcon}\n")
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sb.append("  Visibility: ${notification.visibility}\n")
                }
            }
        } catch (e: Exception) {
            sb.append("[ERROR] Failed to parse: ${e.message}\n")
        }
        return sb.toString()
    }
}

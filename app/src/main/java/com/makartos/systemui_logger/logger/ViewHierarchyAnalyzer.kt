package com.makartos.systemui_logger.logger

import android.os.Build
import android.view.accessibility.AccessibilityManager
import java.lang.reflect.Field

class ViewHierarchyAnalyzer {

    fun analyzeSystemUIStructure(): String {
        val sb = StringBuilder()
        sb.append("\n=== SYSTEMUI STRUCTURE ANALYSIS ===\n\n")

        // Analyze known SystemUI classes
        val systemUIClasses = mapOf(
            "NotificationShade" to "com.android.systemui.shade.NotificationShade",
            "NotificationStackScrollLayout" to "com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout",
            "ExpandableNotificationRow" to "com.android.systemui.statusbar.notification.row.ExpandableNotificationRow",
            "NotificationShadeWindowView" to "com.android.systemui.shade.NotificationShadeWindowView",
            "NotificationEntryManager" to "com.android.systemui.statusbar.notification.NotificationEntryManager",
            "NotificationListenerService" to "android.service.notification.NotificationListenerService",
            "StatusBar" to "com.android.systemui.statusbar.phone.StatusBarWindowView",
            "ScrimController" to "com.android.systemui.statusbar.phone.ScrimController",
            "NotificationShadeDepthController" to "com.android.systemui.statusbar.NotificationShadeDepthController"
        )

        sb.append("Scanning SystemUI Components:\n")
        systemUIClasses.forEach { (name, className) ->
            sb.append("\n[$name]\n")
            sb.append("  ClassName: $className\n")
            
            try {
                val clazz = Class.forName(className)
                sb.append("  Status: ✓ FOUND\n")
                sb.append("  Methods (Notification-related):\n")
                
                clazz.declaredMethods.filter {
                    it.name.contains("notification", ignoreCase = true) ||
                    it.name.contains("expand", ignoreCase = true) ||
                    it.name.contains("collapse", ignoreCase = true) ||
                    it.name.contains("stack", ignoreCase = true) ||
                    it.name.contains("shade", ignoreCase = true) ||
                    it.name.contains("entry", ignoreCase = true)
                }.take(10).forEach { method ->
                    sb.append("    - ${method.name}\n")
                }
                
                sb.append("  Fields:\n")
                clazz.declaredFields.filter {
                    it.name.contains("notif", ignoreCase = true) ||
                    it.name.contains("stack", ignoreCase = true) ||
                    it.name.contains("expand", ignoreCase = true)
                }.take(5).forEach { field ->
                    sb.append("    - ${field.name} (${field.type.simpleName})\n")
                }
            } catch (e: ClassNotFoundException) {
                sb.append("  Status: ✗ NOT FOUND\n")
            } catch (e: Exception) {
                sb.append("  Status: ✗ ERROR - ${e.message}\n")
            }
        }

        // Android version specific info
        sb.append("\n\n=== VERSION-SPECIFIC INFO ===\n")
        sb.append("SDK: ${Build.VERSION.SDK_INT}\n")
        sb.append("Release: ${Build.VERSION.RELEASE}\n")
        sb.append("Base OS: ${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.BASE_OS else "N/A"}\n")

        // Android 16+ specific
        if (Build.VERSION.SDK_INT >= 34) {
            sb.append("\n[ANDROID 14+ SPECIFIC]\n")
            sb.append("  - Predictive Back Gesture available\n")
            sb.append("  - Extended notification UI\n")
            sb.append("  - Material 3 Design\n")
        }

        return sb.toString()
    }

    fun findXposedHookPoints(): String {
        val sb = StringBuilder()
        sb.append("\n=== XPOSED HOOK POINTS ===\n\n")

        val hookPoints = mapOf(
            "NotificationStackScrollLayout.onLayout" to "Controls notification positioning and spacing",
            "ExpandableNotificationRow.setExpanded" to "Controls row expansion state",
            "ExpandableNotificationRow.setHeadsUpAnimatingAway" to "Controls heads-up animation",
            "NotificationShade.onShadeExpanded" to "Called when shade is expanded",
            "NotificationShade.onShadeCollapsed" to "Called when shade is collapsed",
            "NotificationStackScrollLayout.generateLayoutParams" to "Controls layout parameters",
            "NotificationShadeWindowView.onConfigurationChanged" to "Configuration changes",
            "NotificationEntryManager.addNotification" to "New notification added",
            "NotificationEntryManager.removeNotification" to "Notification removed",
            "ScrimController.setScrimBehindAlpha" to "Controls scrim background"
        )

        hookPoints.forEach { (hook, purpose) ->
            sb.append("$hook\n")
            sb.append("  → $purpose\n\n")
        }

        return sb.toString()
    }
}

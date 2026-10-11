# SystemUI Logger

Xposed module debug tool for analyzing SystemUI components, particularly notification-related elements on Android 16-17.

## Features

- **Notification Capture**: Extract detailed information about active notifications
- **SystemUI Analysis**: Scan for notification-related classes and methods in SystemUI
- **Real-time Logging**: View logs in real-time within the app
- **Copy to Clipboard**: Quick copy all logs to clipboard for easy sharing
- **Export Logs**: Save logs to file for later analysis
- **Hook Point Detection**: Identify potential Xposed hook points

## Building

```bash
./gradlew assembleDebug
```

## Installation

1. Build the APK: `./gradlew assembleDebug`
2. Install: `adb install app/build/outputs/apk/debug/app-debug.apk`
3. Grant necessary permissions in Settings
4. Open the app and run analyses

## Permissions Required

- `android.permission.DUMP` - Access system dumps
- `android.permission.PACKAGE_USAGE_STATS` - Monitor package usage
- `android.permission.READ_LOGS` - Read system logs

## Usage

### Capture Notifications
Click "Capture Notifications" to analyze active notifications from NotificationManager.
This will show:
- Notification keys and tags
- Package names
- Titles and content
- Priority and visibility settings
- Icon information

### Capture SystemUI
Click "Capture SystemUI" to scan available SystemUI components.
This will identify:
- NotificationStackScrollLayout
- ExpandableNotificationRow
- NotificationShade
- And other relevant classes

### Analyze Results

The logger will identify:
1. **Available Classes** - Which SystemUI classes are available on your device
2. **Relevant Methods** - Methods related to notifications, expansion, and stacking
3. **Accessible Fields** - Fields that can be hooked or modified
4. **Hook Points** - Suggested Xposed hook locations

## Hook Points for Notification Stacking

Key areas to hook for stacking implementation:

```
- NotificationStackScrollLayout.onLayout()
- ExpandableNotificationRow.setExpanded()
- NotificationShade.onShadeExpanded() / onShadeCollapsed()
- NotificationStackScrollLayout.generateLayoutParams()
- NotificationEntryManager.addNotification()
```

## Next Steps

Once you've identified the exact methods and fields:

1. Create Xposed hooks in these locations
2. Modify `translationY`, `scale`, `alpha` for stacking effect
3. Implement collapse/expand animations
4. Test on both Android 16 and 17

## Architecture

```
app/
├── src/main/java/com/makartos/systemui_logger/
│   ├── MainActivity.kt              # Main UI
│   ├── LogAdapter.kt                # RecyclerView adapter
│   ├── logger/
│   │   ├── SystemUILogger.kt        # Notification analysis
│   │   └── ViewHierarchyAnalyzer.kt # SystemUI component scanning
```

## Notes

- This is a debug/analysis tool, not the final Xposed module
- Requires system permissions (may need ADB)
- Results will vary based on Android version and OEM modifications
- Log results to understand your device's specific SystemUI implementation

## License

MIT

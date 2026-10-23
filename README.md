# Concentrator 🧘
### Intelligent Reel & Short Scroll Tracker for Android

**Concentrator** is a native Android application built with **Kotlin** and **Jetpack Compose (Material 3)** that tracks how many **Instagram Reels** and **YouTube Shorts** you scroll through. It helps you become mindful of your digital consumption, stop mindless doomscrolling, and stay within healthy daily screen limits.

---

## ✨ Features

- 📸 **Instagram Reels Detection**: Detects each individual reel scrolled inside the Instagram app using a content signature hashing algorithm.
- 🎬 **YouTube Shorts Detection**: Detects each individual short video scrolled inside the YouTube app.
- ⏱️ **Estimated Screen Time**: Automatically calculates the estimated time spent watching short-form content based on average video durations.
- 🪟 **Floating Heads-Up Display (HUD)**: Sleek, draggable floating counter widget displayed directly on top of Instagram and YouTube showing live counts.
  - *Smart Auto-Hide*: Automatically disappears when you exit Instagram/YouTube so it never obstructs other apps.
- 🎯 **Daily Scroll Budgets**: Set custom daily scroll limits for Instagram Reels and YouTube Shorts independently.
- 🔔 **Mindful Alerts & Haptics**: High-priority notifications and gentle tactile vibrations when you hit your daily threshold.
- 📊 **24-Hour Scroll Heatmap**: Interactive hourly bar chart highlighting when your doomscrolling peaks (e.g. late night spikes).
- 📅 **Historical Log**: Track past days and total hours saved vs spent.
- 🔒 **100% On-Device & Private**: Zero cloud sync or tracking. All scroll logs are stored locally in an encrypted Room SQLite database.
- 🧪 **Built-in Quick Simulator**: Test and verify the counter logic and floating HUD right inside the dashboard without having to open external apps.

---

## 🛠️ How It Works Under The Hood

Because Instagram and YouTube do not offer public APIs for user scroll counts in their short-form video feeds, Concentrator uses an Android **`AccessibilityService`** with focused package filters:

1. **Package Scope Filter**:
   The service only monitors `com.instagram.android` and `com.google.android.youtube`. The Android operating system will *never* send events from your personal messaging, banking, or browser apps.

2. **Reel / Short Screen Recognition**:
   - For Instagram: Evaluates the presence of Reels-specific views (e.g. `clips_viewer`, audio titles, creator usernames).
   - For YouTube Shorts: Evaluates the Shorts container (`reel_player_page_view`, channel handle `@...`, remix & audio tags).

3. **Content Signature & Deduplication**:
   - A unique composite hash is generated for each video: `hash(creator + sound + caption)`.
   - **Debounce Window**: Ignores rapid micro-gestures under 1.0 second.
   - **Loop Deduplication**: Ignores repeated events for 12 seconds if the user leaves the same video playing on repeat.
   - Only when a new signature appears is the counter incremented by 1 and saved to Room DB.

---

## 📂 Project Architecture

```
Concentrator/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml                  # Permissions & Service declarations
│   │   ├── res/
│   │   │   ├── xml/accessibility_service_config.xml # Accessibility package filters
│   │   │   ├── values/{strings,colors,themes}.xml
│   │   │   └── drawable/                        # Vector icons & adaptive launcher
│   │   └── java/com/concentrator/app/
│   │       ├── ConcentratorApp.kt               # Application entry point
│   │       ├── MainActivity.kt                  # Compose root Activity
│   │       ├── data/
│   │       │   ├── model/                       # Data models & PlatformType enum
│   │       │   ├── local/                       # Room Database, ScrollEntity, ScrollDao
│   │       │   └── preferences/                 # DataStore for daily limits & settings
│   │       ├── detector/
│   │       │   ├── ReelDetector.kt              # Instagram view hierarchy parser
│   │       │   ├── ShortDetector.kt             # YouTube Shorts view hierarchy parser
│   │       │   └── ScrollTrackerEngine.kt       # Debounce, deduplication, & state dispatcher
│   │       ├── service/
│   │       │   ├── ScrollTrackerAccessibilityService.kt # Core Android Accessibility Service
│   │       │   ├── FloatingOverlayService.kt    # Draggable WindowManager floating HUD
│   │       │   └── ScrollNotificationHelper.kt  # System notification & haptic alerts
│   │       └── ui/
│   │           ├── theme/                       # Modern dark Material 3 color system
│   │           ├── components/                  # StatsCard, CircularBadge, Heatmap
│   │           ├── screens/                     # Dashboard, Analytics, Settings
│   │           ├── navigation/                  # BottomBar & NavHost
│   │           └── viewmodel/                   # DashboardViewModel & SettingsViewModel
├── gradle/
│   ├── libs.versions.toml                       # Modern Gradle Version Catalog
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts                             # Root Gradle script
├── settings.gradle.kts                          # Module management
└── local.properties                             # Configured Android SDK path
```


# Simple Pomodoro - Wear OS

A minimal, guilt-free pomodoro timer for Wear OS with gentle haptics and zero task management. This standalone watch app helps you focus without anxiety-inducing features or aggressive notifications.

## Philosophy

Simple Pomodoro embraces a "no guilt" approach to productivity:
- **No task lists** - Just focus time and rest time
- **No streaks or judgmental stats** - Work at your own pace
- **Gentle haptics** - Soft vibrations that don't startle
- **Calm colors** - Muted blues and greens instead of aggressive red/green
- **Progress, not pressure** - Shows completion rather than countdown anxiety

## Features

### Core Functionality
- ⏱️ **25/5 or 50/10 minute cycles** - Choose between Standard and Extended presets
- 📳 **Gentle haptic feedback** - Soft vibrations for phase start, completion, and pause
- 🎯 **Tap anywhere to pause/resume** - No hunting for buttons
- ⚙️ **Long-press for settings** - Quick access without cluttering the UI
- 🔄 **Automatic phase cycling** - Seamlessly transitions between Focus and Break
- 💾 **Persistent settings** - Your preferences are saved across restarts

### Polish & UX
- 🌙 **Ambient mode support** - Battery-efficient always-on display
- 🔔 **Interactive notifications** - Pause, resume, or stop from notification
- ✨ **First launch hint** - Subtle animation teaches long-press gesture
- 🎨 **Clean visual design** - Circular progress with smooth animations
- 🔋 **Battery optimized** - Minimal animations in ambient mode

### Technical Highlights
- 📱 **Fully standalone** - No phone required, runs entirely on watch
- 🎯 **Foreground service** - Timer keeps running even when screen is off
- 🔄 **State management** - Proper lifecycle handling with ViewModel
- 📊 **DataStore persistence** - Settings saved with modern Jetpack DataStore

## Tech Stack

- **Wear OS**: Jetpack Compose for Wear OS
- **UI**: Compose Material for Wear, ScalingLazyColumn with rotary input support
- **State Management**: Kotlin StateFlow and ViewModel
- **Persistence**: DataStore Preferences
- **Background Work**: Foreground Service with CountDownTimer
- **Navigation**: Wear Compose Navigation with swipe-to-dismiss
- **Haptics**: VibrationEffect with custom gentle patterns

## Architecture

```
app/
├── data/
│   ├── model/
│   │   └── PomodoroModels.kt           # Data models (State, Settings, Phase)
│   └── repository/
│       └── SettingsRepository.kt        # DataStore persistence layer
├── presentation/
│   ├── timer/
│   │   └── PomodoroScreen.kt           # Main timer UI with circular progress
│   ├── settings/
│   │   └── SettingsScreen.kt           # Settings with preset chips
│   ├── navigation/
│   │   └── NavGraph.kt                 # Navigation setup
│   ├── theme/
│   │   └── Theme.kt                    # Calm color palette
│   ├── PomodoroViewModel.kt            # Service connection & state
│   └── MainActivity.kt                 # Entry point
└── service/
    ├── PomodoroService.kt               # Foreground service for timer
    └── HapticUtils.kt                   # Gentle haptic feedback patterns
```

## Key Components

### PomodoroService
Foreground service that manages timer logic independently of the UI:
- CountDownTimer for accurate time tracking
- Automatic phase transitions (Focus → Break → Focus)
- Gentle haptic feedback at key moments
- Persistent notification with pause/resume/stop actions
- State exposed via StateFlow for UI reactivity

### PomodoroScreen
Main timer interface with three states:
- **Idle**: "Tap to start" with animated hint for settings
- **Running**: Circular progress with time remaining and phase label
- **Paused**: Dimmed UI with "Tap to resume" hint
- Ambient mode optimization (pure black background, minimal text)

### SettingsScreen
Simple preset selection:
- Standard: 25 min focus / 5 min break
- Extended: 50 min focus / 10 min break
- Visual feedback for current selection
- Auto-save and navigate back on selection

### Gentle Haptic Patterns
Custom vibration patterns designed to be non-intrusive:
```kotlin
PHASE_START:    Double tap (50ms, 100ms, 50ms)
PHASE_COMPLETE: Soft crescendo (100ms → 200ms → 100ms)
PAUSE:          Single subtle tap (30ms)
```

## Color Palette

Following the "no guilt" philosophy:
- **Focus**: Calm blue (#6B9BD1)
- **Break**: Gentle green (#98C9A3)
- **Background**: Dark but not harsh (#1A1A1A)
- **Text**: Soft white (#E0E0E0)

## Building

### Prerequisites
- Android Studio Hedgehog or later
- Wear OS emulator or physical Wear OS device (API 30+)

### Build Instructions
```bash
# Clone the repository
git clone <repository-url>
cd PomodoroWearOS

# Build the project
./gradlew build

# Install on connected Wear OS device
./gradlew installDebug

# Or build release APK
./gradlew assembleRelease
```

### Dependencies
```kotlin
// Wear OS Compose
androidx.wear.compose:compose-material:1.2.1
androidx.wear.compose:compose-foundation:1.2.1
androidx.wear.compose:compose-navigation:1.3.0

// DataStore for settings persistence
androidx.datastore:datastore-preferences:1.0.0

// ViewModel and lifecycle
androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0
androidx.lifecycle:lifecycle-runtime-compose:2.7.0
```

## Usage

1. **Start Timer**: Tap anywhere on the idle screen
2. **Pause/Resume**: Tap anywhere during timer
3. **Access Settings**: Long-press on the timer screen
4. **Change Preset**: Select Standard or Extended in settings
5. **From Notification**: Use Pause/Resume/Stop buttons in notification

## Implementation Phases

This project was implemented in 5 phases:

1. **Phase 1**: Setup & Data Layer - Dependencies, models, repository, navigation
2. **Phase 2**: Core Service & Timer Logic - Foreground service, haptics, state management
3. **Phase 3**: Timer UI - Circular progress, tap gestures, animations
4. **Phase 4**: Settings Screen - Preset selection, rotary input support
5. **Phase 5**: Polish & Testing - Ambient mode, notification actions, edge cases

## Future Enhancements

Potential features to consider:
- Customizable haptic patterns
- Weekly completion stats (non-judgmental)
- Watch face complication
- Silent mode during meetings (calendar integration)
- Gentle audio cues as alternative to haptics
- Export session data for personal reflection

## Design Decisions

### Why No Custom Durations?
To reduce decision fatigue and keep the app simple. Two well-tested presets cover most use cases without overwhelming users with options.

### Why No Task Management?
Focus on the timer itself. Task management adds complexity and can create anxiety. Other apps do this well - we focus on time.

### Why Gentle Haptics?
Aggressive vibrations create stress. Our patterns are designed to inform without startling, supporting a calm productivity flow.

### Why Auto-Cycling?
Removes the decision point between cycles. You can always pause or stop, but the default is to keep the flow going naturally.

## License

[Your License Here]

## Acknowledgments

Built with Jetpack Compose for Wear OS, following Material Design principles adapted for a calm, focused user experience.

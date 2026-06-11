# core:audio-player

Reusable Kotlin Multiplatform audio playback package.

## Responsibility
- Exposes a ready-to-use `AudioRepository` for queue-based playback orchestration.
- Keeps platform engines behind shared contracts (`AudioPlayerEngine`).
- Supports Android (`Media3 ExoPlayer`) and iOS (`AVPlayer`) implementations.

## Public API
- `AudioRepository`: queue controls, transport controls, seek/speed, and reactive state flows.
- `PlayerTrack`: portable track model for playback queues.
- `PlaybackState` and `PlaybackProgress`: lifecycle and timeline outputs.
- `initializePlatformAudio(context)`: host initialization hook for Android context wiring.

## Usage Notes
- Android hosts should call `initializePlatformAudio(applicationContext)` once before creating `AudioRepository`.
- iOS hosts do not require explicit initialization.
- Keep UI-specific presentation logic outside this module.

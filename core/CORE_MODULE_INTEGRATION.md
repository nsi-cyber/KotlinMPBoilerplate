# Core Module Integration Guide

This document is the source of truth for understanding and integrating the standalone `:core` module.

It is written for both:
- developers integrating `:core` into host apps
- LLM agents that need fast architectural context before making changes

## 1) Module Intent

`:core` is a Kotlin Multiplatform infrastructure package.  
It centralizes reusable cross-platform building blocks:

- device/platform helpers
- network stack
- presentation contracts (no concrete UI screens)
- audio player stack
- Room database stack
- DI setup (Koin)

The module is host-app agnostic and should not depend on app feature modules.

---

## 2) Source-Set Layout

- `core/src/commonMain`: shared contracts + shared business/service logic
- `core/src/androidMain`: Android actual implementations
- `core/src/iosMain`: iOS actual implementations
- `core/src/commonTest`: platform-independent tests

Rule: keep non-platform APIs in `commonMain`; keep platform APIs (`Context`, AVFoundation, Android SDK classes) in `androidMain`/`iosMain`.

---

## 3) File-by-File Map

## `core/common`

- `core/src/commonMain/kotlin/com/nsicyber/composeboilerplatform/sharedlogic/core/common/PlatformInfoProvider.kt`
  - Defines `PlatformInfoProvider` contract.
  - Exposes `expect fun platformInfoProvider()`.

- `core/src/androidMain/kotlin/com/nsicyber/composeboilerplatform/sharedlogic/core/common/PlatformInfoProvider.android.kt`
  - Android `actual` platform name provider.

- `core/src/iosMain/kotlin/com/nsicyber/composeboilerplatform/sharedlogic/core/common/PlatformInfoProvider.ios.kt`
  - iOS `actual` platform name provider.

## `core/network`

- `NetworkMonitor.kt`
  - `NetworkMonitor` interface (`isOnline()`).
  - `expect fun networkMonitor()`.

- `NetworkMonitor.android.kt`, `NetworkMonitor.ios.kt`
  - Platform actuals for one-shot network checks.

- `ConnectivityManager.kt`
  - Reactive connectivity contract (`StateFlow<Boolean>`).
  - `expect fun connectivityManager()`.
  - `expect fun initializePlatformConnectivity(context: Any? = null)`.

- `ConnectivityManager.android.kt`, `ConnectivityManager.ios.kt`
  - Platform actuals for connectivity observation.
  - Android uses host context initialization.

- `HttpClientFactory.kt`
  - Shared Ktor client configuration.
  - `expect fun platformHttpClientEngine()`.
  - `HttpClientProvider.client` singleton.

- `HttpClientFactory.android.kt`, `HttpClientFactory.ios.kt`
  - Platform engine actuals.

- `HttpClientExt.kt`
  - Network result wrappers and safe call helpers.

- `BaseRemoteDataSource.kt`
  - Reusable remote call abstraction.

- `RemoteDataSource.kt`
  - Generic GET/POST helpers that map to `NetworkResource`.

- `HttpClientExtTest.kt`, `RemoteDataSourceTest.kt`
  - Unit tests for core network mapping and datasource behavior.

## `core/presentation`

- `UiText.kt`
  - Platform-safe text abstraction (`DynamicString` / `StringResourceId`).
  - Helpers for composable and suspend resolution.

- `BaseViewModel.kt`
  - Main base ViewModel for MVI + overlay helpers in a single class.
  - Exposes state/effect/event/navigator handling directly.

- `model/StateModel.kt`
  - `StateModel<T>` for single data payload states (`loading + data`).
  - `PaginationStateModel<T>` for paged/list-based states (`loading + data + currentPage + hasMore`).
  - Designed to be embedded inside feature `STATE` models in ViewModel layer.

- `navigation/base/*`
  - `Destination.kt`: route descriptor model.
  - `NavigatorController.kt`: framework-agnostic nav controller contract.
  - `IViewNavigator.kt`: base feature navigator interface.
  - `IBaseNavigator.kt`: shared navigator behavior.

- `overlay/*`
  - `SnackbarManager`, `DialogManager`, `BottomSheetManager` contracts.
  - `OverlayLocals.kt` for CompositionLocal bindings.
  - `overlay/model/*` request and message models.

- Tests:
  - `BaseViewModelTest.kt`
  - `UiTextTest.kt`
  - `navigation/base/NavigationBaseTest.kt`

### ViewModel State Modeling Notes

When a screen state keeps list/detail payloads, prefer using `StateModel` wrappers from
`com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.model`:

- Use `StateModel<T>` for non-paginated data (for example profile/detail/home response).
- Use `PaginationStateModel<List<T>>` for lists that support page/append behavior.
- Keep the wrapper inside your screen `STATE` so loading and data visibility checks are standardized.

Example usage idea:
- `val feedState: PaginationStateModel<List<PostUi>>`
- `val profileState: StateModel<ProfileUi>`

## `core/audioplayer`

- `AudioPlayerEngine.kt`
  - Engine abstraction + `expect createAudioPlayerEngine(...)`.
  - `expect initializePlatformAudio(context)`.

- `PlaybackState.kt`, `PlaybackProgress.kt`, `PlayerTrack.kt`
  - Core player models.

- `AudioRepository.kt`
  - High-level playback orchestration (queue, next/prev, seek, speed, state flows, error flow).

- `AudioPlayerEngine.android.kt`
  - Android Media3 ExoPlayer implementation.
  - Requires one-time `initializePlatformAudio(applicationContext)`.

- `AudioPlayerEngine.ios.kt`
  - iOS AVPlayer implementation.

- `AudioRepositoryTest.kt`
  - Unit tests for queue and playback orchestration.

## `core/database`

- `AppLaunchInfoEntity.kt`
  - Table entity for first-open metadata (`app_launch_info`).

- `AppLaunchInfoDao.kt`
  - DAO for observe/get/upsert operations.

- `CoreDatabase.kt`
  - Room database definition.
  - Uses `@ConstructedBy(CoreDatabaseConstructor::class)`.

- `CoreDatabaseFactory.kt`
  - `expect class` returning a `RoomDatabase.Builder<CoreDatabase>`.

- `CoreDatabaseFactory.android.kt`
  - Android file-path based Room builder using `Context`.

- `CoreDatabaseFactory.ios.kt`
  - iOS document-directory based Room builder.

- `CoreDatabaseProvider.kt`
  - `coreDatabase(factory)` singleton creator.
  - applies bundled sqlite driver + coroutine context
  - seeds first-open row when DB is first initialized.

## `core/di`

- `CoreKoin.kt`
  - `coreModule(appContext)` provides all DI-ready core services.
  - `initCoreKoin(...)` bootstraps Koin with core + extra modules.

---

## 4) Runtime Initialization Rules

### Android

Before consuming reactive connectivity and audio services, call:

```kotlin
initializePlatformConnectivity(applicationContext)
initializePlatformAudio(applicationContext)
```

### iOS

No mandatory explicit init for connectivity/audio in the current implementation.

---

## 5) DI Contract (Koin)

`coreModule(appContext)` registers:

- `PlatformInfoProvider`
- `NetworkMonitor`
- `ConnectivityManager`
- `HttpClient`
- `RemoteDataSource`
- `AudioRepository`
- `CoreDatabaseFactory`
- `CoreDatabase`
- `AppLaunchInfoDao`

Recommended bootstrapping:

```kotlin
initCoreKoin(
    appContext = applicationContextOrNull,
    extraModules = listOf(/* host feature modules */)
)
```

---

## 6) Room Boot + Seed Behavior

On first `CoreDatabase` creation:

- `coreDatabase(factory)` builds singleton DB
- `ensureAppLaunchInfoSeeded(...)` inserts default row if missing

Seed row:
- `id = 1`
- `firstOpenedAtEpochMillis = current epoch millis`

This guarantees `app_launch_info` is never empty after first successful DB boot.

---

## 7) Host Integration Checklist

1. Include `:core` module dependency in host app.
2. Run Android-only platform initializers (`initializePlatformConnectivity`, `initializePlatformAudio`) early.
3. Start Koin with `initCoreKoin(...)`.
4. Resolve needed services from Koin (network/audio/database/platform).
5. Keep host UI/features outside `:core` to preserve portability.

---

## 8) Change Guidelines for Future LLMs

- Keep package names under `com.nsicyber.composeboilerplatform.sharedlogic.core.*`.
- Do not move platform-specific code to `commonMain`.
- If you add new reusable core services:
  - create common contract + platform actuals where needed
  - register them in `coreModule(...)`
  - document them here
- If you add Room entities:
  - include entity + DAO + database reference
  - handle schema version and migration strategy
- Preserve host-agnostic design: no feature-specific screen/state code inside `:core`.

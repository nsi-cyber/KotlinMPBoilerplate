# KMP Clean Architecture

This project follows a hybrid KMP modular strategy:

- Platform apps (`androidApp`, `iosApp`) are thin host entry points.
- `shared-ui` contains shared Compose entry and routing.
- `shared-logic` contains `core` and `feature` layers.

## Layer Boundaries

- `core:*`  
  Shared contracts and platform-backed providers used by multiple features.
  Example: `core:network` exposes synchronous `NetworkMonitor` and reactive `ConnectivityManager`.
  Example: `core:audio-player` exposes a ready-to-use `AudioRepository` with Android+iOS engines.

- `feature:*:domain`  
  Business models, repository contracts, and use cases. No platform dependencies.

- `feature:*:data`  
  Repository implementations and datasource orchestration.

- `feature:*:presentation`  
  UI state and presentation logic consumed by `shared-ui`.

- `core:presentation`  
  Shared UI contracts for global overlays such as snackbar, dialog, and bottom sheet.

## Overlay Boilerplate

- Overlay contracts and models live in `shared-logic:core:presentation`.
- Overlay implementations and app-level hosts live in `shared-ui`.
- `AppRoot` mounts one global host layer to avoid per-screen duplication.
- Feature presentation modules use `CompositionLocal` contracts, not UI implementation classes.

## Commenting Standard

- Every public class/function should include short KDoc explaining intent.
- KDoc should describe *why/role*, not restate the code line-by-line.
- Module READMEs define ownership and allowed dependency directions.

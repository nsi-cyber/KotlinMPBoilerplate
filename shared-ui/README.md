# shared-ui

Shared Compose UI entry module for Android and iOS hosts.

## Responsibility
- Own the app-level shared composable root (`AppRoot`).
- Expose iOS bridge function (`MainViewController`) for SwiftUI hosting.
- Render global overlay hosts for snackbar, dialog, and bottom sheet.
- Consume `feature` presentation APIs and `core:presentation` contracts.

## Dependency Rules
- Can depend on `shared-logic:feature:*:presentation`.
- Can depend on `shared-logic:core:presentation` for overlay contracts.
- Must not depend directly on `feature:*:data` modules.

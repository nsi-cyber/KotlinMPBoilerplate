# core/presentation

Reusable presentation contracts for global UI overlays.

## Responsibility
- Define global snackbar, dialog, and bottom sheet contracts.
- Provide shared request models used by feature presentation modules.
- Expose `CompositionLocal` contract accessors with safe no-op defaults.

## Dependency Rules
- This module should not depend on feature modules.
- UI implementation details must stay in `shared-ui`.

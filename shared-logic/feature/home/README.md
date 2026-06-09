# feature/home

Home feature modules organized by clean architecture layers.

## Modules
- `domain`: Home models, contracts, use cases.
- `data`: Home repository implementations.
- `presentation`: Home UI state and composables.

## Dependency Rules
- `domain` does not depend on `data` or `presentation`.
- `data` depends on `domain` and required `core` modules.
- `presentation` depends on `domain` and integration-facing `data` APIs.

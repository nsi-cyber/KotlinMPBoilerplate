# shared-logic

Shared business and feature logic for the multiplatform app.

## Structure
- `core/*`: cross-cutting abstractions and platform providers.
- `core/presentation`: reusable UI contracts and models shared across features.
- `core/audio-player`: platform-backed audio playback repository and engine contracts.
- `feature/*`: feature-specific modules.

## Dependency Rules
- `feature:*:domain` can depend on `core:*`.
- `feature:*:data` can depend on `feature:*:domain` and `core:*`.
- `feature:*:presentation` can depend on `feature:*:domain`, `feature:*:data`, and `core:presentation`.
- Feature modules must not depend on other feature implementation details.

# AETHELIONCS: 10-TIER TEST ARCHITECTURE & REGRESSION SPECIFICATION

============================================================
1. STRATIFIED TEST HIERARCHY
============================================================

Verification in AethelionCS enforces 10 non-interchangeable test tiers:

| Tier | Tier Name | Environment | Evidence Scope |
|---|---|---|---|
| 1 | `STATIC` | IDE / Linter | Kotlin syntax, type checks, compiler diagnostics |
| 2 | `UNIT` | JVM Host (`./gradlew test`) | Isolated helper/parser function tests with mocked inputs |
| 3 | `FIXTURE` | JVM Host (`./gradlew test`) | Parsing real saved HTML/JSON response snapshots |
| 4 | `INTEGRATION` | JVM Host (`./gradlew test`) | Multi-step provider flow (`search` → `load` → `loadLinks`) |
| 5 | `LIVE_WEB` | JVM Host / Network | Direct live HTTP requests to verify current site DOM/endpoints |
| 6 | `LOCAL_BUILD` | Local Gradle (`./gradlew make`) | Extension packaging and `.cs3` artifact generation |
| 7 | `CI` | GitHub Actions Runner | Clean remote build, compatibility checks, and manifest publishing |
| 8 | `EMULATOR` | Android Virtual Device | Extension installation in CloudStream on Android emulator |
| 9 | `REAL_DEVICE` | Physical Android Device | Extension installation in CloudStream on real Android hardware |
| 10 | `RUNTIME` | CloudStream Live App | Authoritative video playback verification (ExoPlayer media rendering) |

============================================================
2. CRITICAL REGRESSION TEST SPECIFICATIONS
============================================================

### Regression 1: Episode Identity Invariant
- **Problem in Old Project:** Episode 1 and Episode 2 resolved to the identical cached video stream.
- **Representative Test Requirement:**
  ```text
  Input:
    Episode 1 URL (e.g. ".../1-sezon-1-bolum")
    Episode 2 URL (e.g. ".../1-sezon-2-bolum")
  Procedure:
    Execute production path loadLinks(Episode 1) -> Capture Stream A
    Execute production path loadLinks(Episode 2) -> Capture Stream B
  Expected:
    Stream A URL != Stream B URL
    Stream A Host/Embed != Stream B Host/Embed
    Zero shared mutable state between invocations
  ```

### Regression 2: Platform Class Purity
- **Problem in Old Project:** Duplicate platform classes bundled inside `.cs3` caused DEX `ClassCastException`.
- **Representative Test Requirement:**
  Inspect generated `.cs3` / `classes.dex` to ensure zero classes exist under `com.lagradost.cloudstream3.*` except provider-specific classes inheriting from platform interfaces.

### Regression 3: Real Extractor Delegation
- **Problem in Old Project:** Synthetic URL generation reported false-positive passes without testing real extractors.
- **Representative Test Requirement:**
  Verify that parsed iframe URLs correctly trigger registered CloudStream `ExtractorApi` resolvers or native extractors.

# TEST HIERARCHY SPECIFICATION (AETHELIONCS VERIFICATION MODEL)

The AethelionCS verification framework defines 10 distinct, non-interchangeable tiers of validation:

| Tier | Level Name | Scope & Method | Proves | Does NOT Prove |
|---|---|---|---|---|
| 1 | `STATIC` | Linter & compilation check | Syntax & type purity | Runtime execution |
| 2 | `UNIT` | Isolated function tests with mocks | Function logic in isolation | Multi-component integration |
| 3 | `FIXTURE` | Local mock HTML / JSON parser tests | Parser selector rules on snapshots | Live site structure |
| 4 | `INTEGRATION` | Multi-component contract tests | Component interaction contracts | Live web or platform compatibility |
| 5 | `LIVE_WEB` | Live HTTP queries via research scripts | Remote endpoint availability | Target player / Android playback |
| 6 | `LOCAL_BUILD` | Local packaging (`./gradlew make`) | Extension packaging (.cs3) | Remote CI environment success |
| 7 | `CI` | Remote GitHub Actions workflow | Multi-platform build reproducibility | Target device playback |
| 8 | `EMULATOR` | Virtual target OS execution | Basic emulator compatibility | Real hardware / DRM behavior |
| 9 | `REAL_DEVICE` | Physical hardware execution | Hardware-specific compatibility | General device ecosystem |
| 10 | `RUNTIME` | Live target player execution (ExoPlayer) | Authentic end-to-end user playback | N/A (Authoritative proof) |

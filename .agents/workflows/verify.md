# WORKFLOW: VERIFY

1. Execute `./gradlew test` (Local Unit & Regression).
2. Execute `./gradlew make` (Local Packaging).
3. Validate DEX classes to prevent platform duplicates.
4. Test live device/emulator playback via ADB logcat.
5. Record outcomes in `docs/EVIDENCE_LOG.md` and update `docs/TRUTH_LEDGER.md`.

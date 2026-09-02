---
name: release-audit
description: Audits codebases against strict pre-release quality, CI, packaging, and target runtime criteria.
---

# RELEASE AUDIT SKILL

## Release Audit Checklist
- [ ] `LOCAL_TEST`: All unit and parser tests pass (`./gradlew test`).
- [ ] `LOCAL_BUILD`: Extension packaging succeeds without warnings (`./gradlew make`).
- [ ] `PACKAGE_PURITY`: Generated `.cs3` / `classes.dex` contains zero duplicated platform classes.
- [ ] `CI_WORKFLOW`: Remote GitHub Actions build completes with status `success`.
- [ ] `TARGET_RUNTIME`: Validated on the target runtime environment (e.g. Android Emulator, Real Device, or Android TV) via clean logcat / runtime traces.
- [ ] `DOCUMENTATION`: Truth Ledger and Evidence Log updated with fresh commit hash and timestamps.

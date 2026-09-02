---
name: verification
description: Measures and records empirical verification evidence for code and runtime claims.
---

# VERIFICATION SKILL

## Purpose
Enforces the validation of claims against actual command execution, log evidence, or runtime tests.

## Verification Template
```text
CLAIM: [Description of claim]
ENVIRONMENT: [Local Host / CI Runner / Emulator / Real Device / Target Runtime]
INPUT: [Test input parameters]
PROCEDURE: [Exact command line or test method executed]
EXPECTED: [Predicted outcome]
ACTUAL: [Observed outcome]
EVIDENCE: [Log excerpt, exit code, or command stdout]
STATUS: [VERIFIED / FAILED / UNKNOWN / NOT_TESTED / NOT_VERIFIED / BLOCKED / CONFLICTING_EVIDENCE / UNCONFIRMED / STALE]
EVIDENCE_TYPE: [STATIC / UNIT / FIXTURE / INTEGRATION / LIVE_WEB / LOCAL_BUILD / CI / EMULATOR / REAL_DEVICE / RUNTIME]
TEST_LEVEL: [1-10]
DATE: [ISO 8601 Timestamp]
COMMIT: [Git SHA]
```

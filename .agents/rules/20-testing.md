# 20-TESTING: TEST HIERARCHY & DISCIPLINE

1. HIERARCHY OF TESTING (AETHELIONCS MODEL)
   Tests are stratified into 10 non-interchangeable tiers:
   - Tier 1: `STATIC` (Compilation & type safety)
   - Tier 2: `UNIT` (Isolated function tests)
   - Tier 3: `FIXTURE` (Mock HTML / JSON parser tests)
   - Tier 4: `INTEGRATION` (Multi-module contract tests)
   - Tier 5: `LIVE_WEB` (Direct HTTP queries)
   - Tier 6: `LOCAL_BUILD` (Local packaging / .cs3 generation)
   - Tier 7: `CI` (Remote Continuous Integration build)
   - Tier 8: `EMULATOR` (Virtual target OS environment)
   - Tier 9: `REAL_DEVICE` (Physical target hardware)
   - Tier 10: `RUNTIME` (Authoritative live target application playback)

2. REPRESENTATIVENESS RULE
   A unit test asserting hardcoded string inequality (`ep1Url != ep2Url`) or synthetic M3U8 parsing proves unit logic only. It DOES NOT prove that the provider produces valid playback links in the target runtime.

3. REGRESSION TEST VALIDITY RULE
   A regression test is valid ONLY when it reproduces the original failure condition or an equivalent failure mechanism. "Test exists" is not proof of verification.
   Required fields for regression tests:
   - `ORIGINAL_FAILURE`: Description of the bug/failure mechanism.
   - `TEST_REPRODUCES_FAILURE`: YES/NO (Confirms test would fail on pre-fix code).
   - `SAME_PRODUCTION_PATH`: YES/NO (Confirms test invokes production execution path).
   If `TEST_REPRODUCES_FAILURE = NO`, the regression test does NOT verify the original defect.

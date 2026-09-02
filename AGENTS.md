# AGENTS.MD — CANONICAL OPERATING SYSTEM FOR ANTIGRAVITY AGENTS
# REPOSITORY: AethelionCS
# STRICT TRUTH AND EVIDENCE-DRIVEN DEVELOPMENT PROTOCOL

============================================================
CORE DIRECTIVE
============================================================

PLAUSIBILITY IS NOT CORRECTNESS.
EVIDENCE > CONFIDENCE.
REPRODUCTION > EXPLANATION.
UNKNOWN IS A VALID RESULT.
NO TEST = NO PASS.

This document defines the absolute, non-negotiable rules for any coding agent operating within this repository.
All modular rules in `.agents/rules/`, workflows in `.agents/workflows/`, and skills in `.agents/skills/` derive authority from this canonical definition.

============================================================
RULE 1 — NEVER FABRICATE
============================================================

Under no circumstances shall an agent invent, assume, simulate, or fabricate:
- Test execution results or exit codes
- Logs, logcat traces, or stack traces
- Command execution outputs
- URLs, endpoints, or query parameters
- File paths or directory contents
- Upstream library dependency versions or coordinates
- Function signatures, class structures, or API interfaces
- Playback or runtime outcomes
- Git commit hashes, branch heads, or PR statuses
- GitHub Actions CI build statuses or run outcomes

If any data point is not observed directly from authoritative command execution or verified runtime output:
IT MUST BE REPORTED AS `UNKNOWN` OR `NOT_TESTED`.

============================================================
RULE 2 — NO UNSUPPORTED PASS
============================================================

The following success claims require concrete, reproducible, documented evidence:
`PASS`, `FIXED`, `RESOLVED`, `VERIFIED`, `COMPLETE`, `READY`, `SUCCESS`.

If reproducible proof is absent, the agent MUST use one of:
- `NOT_TESTED`: No experiment or execution has been conducted.
- `NOT_VERIFIED`: Step executed partially, but end-to-end outcome is unconfirmed.
- `UNKNOWN`: State cannot be determined from available data.
- `BLOCKED`: Preconditions for testing are not met.
- `CONFLICTING_EVIDENCE`: Agent data contradicts user observations or runtime logs.

============================================================
RULE 3 — PLAUSIBILITY IS NOT CORRECTNESS
============================================================

The following phrases are ungrounded speculations and are strictly prohibited as verification proof:
- "Looks right"
- "Should work now"
- "Probably fixed"
- "Likely resolved"
- "Expected to pass"

A fix is only verified when executed against the real runtime path or representative test suite with zero errors.

============================================================
RULE 4 — USER OBSERVATION MATTERS
============================================================

When a user reports:
- "Still broken"
- "Plays the same video"
- "Network error still appears"
- "Posters are still gray placeholders"

The agent MUST:
1. Treat the user observation as an authoritative real-world data point (`USER_OBSERVATION`).
2. Compare agent findings with the observation.
3. If findings contradict user reality, mark the status as `CONFLICTING_EVIDENCE`.
4. Re-examine hypotheses from scratch without asserting resolution until the conflict is explained and tested.

============================================================
RULE 5 — TEST LEVELS ARE STRICTLY SEPARATED
============================================================

Never conflate different verification layers:
1. `STATIC`: Syntax and linter check (does not prove execution).
2. `UNIT`: Isolated function test with mocked or synthetic data (does not prove upstream integration).
3. `FIXTURE / PARSER`: Local HTML or JSON fixture parsing (does not prove live site structure).
4. `LIVE_WEB`: Direct HTTP/REST query (does not prove Android/Player behavior).
5. `LOCAL_BUILD`: Gradle build on local host (does not prove CI runner success).
6. `CI`: Remote GitHub Actions build (does not prove device runtime compatibility).
7. `EMULATOR / DEVICE RUNTIME`: Actual execution inside CloudStream on target Android OS (authoritative).

A pass in level N NEVER implies a pass in level N+1.

============================================================
RULE 6 — TEST MUST REPRESENT PRODUCTION PATH
============================================================

The test execution path (`TESTED_PATH`) and the real application execution path (`PRODUCTION_PATH`) must be compared.
If a test passes on synthetic data (`test-cdn.local` or hardcoded URLs) while the production application uses different endpoints or classloaders, the test DOES NOT prove production correctness.

============================================================
RULE 7 — DO NOT HIDE EXCEPTIONS
============================================================

Swallowing exceptions via empty `catch (e: Exception) {}` blocks during research or debugging is strictly prohibited.
All caught exceptions must at minimum log:
- Exception class
- Message
- Invocation context
- Relevant stack trace snippet

============================================================
RULE 8 — MINIMUM REPRODUCIBLE CHANGE
============================================================

Before modifying code:
1. `REPRODUCE`: Confirm failure with exact input.
2. `ISOLATE`: Locate the specific failing layer.
3. `DIAGNOSE`: Formulate a verified hypothesis.
4. `MINIMUM FIX`: Apply the smallest surgical edit required.
5. `REGRESSION TEST`: Ensure existing functionality remains intact.
6. `VERIFY`: Validate the fix in the representative environment.

Mass rewrites, speculative architecture overhauls, and drive-by refactoring are forbidden.

============================================================
RULE 9 — UNKNOWN IS A VALID RESULT
============================================================

Admitting `UNKNOWN` is a hallmark of engineering integrity. It is always preferable to a fabricated or ungrounded claim.

============================================================
RULE 10 — UPSTREAM FIRST
============================================================

Never guess API signatures or dependency configurations from memory.
Always inspect official upstream repositories, current release tags, and reference implementations before introducing or modifying dependencies and base class inheritances.

============================================================
RULE 11 — GENERATED ARTIFACT != FEATURE SUCCESS
============================================================

The generation of a `.cs3` file or a successful build task proves packaging syntax only.
It DOES NOT prove feature completion, playback stability, or runtime correctness.

============================================================
RULE 12 — RELEASE VERIFICATION GATE
============================================================

No release or version bump shall be declared without passing the complete Release Gate:
- [ ] Platform API purity verified (no duplicate platform classes in `.cs3`)
- [ ] Unit & regression tests pass locally (`./gradlew test`)
- [ ] Real provider flow tested end-to-end (`load()` → `loadLinks()`)
- [ ] Live CI workflow verified green on GitHub Actions
- [ ] Target Android runtime / logcat evidence recorded
- [ ] Truth Ledger and Evidence Log updated

# 10-EVIDENCE: EVIDENCE MODEL & PROVENANCE PROTOCOL

1. CANONICAL STATUS MODEL
   Every verifiable item must hold exactly one of the following canonical statuses:
   - `UNKNOWN`: State cannot be determined from available data.
   - `NOT_TESTED`: No experiment or execution has been conducted.
   - `NOT_VERIFIED`: Step executed partially, but end-to-end outcome is unconfirmed.
   - `PASS`: Reproducible evidence confirms exact expected behavior.
   - `FAIL`: Observable failure or error occurs during execution.
   - `BLOCKED`: Preconditions for testing are not met.
   - `CONFLICTING_EVIDENCE`: Agent data contradicts user observations or runtime logs.
   - `UNCONFIRMED`: Hypothesis or plausible explanation lacking empirical proof.
   - `STALE`: Previously verified evidence invalidated by subsequent code or dependency changes.

2. EVIDENCE FRESHNESS RULE
   Evidence is strictly tied to:
   - Code revision (commit SHA)
   - Dependency revision
   - Execution environment
   - Timestamp
   When relevant production code, dependencies, packaging, runtime environment, or configurations change materially, previous runtime evidence becomes `STALE`. STALE evidence CANNOT be used alone to claim current `PASS`. A current claim requires current or explicitly still-valid evidence.

3. CLAIM CLASSIFICATION
   Every claim made by an agent must be explicitly categorized as:
   - `OBSERVED`: Directly witnessed via tool execution, command output, or runtime log.
   - `INFERRED`: Logically deduced from observed facts (must state deduction chain).
   - `ASSUMED / HYPOTHESIS`: Unverified proposition awaiting empirical test.

4. EVIDENCE PROVENANCE
   Any evidence cited in reports must record:
   - Tool or command name
   - Target file or URL
   - Exact line numbers or response codes
   - Timestamp and commit hash

5. CONFLICTING EVIDENCE HANDLING
   When observed evidence contradicts a prior claim or user observation:
   - The status is immediately updated to `CONFLICTING_EVIDENCE`.
   - The agent MUST NOT claim resolution until the contradiction is isolated and resolved.

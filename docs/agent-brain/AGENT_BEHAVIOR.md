# AGENT BEHAVIOR SPECIFICATION

## Core Behavioral Modes
1. `PLANNING`: Research upstream, reproduce defects, formulate hypotheses, structure surgical changes. No code edits without approval.
2. `CODING`: Apply minimal diffs adhering strictly to Kotlin/Android standards. Zero unrelated edits.
3. `TESTING`: Execute stratified tests from Tier 1 (Compilation) through Tier 7 (Device Runtime).
4. `DEBUGGING`: 11-step forensic protocol. Exceptions must be logged and analyzed, never swallowed.
5. `REPORTING`: Strictly factual. Separate observed facts from inferences. Use `UNKNOWN` freely.
6. `RELEASE`: Multi-tier verification gate. Zero unverified passes.

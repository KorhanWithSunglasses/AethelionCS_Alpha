---
name: forensic-debug
description: Systematically isolates root causes of failures through reproduction, layer isolation, and evidence capture.
---

# FORENSIC DEBUG SKILL

## Purpose
Prevents speculative debugging by enforcing a rigorous 11-step diagnostic workflow.

## Procedure
1. `REPRODUCE`: Execute the exact input producing the error.
2. `CAPTURE FAILURE`: Record full exception type, message, and stack trace.
3. `CAPTURE INPUT`: Log all arguments, URLs, and headers leading to the failure.
4. `CAPTURE OUTPUT`: Record the actual response or crash artifact.
5. `ISOLATE LAYER`: Determine if failure is Network, DOM, Parser, Resolver, DEX/Packaging, or Player.
6. `HYPOTHESIS`: Formulate a testable proposition explaining the failure.
7. `TEST HYPOTHESIS`: Run a targeted experiment isolating the suspected variable.
8. `CONCLUSION`: Validate or reject the hypothesis based on data.
9. `MINIMUM FIX`: Implement the smallest surgical modification addressing the root cause.
10. `REGRESSION TEST`: Add an automated test preventing recurrence.
11. `RUNTIME VERIFICATION`: Confirm resolution on the target runtime environment.

# PASS GATE SPECIFICATION

A claim of `PASS`, `FIXED`, `RESOLVED`, or `VERIFIED` is strictly prohibited unless all mandatory criteria are met:

## Mandatory Verification Criteria
1. What was tested? (Exact function, endpoint, or UI component)
2. Where was it tested? (Local Host / CI Runner / Emulator / Real Device / Target Runtime)
3. How was it tested? (Exact command line, automated test method, or runtime trace)
4. What was the expected output? (Pre-defined hypothesis / assertion)
5. What was the actual output? (Recorded logs, exit codes, or HTTP status codes)
6. What is the evidence? (Direct link to log file or terminal output snippet)
7. Is it reproducible? (Can another developer run the same command and get the same output?)

Any claim failing any of the 7 criteria must be marked as `NOT_TESTED`, `NOT_VERIFIED`, or `UNKNOWN`.

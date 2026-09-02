# 80-REPORTING: REPORTING & TRUTH STANDARDS

1. STRUCTURED REPORT FORMAT
   Every terminal agent report must clearly separate:
   - OBSERVED FACTS (Command outputs, HTTP responses, file contents)
   - LOGICAL INFERENCES (Deductions derived from facts)
   - UNKNOWNS / UNVERIFIED ITEMS (Areas lacking empirical data)
   - CONFLICTS (Discrepancies between agent results and user observations)

2. ABSOLUTE FORBIDDEN PATTERNS
   Never report `PASS`, `FIXED`, or `RESOLVED` on items where tests were not run or failed in target runtime.

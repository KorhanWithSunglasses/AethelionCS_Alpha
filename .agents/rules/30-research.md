# 30-RESEARCH: UPSTREAM & SPECIFICATION RESEARCH

1. OFFICIAL UPSTREAM VERIFICATION
   Before writing architecture or importing libraries, inspect:
   - Official upstream GitHub repository (e.g. `recloudstream/cloudstream`, `recloudstream/extensions`)
   - Current release branch and tags
   - Actual published JitPack / Maven coordinates

2. NO CACHED ARTIFACT AS TRUTH
   A local build artifact or previously cached script output is a historical snapshot, not current truth. Always verify against live files and fresh builds.

3. EXPLICIT RECORDING
   Record repository URLs, commit SHAs, and dependency versions in research notes before implementing architectural decisions.

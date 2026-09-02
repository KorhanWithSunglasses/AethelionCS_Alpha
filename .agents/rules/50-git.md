# 50-GIT: GIT & VERSION CONTROL HYGIENE

1. CLEAN COMMITS
   Each commit must contain a focused, cohesive change with a descriptive message prefix (`feat:`, `fix:`, `refactor:`, `test:`, `ci:`).

2. DIFF AUDITING
   Always run `git diff` and `git status` before staging and committing. Never commit unexpected temp files, build caches, or credential files.

3. BRANCH DISCIPLINE
   - `master`: Main development and source branch.
   - `builds`: Dedicated deployment branch containing only packaged `.cs3` and `plugins.json`.
   - Never force-push to `master` without explicit user instruction.

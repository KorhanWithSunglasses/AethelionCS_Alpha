# WORKFLOW: IMPLEMENT

1. **PRECONDITION**: Verify that `PLAN_APPROVED = YES`. If only `PLAN_EXISTS` without explicit approval, HALT execution and request user confirmation.
2. Follow the approved change plan strictly.
3. Apply modifications to target files without unrelated edits.
4. Review git diff (`git diff`) to ensure zero accidental changes.
5. Execute local compilation and unit tests.

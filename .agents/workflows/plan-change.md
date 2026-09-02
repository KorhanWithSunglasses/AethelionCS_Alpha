# WORKFLOW: PLAN CHANGE

1. Read canonical constraints in `AGENTS.md`.
2. Reproduce the problem using exact input.
3. Isolate the failing component layer.
4. Draft the minimal surgical edit plan.
5. Define automated regression tests and rollback steps.
6. **APPROVAL GATE**: Mark state as `PLAN_EXISTS`. Present the plan to the user. Do NOT modify code until `PLAN_APPROVED = YES` is explicitly confirmed.

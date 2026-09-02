# WORKFLOW: RELEASE

1. Complete all steps of `verify.md`.
2. Push commits to `master` and monitor GitHub Actions CI to completion.
3. Validate packaging and manifest artifacts on the `builds` branch.
4. Perform post-deployment smoke test on the declared `TARGET_RUNTIME` (e.g. Emulator, Real Device, or Target Application).
5. Record release evidence in `docs/TRUTH_LEDGER.md`.

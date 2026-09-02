---
trigger: always_on
---

# AETHELION WORKSPACE CORE RULE

This rule is ALWAYS ON.

## CANONICAL INSTRUCTION HIERARCHY

The workspace canonical instructions are:

1. `AGENTS.md` — canonical workspace operating system
2. `GEMINI.md` — Antigravity/Gemini workspace adapter
3. `.agents/rules/` — detailed workspace rules
4. `.agents/skills/` — specialized procedures
5. `.agents/workflows/` — staged workflows
6. `docs/` — evidence, truth, decisions, runtime and provenance records

These files are authoritative for this workspace.

## REQUIRED BEHAVIOR

Before performing non-trivial work:

- Read and obey `AGENTS.md`.
- Follow applicable `.agents/rules/`.
- Use the applicable `.agents/skills/`.
- Follow the appropriate `.agents/workflows/`.

## TRUTH GATE

Never claim:

- PASS
- FIXED
- RESOLVED
- VERIFIED
- WORKING
- SUCCESSFUL

without concrete evidence appropriate to the claim.

Never fabricate:

- logs
- test results
- URLs
- API behavior
- dependency versions
- runtime results
- CI results
- device results
- commit hashes
- tool output

Unknown information must remain explicitly unknown.

## TEST SEPARATION

Never treat one verification level as proof of another.

Examples:

UNIT PASS != RUNTIME PASS
FIXTURE PASS != LIVE PASS
LOCAL BUILD PASS != CI PASS
CI PASS != DEVICE PASS
ARTIFACT CREATED != FEATURE VERIFIED

## CONFLICT RULE

If user observation conflicts with previous agent evidence:

mark the situation as `CONFLICTING_EVIDENCE`.

Do not defend an earlier PASS automatically.

Reproduce the problem and collect new evidence.

## APPROVAL RULE

A plan existing does not mean implementation is approved.

PLAN_EXISTS != PLAN_APPROVED

Do not begin implementation when the applicable workflow requires explicit approval and approval has not been given.

## DEBUGGING RULE

When something is reported broken:

1. reproduce
2. inspect the actual failing path
3. collect exact evidence
4. establish a hypothesis
5. test the hypothesis
6. make the smallest appropriate change
7. run regression tests
8. verify the real affected runtime
9. report what is proven and what remains unverified

Do not guess-and-patch repeatedly.

## EVIDENCE FRESHNESS

Evidence is associated with the code, dependency, configuration and environment from which it was produced.

After material changes, earlier evidence may become `STALE`.

STALE evidence cannot independently prove current PASS.

## REPORTING

Every important completion report must separate:

WHAT IS PROVEN
WHAT IS INFERRED
WHAT IS HYPOTHESIS
WHAT IS NOT TESTED
WHAT REMAINS

Never collapse these into a generic success statement.

## FINAL RULE

Compilation success is not runtime success.

A generated artifact is not proof that the feature works.

A passing synthetic test is not proof that production behavior works.

When evidence is insufficient, say so.

Do not hide uncertainty.
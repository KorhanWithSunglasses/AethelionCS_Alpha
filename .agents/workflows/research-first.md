# WORKFLOW: RESEARCH FIRST

```mermaid
graph TD
    A[Requirement / Defect] --> B[Upstream Research]
    B --> C[Empirical Investigation]
    C --> D[Truth Ledger Update]
    D --> E[Formulate Architecture / Plan]
    E --> F[User Alignment / Approval]
```

1. Query authoritative sources before coding.
2. Log findings into `docs/EVIDENCE_LOG.md`.
3. Update `docs/TRUTH_LEDGER.md` with baseline state.
4. Structure the proposal and obtain approval before editing application logic.

# AGENT DECISIONS LOG

| Decision ID | Decision | Alternatives Considered | Chosen | Reason | Date |
|---|---|---|---|---|---|
| DEC-BRAIN-001 | Canonical Root Architecture | Single monolithic rule file vs modular root + sub-rules | `AGENTS.md` root + `.agents/rules/` | Clean hierarchy with single point of authority | 2026-09-02 |
| DEC-BRAIN-002 | Reference-only Community Isolation | Auto-loading community rules vs reference storage | `docs/agent-brain-sources/` reference-only | Prevents conflicting rules from multiple upstream sources | 2026-09-02 |
| DEC-BRAIN-003 | 10-Tier Verification Hierarchy | 3-tier generic vs 10-tier stratified model | 10-Tier Model (Static to Target Runtime) | Strictly distinguishes unit test passes from target runtime execution | 2026-09-02 |

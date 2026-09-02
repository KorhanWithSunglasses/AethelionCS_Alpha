# AGENT DECISIONS LOG

| Decision ID | Decision | Alternatives Considered | Chosen | Reason | Date |
|---|---|---|---|---|---|
| DEC-BRAIN-001 | Canonical Root Architecture | Single monolithic rule file vs modular root + sub-rules | `AGENTS.md` root + `.agents/rules/` | Clean hierarchy with single point of authority | 2026-09-02 |
| DEC-BRAIN-002 | Reference-only Community Isolation | Auto-loading community rules vs reference storage | `docs/agent-brain-sources/` reference-only | Prevents conflicting rules from multiple upstream sources | 2026-09-02 |
| DEC-BRAIN-003 | 10-Tier Verification Hierarchy | 3-tier generic vs 10-tier stratified model | 10-Tier Model (Static to Target Runtime) | Strictly distinguishes unit test passes from target runtime execution | 2026-09-02 |
| DEC-CS-001 | Dynamic Server Discovery via DOM | Hardcoded server names (VidMoly, Odnok) vs dynamic `<option>` parsing | Dynamic Jsoup `<select.woca-linkpages-dd option>` parsing | Future-proof against server changes, additions, and renamings | 2026-09-02 |
| DEC-CS-002 | Base URL Routing for DBX Pro | Forcing `/1/` suffix vs base episode URL | Base URL without `/1/` | Live site evidence shows `/1/` redirects to base URL | 2026-09-02 |
| DEC-CS-003 | Stateless Episode Identity | Companion object caching vs immutable `data` parameter | Strictly immutable `Episode.data` in `loadLinks` | Eliminates cross-episode pollution ("same video playing for multiple episodes") | 2026-09-02 |
| DEC-CS-004 | Native Extractor Delegation | Re-implementing custom decryptors vs upstream `loadExtractor` | `loadExtractor` with fallback | Leverages core CloudStream extractors (VidMoly, Odnoklassniki) | 2026-09-02 |

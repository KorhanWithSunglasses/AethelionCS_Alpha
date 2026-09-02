# AGENT BRAIN FILE MAP & CATALOG

This document indexes all operational files of the Antigravity Agent Brain for AethelionCS.

> [!NOTE]
> All priority levels listed below denote the repository's **INTENDED_PRIORITY** design model. They define the intended behavioral authority designed for this workspace.

---

### 1. Root Canonical & Adapters

#### AGENTS.md
- **FILE:** `AGENTS.md`
- **CATEGORY:** AGENTS
- **PURPOSE:** Highest-precedence canonical operating system definition and immutable truth rules.
- **LOAD_ROLE:** Canonical Root Instruction.
- **WHEN_USED:** Active in all agent conversations and turns.
- **SOURCE:** PROJECT-DESIGNED (Synthesized from Community Best Practices)
- **SOURCE_URL:** N/A (Canonical Workspace Root)
- **DEPENDENCIES:** None
- **INTENDED_PRIORITY:** 1 (Highest / Designed to override all lower-level instructions)
- **OVERRIDES:** Any conflicting guideline in subdirectories or prompts.
- **NOTES:** Enforces non-negotiable anti-fabrication, evidence requirements, and test hierarchy.

#### GEMINI.md
- **FILE:** `GEMINI.md`
- **CATEGORY:** GEMINI
- **PURPOSE:** Antigravity and Gemini environment adapter directing execution flow to AGENTS.md.
- **LOAD_ROLE:** Environment Adapter.
- **WHEN_USED:** System initialization and rule resolution.
- **SOURCE:** ANTIGRAVITY
- **SOURCE_URL:** N/A
- **DEPENDENCIES:** `AGENTS.md`
- **INTENDED_PRIORITY:** 2
- **OVERRIDES:** Default unconstrained agent behavior.
- **NOTES:** References canonical rules without duplicating rule text.

---

### 2. Modular Rules (.agents/rules/)

#### 00-core-truth.md
- **FILE:** `.agents/rules/00-core-truth.md`
- **CATEGORY:** RULE
- **PURPOSE:** Core truth principles (truth over confidence, evidence over intuition).
- **LOAD_ROLE:** Fundamental Truth Guard.
- **WHEN_USED:** Throughout all reasoning and claiming steps.
- **SOURCE:** COMMUNITY (agentsmd/agents.md + awesome-agents-md)
- **SOURCE_URL:** `https://github.com/agentsmd/agents.md`
- **DEPENDENCIES:** `AGENTS.md`
- **INTENDED_PRIORITY:** 3

#### 10-evidence.md
- **FILE:** `.agents/rules/10-evidence.md`
- **CATEGORY:** RULE
- **PURPOSE:** Classification of claims (OBSERVED, INFERRED, ASSUMED) and provenance logging.
- **LOAD_ROLE:** Evidence Auditor.
- **WHEN_USED:** When generating outputs, making claims, or updating Truth Ledger.
- **SOURCE:** COMMUNITY (agents-standard + coding-agent-rules)
- **SOURCE_URL:** `https://github.com/nbiish/agents-standard`
- **DEPENDENCIES:** `00-core-truth.md`
- **INTENDED_PRIORITY:** 3

#### 20-testing.md
- **FILE:** `.agents/rules/20-testing.md`
- **CATEGORY:** RULE
- **PURPOSE:** Enforces strict multi-tier test hierarchy and test representativeness.
- **LOAD_ROLE:** Test Tier Enforcement.
- **WHEN_USED:** During test execution, verification, and regression checking.
- **SOURCE:** COMMUNITY (FerroxLabs/agents-md + agents-standard)
- **SOURCE_URL:** `https://github.com/FerroxLabs/agents-md`
- **DEPENDENCIES:** `AGENTS.md`
- **INTENDED_PRIORITY:** 3

#### 30-research.md
- **FILE:** `.agents/rules/30-research.md`
- **CATEGORY:** RULE
- **PURPOSE:** Regulates research against official upstream sources and prevents cached truth fallacies.
- **LOAD_ROLE:** Research Protocol.
- **WHEN_USED:** Prior to architecture formulation and dependency adoption.
- **SOURCE:** PROJECT-DESIGNED
- **SOURCE_URL:** N/A
- **DEPENDENCIES:** `AGENTS.md`
- **INTENDED_PRIORITY:** 3

#### 40-debugging.md
- **FILE:** `.agents/rules/40-debugging.md`
- **CATEGORY:** RULE
- **PURPOSE:** 11-step forensic debugging protocol and prohibition of exception swallowing.
- **LOAD_ROLE:** Debugging Discipline.
- **WHEN_USED:** During failure diagnosis and troubleshooting.
- **SOURCE:** COMMUNITY (cskwork/coding-agent-rules)
- **SOURCE_URL:** `https://github.com/cskwork/coding-agent-rules`
- **DEPENDENCIES:** `AGENTS.md`
- **INTENDED_PRIORITY:** 3

#### 50-git.md
- **FILE:** `.agents/rules/50-git.md`
- **CATEGORY:** RULE
- **PURPOSE:** Git branch discipline, commit cleanliness, and diff auditing.
- **LOAD_ROLE:** Version Control Hygiene.
- **WHEN_USED:** During staging, committing, and branching.
- **SOURCE:** COMMUNITY (awesome-cursorrules)
- **SOURCE_URL:** `https://github.com/PatrickJS/awesome-cursorrules`
- **DEPENDENCIES:** `AGENTS.md`
- **INTENDED_PRIORITY:** 3

#### 60-security.md
- **FILE:** `.agents/rules/60-security.md`
- **CATEGORY:** RULE
- **PURPOSE:** Secret prevention, credential leakage protection, and clean archiving.
- **LOAD_ROLE:** Security & Credential Guard.
- **WHEN_USED:** File creation, logging, archiving, and tool invocation.
- **SOURCE:** COMMUNITY (awesome-cursorrules)
- **SOURCE_URL:** `https://github.com/PatrickJS/awesome-cursorrules`
- **DEPENDENCIES:** `AGENTS.md`
- **INTENDED_PRIORITY:** 3

#### 70-change-control.md
- **FILE:** `.agents/rules/70-change-control.md`
- **CATEGORY:** RULE
- **PURPOSE:** Scope boundary enforcement, minimal change requirement, and rollback planning.
- **LOAD_ROLE:** Change Control Gate.
- **WHEN_USED:** Planning and implementing modifications.
- **SOURCE:** COMMUNITY (FerroxLabs/agents-md)
- **SOURCE_URL:** `https://github.com/FerroxLabs/agents-md`
- **DEPENDENCIES:** `AGENTS.md`
- **INTENDED_PRIORITY:** 3

#### 80-reporting.md
- **FILE:** `.agents/rules/80-reporting.md`
- **CATEGORY:** RULE
- **PURPOSE:** Structured reporting, separation of facts from inference, forbidden pass phrases.
- **LOAD_ROLE:** Report Formatter.
- **WHEN_USED:** Compiling terminal reports and user responses.
- **SOURCE:** PROJECT-DESIGNED
- **SOURCE_URL:** N/A
- **DEPENDENCIES:** `10-evidence.md`
- **INTENDED_PRIORITY:** 3

---

### 3. Specialized Skills (.agents/skills/)

#### verification/SKILL.md
- **FILE:** `.agents/skills/verification/SKILL.md`
- **CATEGORY:** SKILL
- **PURPOSE:** Measures and records empirical verification evidence against claims.
- **LOAD_ROLE:** Verification Skill.
- **WHEN_USED:** On-demand claim audit.
- **SOURCE:** COMMUNITY (JayRHa/AgentSkills)
- **SOURCE_URL:** `https://github.com/JayRHa/AgentSkills`
- **DEPENDENCIES:** `docs/EVIDENCE_LOG.md`, `docs/TRUTH_LEDGER.md`
- **INTENDED_PRIORITY:** 4

#### forensic-debug/SKILL.md
- **FILE:** `.agents/skills/forensic-debug/SKILL.md`
- **CATEGORY:** SKILL
- **PURPOSE:** Systematically isolates root causes of failures.
- **LOAD_ROLE:** Diagnostic Skill.
- **WHEN_USED:** On bug investigation.
- **SOURCE:** COMMUNITY (cskwork/coding-agent-rules)
- **SOURCE_URL:** `https://github.com/cskwork/coding-agent-rules`
- **DEPENDENCIES:** `40-debugging.md`
- **INTENDED_PRIORITY:** 4

#### upstream-research/SKILL.md
- **FILE:** `.agents/skills/upstream-research/SKILL.md`
- **CATEGORY:** SKILL
- **PURPOSE:** Verifies external APIs and dependency coordinates.
- **LOAD_ROLE:** Research Skill.
- **WHEN_USED:** Prior to dependency or interface integration.
- **SOURCE:** PROJECT-DESIGNED
- **SOURCE_URL:** N/A
- **DEPENDENCIES:** `30-research.md`
- **INTENDED_PRIORITY:** 4

#### change-planning/SKILL.md
- **FILE:** `.agents/skills/change-planning/SKILL.md`
- **CATEGORY:** SKILL
- **PURPOSE:** Structures proposed code changes with problem definitions, risk analysis, and rollback plans.
- **LOAD_ROLE:** Planning Skill.
- **WHEN_USED:** Creating implementation plans.
- **SOURCE:** COMMUNITY (FerroxLabs/agents-md)
- **SOURCE_URL:** `https://github.com/FerroxLabs/agents-md`
- **DEPENDENCIES:** `70-change-control.md`
- **INTENDED_PRIORITY:** 4

#### release-audit/SKILL.md
- **FILE:** `.agents/skills/release-audit/SKILL.md`
- **CATEGORY:** SKILL
- **PURPOSE:** Audits codebase against release gate checklists across target runtimes.
- **LOAD_ROLE:** Release Gatekeeper.
- **WHEN_USED:** Prior to version bump and release.
- **SOURCE:** COMMUNITY (eugeniughelbur/agents-md)
- **SOURCE_URL:** `https://github.com/eugeniughelbur/agents-md`
- **DEPENDENCIES:** `docs/agent-brain/PASS_GATE.md`
- **INTENDED_PRIORITY:** 4

---

### 4. Workflows (.agents/workflows/)

- `research-first.md`: Sequential guide from research to approval.
- `plan-change.md`: Sequential guide from reproduction to minimum surgical plan.
- `implement.md`: Execution sequence for approved plans (enforcing PLAN_APPROVED gate).
- `verify.md`: Verification procedure across all tiers.
- `release.md`: End-to-end release gate workflow.

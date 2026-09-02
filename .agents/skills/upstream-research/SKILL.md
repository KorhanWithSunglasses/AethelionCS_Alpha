---
name: upstream-research
description: Verifies external APIs, upstream repository interfaces, and dependency coordinates against official sources.
---

# UPSTREAM RESEARCH SKILL

## Purpose
Ensures plugin implementations conform strictly to upstream CloudStream specifications without fabricated interfaces.

## Research Protocol
1. Query official upstream GitHub repositories (`recloudstream/cloudstream`, `recloudstream/extensions`).
2. Inspect exact source files for class hierarchies, method signatures, and annotations.
3. Validate JitPack / Maven build logs for actual published artifact coordinates.
4. Document the exact commit SHA or release tag serving as the baseline.

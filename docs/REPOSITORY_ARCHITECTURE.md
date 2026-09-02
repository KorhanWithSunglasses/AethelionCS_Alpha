# AETHELIONCS: REPOSITORY STRUCTURE & MULTI-PROVIDER ARCHITECTURE

============================================================
1. REPOSITORY LAYOUT
============================================================

```text
AethelionCS/
├── AGENTS.md                          # Canonical operating system & truth rules
├── GEMINI.md                          # Antigravity adapter & directives
├── .agents/                           # Rules, skills, and workflows
├── docs/                              # Evidence, truth ledger, and architecture records
│   ├── ARCHITECTURE_RESEARCH.md
│   ├── UPSTREAM_COMPATIBILITY.md
│   ├── ACTIVE_PROVIDER_PATTERNS.md
│   ├── DIZIBOX_RESEARCH.md
│   ├── TEST_ARCHITECTURE.md
│   ├── DEPENDENCY_MATRIX.md
│   └── REPOSITORY_ARCHITECTURE.md
│
├── build.gradle.kts                   # Root buildscript, repository definitions, subproject config
├── settings.gradle.kts                # Dynamic subproject inclusion
├── gradle.properties                  # JVM memory and build performance properties
├── gradle/wrapper/                    # Gradle wrapper binaries & properties
├── gradlew / gradlew.bat              # Gradle wrapper execution scripts
├── repo.json                          # CloudStream repository manifest index
├── .github/
│   └── workflows/
│       └── build.yml                  # Continuous Integration & builds branch release deployment
│
└── DiziBoxProvider/                   # Target initial provider module
    ├── build.gradle.kts               # Module manifest metadata (name, version, tvTypes, icon)
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml    # Android library manifest
        │   ├── kotlin/com/aethelion/
        │   │   ├── DiziBoxPlugin.kt   # @CloudstreamPlugin entrypoint registering DiziBoxProvider
        │   │   └── DiziBoxProvider.kt # MainAPI implementation (search, home, load, loadLinks)
        │   └── res/                   # Resource drawables / icons
        └── test/
            └── kotlin/com/aethelion/  # Unit, fixture, and regression test suite
```

============================================================
2. SHARED UTILITIES & ISOLATION STRATEGY
============================================================

### Analysis: Standalone vs Shared Module
- **Upstream Pattern:** In both `recloudstream/extensions` and `hexated/cloudstream-extensions-hexated`, providers are kept strictly modular. Heavy shared library modules are avoided because they often lead to class duplication in DEX files across independently installed `.cs3` plugins.
- **Recommended Strategy:**
  - Common utilities (e.g. string helpers, URL fixers) should remain lightweight internal functions within the provider or packaged as clean Kotlin source files per module.
  - Providers remain independently buildable, deployable, and testable without hard inter-module binary dependencies.

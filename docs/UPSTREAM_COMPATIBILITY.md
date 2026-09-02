# AETHELIONCS: UPSTREAM COMPATIBILITY & BUILD SPECIFICATION

============================================================
1. UPSTREAM CANONICAL REPOSITORIES
============================================================

The following upstream repositories serve as the authoritative baseline for CloudStream extension development:

1. **`recloudstream/extensions`** (Official Extensions Monorepo)
   - Baseline Repository: `https://github.com/recloudstream/extensions`
   - Role: Reference build scripts, subproject Gradle layout, provider implementations, and manifest generation.

2. **`recloudstream/TestPlugins`** (Official Template & Guide)
   - Baseline Repository: `https://github.com/recloudstream/TestPlugins`
   - Role: Bootstrap project template, testing layout, GitHub Actions workflow.

3. **`recloudstream/cloudstream`** (Core Android Application & Library)
   - Baseline Repository: `https://github.com/recloudstream/cloudstream`
   - Core Library Artifact: `com.github.recloudstream.cloudstream:library:-SNAPSHOT`
   - Gradle Plugin: `com.github.recloudstream:gradle:-SNAPSHOT`

============================================================
2. BUILD TOOLCHAIN & COMPILER CONFIGURATION
============================================================

### Root Buildscript & Plugin Setup (`build.gradle.kts`):
```kotlin
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
        classpath("com.github.recloudstream:gradle:-SNAPSHOT")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

### Subproject Target Specification:
```kotlin
subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")

    cloudstream {
        setRepo(System.getenv("GITHUB_REPOSITORY") ?: "https://github.com/user/repo")
    }

    android {
        namespace = "recloudstream"

        defaultConfig {
            minSdk = 21
            compileSdkVersion(35)
            targetSdk = 35
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        tasks.withType<KotlinJvmCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_1_8)
                freeCompilerArgs.addAll(
                    "-Xno-call-assertions",
                    "-Xno-param-assertions",
                    "-Xno-receiver-assertions"
                )
            }
        }
    }
}
```

============================================================
3. STRICT RUNTIME DEPENDENCY CONSTRAINTS
============================================================

1. **Jackson Compatibility Rule:**
   - Coordinate: `com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1`
   - Rule: **DO NOT BUMP Jackson above 2.13.1**. Upstream explicitly documents that versions >2.13.1 break binary compatibility on older Android runtime versions.

2. **HTTP Transport Library:**
   - Coordinate: `com.github.Blatzar:NiceHttp:0.4.11`
   - Native integration with CloudStream interceptors and cookie management.

3. **HTML DOM Parser:**
   - Coordinate: `org.jsoup:jsoup:1.18.3`

4. **Platform API Purity:**
   - Platform classes provided by the CloudStream core runtime (`com.lagradost.cloudstream3.*`) must be provided via `implementation("com.github.recloudstream.cloudstream:library:-SNAPSHOT")` and must NOT be packaged as duplicate duplicate classes inside the extension DEX bytecode.

============================================================
4. CI/CD & REPOSITORY DEPLOYMENT
============================================================

### GitHub Actions Tasks:
1. `./gradlew make makePluginsJson` — Compiles all active subprojects, packages `.cs3` files, and generates `plugins.json`.
2. `./gradlew ensureJarCompatibility` — Validates DEX and JAR binary compatibility.
3. Deploy to `builds` branch: `plugins.json` and all `*.cs3` artifacts committed to the dedicated `builds` orphan branch.
4. `repo.json` manifest:
```json
{
    "name": "AethelionCS",
    "description": "Aethelion CloudStream Providers Repository",
    "manifestVersion": 1,
    "pluginLists": [
        "https://raw.githubusercontent.com/<user>/<repo>/builds/plugins.json"
    ]
}
```

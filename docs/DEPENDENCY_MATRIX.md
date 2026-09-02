# AETHELIONCS: CURRENT UPSTREAM DEPENDENCY MATRIX

============================================================
1. BUILD & RUNTIME DEPENDENCY COORDINATES
============================================================

| Component | Upstream Canonical Coordinate / Version | Purpose |
|---|---|---|
| **Android Gradle Plugin (AGP)** | `com.android.tools.build:gradle:8.7.3` | Android build system |
| **Kotlin Gradle Plugin** | `org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0` | Kotlin language compiler |
| **CloudStream Gradle Plugin** | `com.github.recloudstream:gradle:-SNAPSHOT` | Extension packager (`.cs3`) |
| **CloudStream Core Library** | `com.github.recloudstream.cloudstream:library:-SNAPSHOT` | Platform interfaces (`MainAPI`, `LoadResponse`, `ExtractorApi`) |
| **NiceHttp** | `com.github.Blatzar:NiceHttp:0.4.11` | Asynchronous HTTP client & cookie manager |
| **Jsoup** | `org.jsoup:jsoup:1.18.3` | HTML parser & DOM selector engine |
| **Jackson Kotlin Module** | `com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1` | JSON serialization (**Strict upper limit: 2.13.1**) |
| **NewPipe Extractor** | `com.github.teamnewpipe:NewPipeExtractor:v0.25.2` | Optional YouTube / video extractor |

============================================================
2. SDK & JVM TARGET SPECIFICATIONS
============================================================

- **Min SDK:** `21` (Android 5.0 Lollipop)
- **Target SDK:** `35` (Android 15)
- **Compile SDK:** `35` (Android 15)
- **Java Source Compatibility:** `JavaVersion.VERSION_1_8`
- **Java Target Compatibility:** `JavaVersion.VERSION_1_8`
- **Kotlin JVM Target:** `JvmTarget.JVM_1_8`
- **Kotlin Compiler Free Args:**
  - `"-Xno-call-assertions"`
  - `"-Xno-param-assertions"`
  - `"-Xno-receiver-assertions"`

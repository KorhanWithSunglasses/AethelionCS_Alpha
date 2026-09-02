# AETHELIONCS: ACTIVE PROVIDER PATTERNS & BEST PRACTICES

============================================================
1. ACTIVE REPOSITORY CASE STUDIES
============================================================

Based on empirical inspection of `recloudstream/extensions` and `hexated/cloudstream-extensions-hexated`:

### Pattern A: Multi-Provider Subproject Architecture
- Every provider module is an independent Gradle subproject directory (e.g. `DiziBoxProvider/`, `YoutubeProvider/`).
- Subprojects share root plugin configuration but maintain separate versioning, manifests, and dependencies.
- `settings.gradle.kts` uses dynamic subdirectory discovery:
  ```kotlin
  File(rootDir, ".").eachDir { dir ->
      if (!disabled.contains(dir.name) && File(dir, "build.gradle.kts").exists()) {
          include(dir.name)
      }
  }
  ```

### Pattern B: Source Resolution & Extractor Delegation
Working providers do not reinvent video decoders; they delegate iframe/embed URLs directly to CloudStream's extractor engine:
```kotlin
// Example from working provider (YomoviesProvider / YoutubeProvider)
override suspend fun loadLinks(
    data: String,
    isCasting: Boolean,
    subtitleCallback: (SubtitleFile) -> Unit,
    callback: (ExtractorLink) -> Unit
): Boolean {
    // 1. Fetch episode page / player DOM
    val document = app.get(data).document
    
    // 2. Extract player iframes
    val iframes = document.select("div.player-wrapper iframe").mapNotNull { it.attr("src") }
    
    // 3. Delegate to native CloudStream extractors
    iframes.apmap { iframeUrl ->
        safeApiCall {
            loadExtractor(
                url = fixUrl(iframeUrl),
                referer = "$mainUrl/",
                subtitleCallback = subtitleCallback,
                callback = callback
            )
        }
    }
    return true
}
```

============================================================
2. EPISODE & SOURCE IDENTITY ARCHITECTURE
============================================================

### The Episode Identity Invariant
In CloudStream, episode navigation is stateless across invocations of `loadLinks`:
1. `load(url)` parses episodes and attaches a distinct `data` payload:
   ```kotlin
   episodes.add(
       newEpisode(data = episodeUrl) {
           this.name = episodeTitle
           this.season = seasonNumber
           this.episode = episodeNumber
           this.posterUrl = episodeThumbnail
       }
   )
   ```
2. When the user taps Episode X, CloudStream invokes `loadLinks(data = episodeXUrl, ...)`.

### Critical Prohibitions:
- **NO Global Mutable State:** Never store current episode URLs or resolved streams in provider companion objects or instance variables (e.g. `var currentEpisodeUrl`). This causes race conditions where Episode 2 plays Episode 1's cached video.
- **NO Unkeyed Caching:** Cache lookups must be strictly keyed by full `episodeUrl` or `contentId + season + episode`.
- **NO Cross-Episode Source Leakage:** Every execution of `loadLinks` must resolve sources solely from the passed `data` argument.

============================================================
3. SUBTITLE & AUDIO HANDLING
============================================================

1. **Subtitles (`SubtitleFile`):**
   - Transmitted via `subtitleCallback.invoke(SubtitleFile(lang = "Türkçe", url = vttOrSrtUrl))`.
   - Subtitles embedded in HLS (M3U8) are parsed natively by ExoPlayer if provided in the stream manifest.
   - Do NOT fabricate subtitle tracks or languages that do not exist in the source HTML/JSON.

2. **Audio / Quality Tracks (`ExtractorLink`):**
   - Produced via `ExtractorLink(source = name, name = qualityLabel, url = mediaUrl, referer = ref, quality = Qualities.P1080.value, isM3u8 = true)`.
   - Distinguish Dubbed (`Dub`) vs Subtitled (`Sub`) explicitly in link names when provided by the site (e.g. `DiziBox - Türkçe Dublaj (1080p)`).

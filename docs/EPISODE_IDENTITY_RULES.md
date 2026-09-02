# AETHELIONCS: EPISODE IDENTITY & CACHE SAFETY RULES

============================================================
1. THE EPISODE IDENTITY INVARIANT
============================================================

```text
INVARIANT:
For any two episodes N and M (where N != M):
loadLinks(data = EpisodeN.data) and loadLinks(data = EpisodeM.data)
MUST execute independently with ZERO cross-talk, ZERO shared mutable memory,
and produce strictly independent ExtractorLink streams.
```

============================================================
2. STRICT PROHIBITIONS FOR AETHELIONCS
============================================================

1. **NO Global or Class-Level Mutable Variables:**
   ```kotlin
   // FORBIDDEN PATTERN
   class DiziBoxProvider : MainAPI() {
       companion object {
           var cachedStreamUrl: String? = null // VIOLATION
       }
       var lastResolvedEpisode: String? = null // VIOLATION
   }
   ```
   **Reason:** CloudStream maintains long-lived provider singletons. Instance variables persist across episode taps, causing Episode 2 to reuse Episode 1's resolved URL.

2. **NO Unkeyed In-Memory Maps:**
   Any caching mechanism must be explicitly keyed by `(episodeData, serverIndex)` with short TTLs or disabled entirely in favor of on-demand stateless scraping.

3. **NO Synthetic String Inventions:**
   Never construct fake video URLs (e.g. `https://example.com/video_${epNum}.mp4`) to pass unit tests. Unit tests must validate parser logic on real DOM fixtures.

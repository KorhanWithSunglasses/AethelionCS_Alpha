# AETHELIONCS: EXTRACTOR STRATEGY & DELEGATION RESEARCH

============================================================
1. NATIVE `loadExtractor` VS CUSTOM `ExtractorApi`
============================================================

### 1. When to use native `loadExtractor`:
- CloudStream contains built-in extractors for common video hosts: VidMoly, StreamTape, FileLions, DoodStream, MixDrop, Voe, SuperStream, FastStream, etc.
- When an episode page provides an iframe pointing to one of these hosts (e.g. `https://vidmoly.to/embed-...`), calling:
  ```kotlin
  loadExtractor(
      url = iframeUrl,
      referer = "$mainUrl/",
      subtitleCallback = subtitleCallback,
      callback = callback
  )
  ```
  allows CloudStream's core extractor engine to unpack packed JavaScript, fetch HLS master manifests, parse quality tracks, and stream `ExtractorLink` items automatically.

### 2. When to create a custom `ExtractorApi`:
- When the target site uses a proprietary video player API (e.g. custom tokenized AJAX endpoint, custom AES encryption, or private CDN).
- Custom extractors subclass `ExtractorApi`:
  ```kotlin
  class DiziBoxCustomExtractor : ExtractorApi() {
      override val mainUrl = "https://player.dizibox.live"
      override val name = "DiziBox Player"
      override val requiresReferer = true

      override suspend fun getUrl(
          url: String,
          referer: String?,
          subtitleCallback: (SubtitleFile) -> Unit,
          callback: (ExtractorLink) -> Unit
      ) {
          // Parse proprietary manifest or API
      }
  }
  ```
- Registered via `registerExtractorAPI(DiziBoxCustomExtractor())` in plugin entrypoint.

============================================================
2. CONCURRENT RESOLUTION & ERROR ISOLATION
============================================================

Working multi-source providers resolve all available server iframes concurrently using `apmap` with `safeApiCall`:

```kotlin
serverIframes.apmap { (serverName, iframeUrl) ->
    safeApiCall {
        loadExtractor(
            url = fixUrl(iframeUrl),
            referer = "$mainUrl/",
            subtitleCallback = subtitleCallback,
            callback = { link ->
                // Optionally prefix link name with server name
                callback.invoke(
                    link.copy(name = "[$serverName] ${link.name}")
                )
            }
        )
    }
}
```

### Key Safety Characteristics:
1. **Non-blocking:** A timeout or 404 on Server 1 (e.g. Rapidrame) does not block Server 2 (e.g. VidMoly).
2. **Distinct Link Naming:** Prefixing links with `[ServerName]` allows users in the CloudStream player to see and choose their preferred mirror.

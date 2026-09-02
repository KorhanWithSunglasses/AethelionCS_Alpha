# AETHELIONCS: OFFICIAL CLOUDSTREAM PROVIDER DEEP RESEARCH

============================================================
1. OFFICIAL PROVIDER ARCHITECTURAL ANALYSIS
============================================================

Based on deep inspection of official repositories (`recloudstream/extensions` and `recloudstream/TestPlugins`):

### Analyzed Official Providers:
1. **`YoutubeProvider`** (`recloudstream/extensions/master/YoutubeProvider/src/main/kotlin/recloudstream/YoutubeProvider.kt`)
   - **Type:** Media / Playlist / Channel streaming.
   - **Load Lifecycle:** Preserves episode identity via URL mapping (`newEpisode(item.url)`).
   - **LoadLinks Lifecycle:** Directly delegates `loadLinks(data = itemUrl)` to `loadExtractor("https://youtube.com/watch?v=$data", subtitleCallback, callback)`.
   - **Fault Isolation:** Page parsing uses pagination thresholds to avoid API overhead.

2. **`TwitchProvider`** (`recloudstream/extensions/master/TwitchProvider/src/main/kotlin/recloudstream/TwitchProvider.kt`)
   - **Type:** Live stream parsing.
   - **Load Lifecycle:** Parses live metadata and channel rank.
   - **Extractor Integration:** Embeds an inner `TwitchExtractor : ExtractorApi()` defining custom JSON API stream resolution (`pwn.sh/tools/streamapi.py`) and emitting `newExtractorLink` with quality parameters (`quality = getQualityFromName(...)`).

3. **`InvidiousProvider`** (`recloudstream/extensions/master/InvidiousProvider/src/main/kotlin/recloudstream/InvidiousProvider.kt`)
   - **Type:** Multi-instance video and playlist scraping.
   - **LoadLinks Lifecycle:** Resolves HLS manifests and direct MP4 qualities, feeding `callback` directly.

============================================================
2. CORE LIFECYCLE & EPISODE IDENTITY INVARIANTS
============================================================

```mermaid
sequenceDiagram
    participant App as CloudStream Core
    participant MainAPI as DiziBoxProvider (MainAPI)
    participant Extractor as Extractor Engine (loadExtractor)
    participant Player as ExoPlayer / Media3

    App->>MainAPI: search("query")
    MainAPI-->>App: List<SearchResponse>

    App->>MainAPI: load(seriesUrl)
    Note over MainAPI: Creates Episodes with immutable 'data = episodeUrl'
    MainAPI-->>App: TvSeriesLoadResponse(episodes=[Ep1(data=url1), Ep2(data=url2)])

    App->>MainAPI: loadLinks(data = url1, subtitleCallback, callback)
    MainAPI->>Extractor: loadExtractor(iframeUrl, referer, subtitleCallback, callback)
    Extractor-->>MainAPI: ExtractorLink / SubtitleFile
    MainAPI-->>App: Stream emitted via callbacks
    App->>Player: Playback initialization
```

### Invariant Rules:
1. **Stateless `loadLinks`:** `loadLinks` must treat `data` as the sole authority for resolution.
2. **Zero Shared State:** No class-level instance properties (`var currentVideoUrl`) or global variables may hold transient episode data.
3. **Failure Isolation:** Each server / iframe in `loadLinks` must be wrapped in `safeApiCall { ... }` or `try/catch` so that a broken iframe on Server A does not halt resolution of Server B or Server C.

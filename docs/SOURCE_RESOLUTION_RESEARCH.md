# AETHELIONCS: MULTI-SOURCE & QUALITY RESOLUTION MODEL

============================================================
1. SOURCE RESOLUTION TAXONOMY
============================================================

To prevent duplicate links and preserve source clarity, the provider architecture strictly separates the following layers:

```text
┌─────────────────────────────────────────────────────────────┐
│ 1. Episode Entity (e.g. Breaking Bad S01E01)                │
└──────────────────────────────┬──────────────────────────────┘
                               │
       ┌───────────────────────┼───────────────────────┐
       ▼                       ▼                       ▼
┌───────────────┐       ┌───────────────┐       ┌───────────────┐
│ Server A      │       │ Server B      │       │ Server C      │
│ (VidMoly)     │       │ (Rapidrame)   │       │ (Plus / Özel) │
└──────┬────────┘       └──────┬────────┘       └──────┬────────┘
       ▼                       ▼                       ▼
┌───────────────┐       ┌───────────────┐       ┌───────────────┐
│ Iframe Embed  │       │ Iframe Embed  │       │ Iframe Embed  │
└──────┬────────┘       └──────┬────────┘       └──────┬────────┘
       ▼                       ▼                       ▼
┌───────────────┐       ┌───────────────┐       ┌───────────────┐
│ Extractor     │       │ Extractor     │       │ Extractor     │
└──────┬────────┘       └──────┬────────┘       └──────┬────────┘
       ▼                       ▼                       ▼
┌───────────────┐       ┌───────────────┐       ┌───────────────┐
│ ExtractorLink │       │ ExtractorLink │       │ ExtractorLink │
│ (1080p, 720p) │       │ (Auto HLS)    │       │ (1080p DUB)   │
└───────────────┘       └───────────────┘       └───────────────┘
```

============================================================
2. DISTINCTIONS ACROSS LAYERS
============================================================

1. **SOURCE HOST:** The hosting domain (e.g. `vidmoly.to`, `rapidrame.com`).
2. **SOURCE URL:** The specific embed URL with episode-specific token/ID (e.g. `https://vidmoly.to/embed-abc123xyz.html`).
3. **EXTRACTOR:** The resolver engine converting the embed into playable media streams.
4. **QUALITY:** The resolution variant (e.g. `1080p`, `720p`, `480p`, `Auto (HLS)`).
5. **MEDIA URL:** The final direct video stream (e.g. `.m3u8` or `.mp4` endpoint on CDN).

============================================================
3. DEDUPLICATION RULES
============================================================

- **Rule 1:** Different episodes pointing to the same host MUST NEVER be deduplicated. Episode identity (`data`) is primary.
- **Rule 2:** Multiple quality variants originating from the same HLS master playlist are handled natively by ExoPlayer if `isM3u8 = true` and should not be manually duplicated as separate flat links unless distinct MP4 direct links exist.
- **Rule 3:** Dubbed (`TR Dublaj`) vs Subtitled (`TR Altyazı`) must be treated as separate distinct source streams and labeled accordingly in `ExtractorLink.name`.

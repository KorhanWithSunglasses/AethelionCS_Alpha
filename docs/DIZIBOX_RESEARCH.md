# AETHELIONCS: DIZIBOX TARGET SITE SPECIFICATION & RESEARCH

============================================================
1. TARGET SITE PROFILE
============================================================

- **Target Service:** DiziBox
- **Language / Region:** Turkish (`"tr"`)
- **Primary Content:** Foreign TV Series and Movies localized with Turkish subtitles and dubbing.
- **Domain Resilience:** DiziBox frequently changes root domains due to DNS blocking in Turkey. The provider architecture must support dynamic base URL resolution or easy configuration overrides.

============================================================
2. CONTENT MODEL & URL PATTERNS
============================================================

### 1. Home Page Feed
- **Categories:**
  - Son Eklenen Bölümler (Recently Added Episodes)
  - Popüler Diziler (Popular Series)
  - Yeni Başlayan Diziler (New Releases)
  - Tüm Diziler (All Series Catalog)
- **Model Mapping:** Maps to `HomePageResponse` containing `HomePageList` categories.

### 2. Search System
- **Endpoint:** `/?s={query}` or `/ara?q={query}`
- **Parsing:** Returns media list (`div.dizi-kutu`, `div.post-item`) with thumbnail, title, and link.
- **Model Mapping:** Maps to `List<SearchResponse>` (`newTvSeriesSearchResponse`).

### 3. TV Series Detail Page
- **Structure:**
  - Title: Extracted from main heading.
  - Poster: Extracted from `img.poster`, `img.dizi-afis`.
  - Synopsis / Plot: Extracted from `div.dizi-ozet` or description container.
  - Metadata: Year, IMDb score, categories/tags, actors.
  - Seasons & Episodes: Structured list of seasons and episode cards.
- **Episode List Parsing:**
  - Season containers (`div.sezon-bolumleri`, `ul.bolum-listesi`).
  - Episode item extraction: Episode name, season index, episode index, thumbnail, and canonical episode page URL.
  - Attached to `newEpisode(data = episodeUrl)`.

### 4. Episode Playback & Source Resolution
- **Structure:**
  - Episode page contains video player frame and server selection tabs (e.g. `VidMoly`, `Rapidrame`, `DiziBox Özel`, `Plus`, `Fembed`).
  - Server tabs switch player embed iframes via data attributes or AJAX endpoints.
- **Source Delegation:**
  - Extract iframe embed URLs (e.g. `https://vidmoly.to/embed-...`, `https://streamtape.com/e/...`).
  - Delegate each iframe directly to `loadExtractor(iframeUrl, referer, subtitleCallback, callback)`.

============================================================
3. SUBTITLES & AUDIO LOCALIZATION
============================================================

- **Turkish Dubbing (Dublaj):** Detected via server tab labels (e.g. "Türkçe Dublaj", "TR Dublaj") -> Tagged in link name.
- **Turkish Subtitles (Altyazı):** Detected via server tab labels (e.g. "Türkçe Altyazı", "TR Altyazı") -> Tagged in link name.
- **External Subtitles:** If VTT/SRT subtitles are linked in player scripts, transmit via `subtitleCallback(SubtitleFile("Türkçe", vttUrl))`.

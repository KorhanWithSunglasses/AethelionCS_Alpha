# AETHELIONCS: DIZIBOX SOURCE MAP & DOMAIN PROFILE

============================================================
1. TARGET DOMAIN & LIVE INVESTIGATION STATUS
============================================================

- **Target Domain:** `https://www.dizibox.live/`
- **Live Scraping Status:** Standard raw HTTP requests return HTTP `403 Forbidden` due to Cloudflare Web Application Firewall (WAF) challenge protection.
- **CloudStream Runtime Integration:** CloudStream handles WAF/Cloudflare natively via user WebView clearance challenges, passing `cf_clearance` cookies through `NiceHttp` / `app.get()`.

============================================================
2. CONTENT HIERARCHY & URL SCHEMES
============================================================

```text
1. HOMEPAGE:
   URL: https://www.dizibox.live/
   Sections:
   - "Son Eklenen Bölümler" (Recent Episodes)
   - "Popüler Diziler" (Popular TV Series)
   - "Yeni Başlayan Diziler" (New Releases)

2. SEARCH:
   URL: https://www.dizibox.live/?s={query}
   Results: Cards with poster, title, and link.

3. SERIES DETAIL PAGE:
   URL: https://www.dizibox.live/dizi/{series-slug}/
   Elements:
   - Title, Banner, Poster, Year, Genres, Synopsis, Cast, IMDb Rating
   - Season tabs: 1. Sezon, 2. Sezon...
   - Episode list: List of episode links mapped to canonical episode pages.

4. EPISODE PLAYBACK PAGE:
   URL: https://www.dizibox.live/{series-slug}-{season}-sezon-{episode}-bolum-izle/
   Elements:
   - Server selection tabs: [VidMoly, Rapidrame, DiziBox Plus, DiziBox Özel]
   - Subtitle / Dub selection: [Türkçe Altyazı, Türkçe Dublaj]
   - Embedded video iframes: Dynamic iframe loader pointing to video hosts.
```

============================================================
3. OBSERVED SERVER & EXTRACTOR MAPPING
============================================================

| Server Label | Embed Host Type | Extractor Target | Status |
|---|---|---|---|
| **VidMoly** | `https://vidmoly.to/embed-{id}.html` | Built-in CloudStream `VidMoly` Extractor | `DISCOVERED` |
| **Rapidrame** | `https://rapidrame.com/embed-{id}` | Built-in CloudStream `Rapidrame` Extractor | `DISCOVERED` |
| **DiziBox Özel / Plus** | Internal tokenized player | Proprietary or secondary iframe delegation | `DISCOVERED / UNRESOLVED` |
| **StreamTape / FileLions** | Secondary mirror host | Built-in CloudStream Extractor | `DISCOVERED` |

*Note: Unresolved proprietary sources will be inspected during live runtime testing without fabricating synthetic stream URLs.*

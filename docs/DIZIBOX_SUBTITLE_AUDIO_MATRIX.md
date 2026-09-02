# AETHELIONCS: DIZIBOX SUBTITLE & AUDIO LOCALIZATION MATRIX

============================================================
1. SUBTITLE & AUDIO OBSERVATION MATRIX
============================================================

| Server | Subtitle Delivery Type | Subtitle Language | Audio Track | Evidence |
|---|---|---|---|---|
| **Moly+ (`/2/`)** | Hardcoded / Burned into video stream | Turkish (`"tr"`) | Original English / Japanese | `LIVE_BROWSER_OBSERVED` |
| **Odnok (`/3/`)** | Hardcoded / Burned into video stream | Turkish (`"tr"`) | Original English / Japanese | `LIVE_BROWSER_OBSERVED` |
| **DBX Pro (`/1/`)** | Broken / Backend error | N/A | N/A | `LIVE_BROWSER_OBSERVED` |

============================================================
2. CLOUDSTREAM STREAM TAGGING RULES
============================================================

1. **Hardcoded Subtitles:**
   - Because Turkish subtitles are burned directly into the video stream for Moly+ and Odnok embeds, no external VTT/SRT file is emitted.
   - The stream link must be labeled as `[Moly+] Türkçe Altyazılı` or `[Odnok] Türkçe Altyazılı` in `ExtractorLink.name`.

2. **No Fabricated Subtitle Files:**
   - Do NOT emit fake or synthetic `.srt` links when none exist in the underlying HTML/JSON.

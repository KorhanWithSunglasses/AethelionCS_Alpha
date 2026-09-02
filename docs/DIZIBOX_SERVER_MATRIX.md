# AETHELIONCS: DIZIBOX DYNAMIC SERVER DISCOVERY & STATUS MATRIX

============================================================
1. DYNAMIC SERVER DISCOVERY MECHANISM
============================================================

DiziBox renders server selections dynamically via HTML markup in `select.woca-linkpages-dd`:
- **Option 1 (`DBX Pro`):** `value="https://www.dizibox.live/[episode-slug]/"` (Base URL **without** `/1/`, `selected="selected"`)
- **Option 2 (`Moly+`):** `value="https://www.dizibox.live/[episode-slug]/2/"`
- **Option 3 (`Odnok`):** `value="https://www.dizibox.live/[episode-slug]/3/"`
- **Option N:** Discovered dynamically by iterating over all `<option>` tags in `select.woca-linkpages-dd`.

### Direct Target Semantics:
Every `<option value="...">` is directly navigable as the server-specific episode page (`OPTION_VALUE_IS_DIRECT_TARGET = YES`). Navigating directly to `.../2/` or `.../3/` renders the specific player for that mirror. Navigating to `/1/` redirects to the base URL.

### Dynamic Parsing Architecture:
```kotlin
fun Document.discoverEpisodeServers(episodeBaseUrl: String): List<Pair<String, String>> {
    val servers = mutableListOf<Pair<String, String>>()
    this.select("select.woca-linkpages-dd option").forEach { option ->
        val name = option.text().trim()
        val targetUrl = option.attr("value").ifEmpty { option.attr("href") }
        if (name.isNotBlank() && targetUrl.isNotBlank()) {
            servers.add(name to fixUrl(targetUrl))
        }
    }
    return if (servers.isNotEmpty()) servers else listOf("Default" to episodeBaseUrl)
}
```

============================================================
2. FULL STATUS MODEL MATRIX (TESTED EPISODES)
============================================================

| Episode | Server | Target Route URL | Source Discovered | Player Visible | Player Initialized | Jetski Browser Playback | Opera User Observed Playback | Final Media URL | CloudStream Runtime |
|---|---|---|---|---|---|---|---|---|---|
| **Ghost in Shell S01E01** | `DBX Pro` | `.../the-ghost-in-the-shell-1-sezon-1-bolum-izle/` | YES | YES | YES | NOT_PLAYING_IN_JETSKI (Ban Error) | **WORKING** | UNKNOWN | `NOT_TESTED` |
| **Ghost in Shell S01E01** | `Moly+` | `.../the-ghost-in-the-shell-1-sezon-1-bolum-izle/2/` | YES | YES | YES | **WORKING_IN_JETSKI** | **WORKING** | OBSERVED (HLS) | `NOT_TESTED` |
| **Ghost in Shell S01E01** | `Odnok` | `.../the-ghost-in-the-shell-1-sezon-1-bolum-izle/3/` | YES | YES | YES | **WORKING_IN_JETSKI** | **WORKING** | OBSERVED (OK.ru) | `NOT_TESTED` |
| **Ghost in Shell S01E02** | `DBX Pro` | `.../the-ghost-in-the-shell-1-sezon-2-bolum-izle/` | YES | YES | YES | NOT_PLAYING_IN_JETSKI | **WORKING** | UNKNOWN | `NOT_TESTED` |
| **Ghost in Shell S01E02** | `Moly+` | `.../the-ghost-in-the-shell-1-sezon-2-bolum-izle/2/` | YES | YES | YES | **WORKING_IN_JETSKI** | **WORKING** | OBSERVED (HLS) | `NOT_TESTED` |
| **Ghost in Shell S01E02** | `Odnok` | `.../the-ghost-in-the-shell-1-sezon-2-bolum-izle/3/` | YES | YES | YES | **WORKING_IN_JETSKI** | **WORKING** | OBSERVED (OK.ru) | `NOT_TESTED` |
| **Chernobyl S01E01** | `DBX Pro` | `.../chernobyl-1-sezon-1-bolum-hd-izle/` | YES | YES | YES | NOT_PLAYING_IN_JETSKI | **WORKING** | UNKNOWN | `NOT_TESTED` |
| **Chernobyl S01E01** | `Moly+` | `.../chernobyl-1-sezon-1-bolum-hd-izle/2/` | YES | YES | YES | **WORKING_IN_JETSKI** | **WORKING** | OBSERVED (HLS) | `NOT_TESTED` |
| **Chernobyl S01E01** | `Odnok` | `.../chernobyl-1-sezon-1-bolum-hd-izle/3/` | YES | YES | YES | BLOCKED_IN_JETSKI (DMCA) | BLOCKED_IN_OPERA (DMCA) | UNKNOWN | `NOT_TESTED` |

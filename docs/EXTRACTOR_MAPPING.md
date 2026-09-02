# AETHELIONCS: EXTRACTOR MAPPING & UPSTREAM SUPPORT

============================================================
1. EXTRACTOR MAPPING TABLE
============================================================

| DiziBox Server Option | Subpage Route | Extractor Host Target | Upstream CloudStream Support | Strategy |
|---|---|---|---|---|
| **Moly+** | `/2/` | `vidmoly.to` / `molystream` | **YES** (`VidMoly` Extractor in CloudStream) | Delegate via `loadExtractor()` |
| **Odnok** | `/3/` | `ok.ru` / `odnoklassniki.ru` | **YES** (`Odnoklassniki` Extractor in CloudStream) | Delegate via `loadExtractor()` |
| **DBX Pro** | `/1/` or base | Internal player | **NO / BROKEN** | Fallback with `safeApiCall` |

============================================================
2. DELEGATION WORKFLOW
============================================================

```kotlin
suspend fun extractServerIframes(
    episodeBaseUrl: String,
    subtitleCallback: (SubtitleFile) -> Unit,
    callback: (ExtractorLink) -> Unit
) {
    val servers = listOf(
        "Moly+" to "$episodeBaseUrl/2/",
        "Odnok" to "$episodeBaseUrl/3/",
        "DBX Pro" to "$episodeBaseUrl/1/"
    )

    servers.apmap { (serverName, serverUrl) ->
        safeApiCall {
            val doc = app.get(serverUrl).document
            val iframeSrc = doc.selectFirst("iframe")?.attr("src")
            if (!iframeSrc.isNullOrBlank()) {
                loadExtractor(
                    url = fixUrl(iframeSrc),
                    referer = "https://www.dizibox.live/",
                    subtitleCallback = subtitleCallback,
                    callback = { link ->
                        callback.invoke(link.copy(name = "[$serverName] ${link.name}"))
                    }
                )
            }
        }
    }
}
```

# AETHELIONCS: DIZIBOX EPISODE SERVER DISCOVERY & HTML STRUCTURE SPECIFICATION

============================================================
1. EXACT HTML STRUCTURE
============================================================

On DiziBox episode pages, server/source options are rendered in the raw HTML via a `<select>` element with class `woca-linkpages-dd`:

### Raw Initial HTML Snippet:
```html
<select class="woca-linkpages-dd" name="woca-linkpages-dd">
    <option value="https://www.dizibox.live/death-note-1-sezon-1-bolum-hd-izle/" selected="selected">DBX Pro</option>
    <option value="https://www.dizibox.live/death-note-1-sezon-1-bolum-hd-izle/2/">Moly+</option>
    <option value="https://www.dizibox.live/death-note-1-sezon-1-bolum-hd-izle/3/">Odnok</option>
</select>
```

### Client-Side Rendered DOM (jQuery `SelectBox` Plugin Transformation):
```html
<a class="selectBox woca-linkpages-dd selectBox-dropdown" style="display: inline-block;">
    <span class="selectBox-label">
        <span class="player-icon">Player: </span>DBX Pro
    </span>
    <span class="selectBox-arrow"></span>
</a>

<ul class="selectBox-dropdown-menu selectBox-options woca-linkpages-dd">
    <li class="selectBox-selected">
        <a rel="https://www.dizibox.live/death-note-1-sezon-1-bolum-hd-izle/">DBX Pro</a>
    </li>
    <li>
        <a rel="https://www.dizibox.live/death-note-1-sezon-1-bolum-hd-izle/2/">Moly+</a>
    </li>
    <li>
        <a rel="https://www.dizibox.live/death-note-1-sezon-1-bolum-hd-izle/3/">Odnok</a>
    </li>
</ul>
```

============================================================
2. DYNAMIC DISCOVERY ALGORITHM (KOTLIN / JSOUP)
============================================================

Because the `<select>` element and its `<option>` tags are present in the raw HTTP response document (prior to client-side JS execution), CloudStream's standard Jsoup parser can extract all servers dynamically:

```kotlin
fun Document.discoverEpisodeServers(episodeBaseUrl: String): List<Pair<String, String>> {
    val servers = mutableListOf<Pair<String, String>>()
    
    // 1. Primary: Extract from raw <select class="woca-linkpages-dd"> <option> tags
    this.select("select.woca-linkpages-dd option").forEach { option ->
        val serverName = option.text().trim()
        val serverUrl = option.attr("value").ifEmpty { option.attr("href") }
        if (serverName.isNotBlank() && serverUrl.isNotBlank()) {
            servers.add(serverName to fixUrl(serverUrl))
        }
    }

    // 2. Fallback: Extract from any linkpages list elements if present
    if (servers.isEmpty()) {
        this.select("div.woca-linkpages a, ul.woca-linkpages-dd li a").forEach { link ->
            val serverName = link.text().trim()
            val serverUrl = link.attr("href").ifEmpty { link.attr("rel") }
            if (serverName.isNotBlank() && serverUrl.isNotBlank()) {
                servers.add(serverName to fixUrl(serverUrl))
            }
        }
    }

    // 3. Fallback: Default to base episode URL if no server selector is present
    return if (servers.isNotEmpty()) servers else listOf("Default" to episodeBaseUrl)
}
```

============================================================
3. ADVANTAGES OF DYNAMIC DISCOVERY
============================================================

1. **No Hardcoded Names:** If DiziBox adds a new server (e.g. `Rapid`, `UpToStream`, `DiziBox Özel`) or renames `Moly+` to `VidMoly`, the provider discovers and extracts it automatically.
2. **Deterministic Route Mapping:** The exact target URL for each mirror (e.g. `.../2/`, `.../3/`) is read directly from the `value` attribute, avoiding hardcoded string manipulation.
3. **Stateless Extensibility:** Server discovery is invoked dynamically per episode inside `loadLinks(data)`.

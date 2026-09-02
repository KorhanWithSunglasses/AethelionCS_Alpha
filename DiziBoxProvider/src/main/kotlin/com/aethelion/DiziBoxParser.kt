package com.aethelion

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import org.jsoup.nodes.Document

object DiziBoxParser {

    fun fixUrl(url: String, baseUrl: String = "https://www.dizibox.live"): String {
        val trimmed = url.trim()
        if (trimmed.isBlank()) return ""
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed
        if (trimmed.startsWith("//")) return "https:$trimmed"
        val cleanBase = baseUrl.trimEnd('/')
        val cleanPath = trimmed.trimStart('/')
        return "$cleanBase/$cleanPath"
    }

    fun isEpisodeUrl(url: String): Boolean {
        return url.contains("-bolum-") || url.contains("-sezon-")
    }

    fun MainAPI.parseSearch(document: Document): List<SearchResponse> {
        val results = mutableListOf<SearchResponse>()
        val items = document.select("article.search-card, a.series-card, article.post-item, div.dizi-kutu")

        items.forEach { element ->
            val titleElement = element.selectFirst("h3.title, span, h2, .dizi-adi")
            val linkElement = if (element.tagName() == "a") element else element.selectFirst("a[href]")
            val imgElement = element.selectFirst("img")

            val title = titleElement?.text()?.trim() ?: linkElement?.attr("title")?.trim()
            val href = linkElement?.attr("href")?.trim()
            val posterUrl = imgElement?.attr("src")?.ifEmpty { null }
                ?: imgElement?.attr("data-src")?.ifEmpty { null }

            if (!title.isNullOrBlank() && !href.isNullOrBlank()) {
                results.add(
                    newTvSeriesSearchResponse(title, fixUrl(href), TvType.TvSeries) {
                        this.posterUrl = posterUrl?.let { fixUrl(it) }
                    }
                )
            }
        }
        return results
    }

    suspend fun MainAPI.parseSeriesDetail(document: Document, url: String): TvSeriesLoadResponse {
        val title = document.selectFirst("div.dizi-header a.link-unstyled, h1.dizi-adi, h1")?.text()?.trim()
            ?: "Bilinmeyen Dizi"

        val poster = document.selectFirst("div.dizi-afis img, div.poster img, img.dizi-poster")?.let { img ->
            img.attr("src").ifEmpty { img.attr("data-src") }
        }

        val plot = document.selectFirst("div.dizi-header p.description, div.dizi-ozet")?.text()?.trim()

        val yearText = document.selectFirst("div.meta a[href*='/yil/'], span.yil")?.text()
        val year = Regex("""\b(19\d{2}|20\d{2})\b""").find(yearText ?: "")?.value?.toIntOrNull()

        val tags = document.select("div.meta a[href*='/tur/'], div.kategoriler a").map { it.text().trim() }.filter { it.isNotBlank() }
        val actors = document.select("div.oyuncular a, div.cast a").map { it.text().trim() }.filter { it.isNotBlank() }

        val episodes = mutableListOf<Episode>()
        val episodeElements = document.select("div.episodes-list a.season-episode, ul.bolum-listesi li a")

        episodeElements.forEach { el ->
            val epHref = el.attr("href").trim()
            val epText = el.text().trim()

            if (epHref.isNotBlank()) {
                val seasonNum = Regex("""(?i)(\d+)\.?\s*sezon""").find(epText)?.groupValues?.get(1)?.toIntOrNull()
                    ?: Regex("""(?i)s(\d+)""").find(epHref)?.groupValues?.get(1)?.toIntOrNull()
                    ?: 1
                val episodeNum = Regex("""(?i)(\d+)\.?\s*b[oö]l[uü]m""").find(epText)?.groupValues?.get(1)?.toIntOrNull()
                    ?: Regex("""(?i)e(\d+)""").find(epHref)?.groupValues?.get(1)?.toIntOrNull()

                episodes.add(
                    newEpisode(data = fixUrl(epHref)) {
                        this.name = epText.ifBlank { "$seasonNum. Sezon $episodeNum. Bölüm" }
                        this.season = seasonNum
                        this.episode = episodeNum
                    }
                )
            }
        }

        return newTvSeriesLoadResponse(title, url, TvType.TvSeries, episodes) {
            this.posterUrl = poster?.let { fixUrl(it) }
            this.plot = plot
            this.year = year
            this.tags = tags
            addActors(actors)
        }
    }

    suspend fun MainAPI.parseEpisodeDetail(document: Document, url: String): TvSeriesLoadResponse {
        val titleText = document.selectFirst("h1.dizi-adi, h1")?.text()?.trim() ?: "Bilinmeyen Bölüm"
        val seriesTitle = Regex("""^(.*?)\s+\d+\.\s*Sezon""").find(titleText)?.groupValues?.get(1)?.trim()
            ?: titleText

        val seasonNum = Regex("""(?i)(\d+)\.?\s*sezon""").find(titleText)?.groupValues?.get(1)?.toIntOrNull() ?: 1
        val episodeNum = Regex("""(?i)(\d+)\.?\s*b[oö]l[uü]m""").find(titleText)?.groupValues?.get(1)?.toIntOrNull() ?: 1

        val singleEpisode = newEpisode(data = fixUrl(url)) {
            this.name = titleText
            this.season = seasonNum
            this.episode = episodeNum
        }

        return newTvSeriesLoadResponse(seriesTitle, url, TvType.TvSeries, listOf(singleEpisode)) {
            this.plot = document.selectFirst("div.episode-summary, div.description, p")?.text()?.trim()
        }
    }

    fun discoverServers(document: Document, episodeBaseUrl: String): List<DiziBoxServerOption> {
        val servers = mutableListOf<DiziBoxServerOption>()

        // 1. Primary: Extract from raw <select class="woca-linkpages-dd"> <option> tags
        document.select("select.woca-linkpages-dd option").forEach { option ->
            val serverName = option.text().trim()
            val rawUrl = option.attr("value").trim().ifEmpty { option.attr("href").trim() }
            if (serverName.isNotBlank() && rawUrl.isNotBlank()) {
                servers.add(DiziBoxServerOption(serverName, fixUrl(rawUrl, episodeBaseUrl)))
            }
        }

        // 2. Fallback: Extract from any linkpages list container elements if present
        if (servers.isEmpty()) {
            document.select("ul.selectBox-options li a, div.woca-linkpages a, ul.woca-linkpages-dd li a").forEach { link ->
                val serverName = link.text().trim()
                val rawUrl = link.attr("href").trim().ifEmpty { link.attr("rel").trim() }
                if (serverName.isNotBlank() && rawUrl.isNotBlank()) {
                    servers.add(DiziBoxServerOption(serverName, fixUrl(rawUrl, episodeBaseUrl)))
                }
            }
        }

        // 3. Fallback: Default to base episode URL if no server selector is found
        return if (servers.isNotEmpty()) servers else listOf(DiziBoxServerOption("DBX Pro", fixUrl(episodeBaseUrl)))
    }

    fun extractPlayerIframes(document: Document): List<String> {
        return document.select("div#player iframe, div.video-embed iframe, div.player-container iframe, div.player-wrapper iframe")
            .mapNotNull { it.attr("src").trim().ifEmpty { null } }
            .map { fixUrl(it) }
            .filter { it.startsWith("http://") || it.startsWith("https://") }
            .distinct()
    }
}

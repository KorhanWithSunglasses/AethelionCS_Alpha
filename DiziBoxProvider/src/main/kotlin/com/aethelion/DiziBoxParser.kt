package com.aethelion

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

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

    private fun MainAPI.extractCard(element: Element): SearchResponse? {
        val titleElement = element.selectFirst(
            "a.episode-card-title, a.poster-title, span.post_title, h3.title, .dizi-adi, h2, b.series-name, span"
        )
        val title = titleElement?.attr("title")?.takeIf { it.isNotBlank() }
            ?: titleElement?.text()?.trim()

        val linkElement = if (element.tagName() == "a") element else element.selectFirst(
            "a.episode-card-title, a.poster-title, a[href]"
        )
        val href = linkElement?.attr("href")?.trim()

        val imgElement = element.selectFirst(
            "img.afis, img.wp-post-image, img.post_thumb, img.main-cover, a.figure-link img, figure img"
        )
        val rawPoster = imgElement?.attr("data-src")?.takeIf { it.isNotBlank() }
            ?: imgElement?.attr("src")?.takeIf { !it.startsWith("data:") && it.isNotBlank() }
        val posterUrl = rawPoster?.let { fixUrl(it) }

        if (!title.isNullOrBlank() && !href.isNullOrBlank()) {
            return newTvSeriesSearchResponse(title, fixUrl(href), TvType.TvSeries) {
                this.posterUrl = posterUrl
            }
        }
        return null
    }

    fun MainAPI.parseEpisodes(document: Document): List<SearchResponse> {
        val items = document.select("article.article-episode-card, article.article-episode-small-grid")
        return items.mapNotNull { extractCard(it) }
    }

    fun MainAPI.parseSeries(document: Document): List<SearchResponse> {
        val items = document.select("article.article-series-poster, article.article-series-small-grid, a.series-card")
        return items.mapNotNull { extractCard(it) }
    }

    fun MainAPI.parseSearch(document: Document): List<SearchResponse> {
        val items = document.select(
            "article.article-episode-card, article.article-series-poster, ul.dwls_search_results > a, ul.search_results > a, article.search-card, a.series-card, article.post-item, div.dizi-kutu"
        )
        return items.mapNotNull { extractCard(it) }
    }

    suspend fun MainAPI.parseSeriesDetail(document: Document, url: String): TvSeriesLoadResponse {
        val title = document.selectFirst("h1.entry-title, div.dizi-header a.link-unstyled, h1.dizi-adi, h1")?.text()?.trim()
            ?: "Bilinmeyen Dizi"

        val img = document.selectFirst("img.main-cover, figure#main-cover img, div.dizi-afis img, div.poster img, img.dizi-poster, img.afis")
        val rawPoster = img?.attr("data-src")?.takeIf { it.isNotBlank() }
            ?: img?.attr("src")?.takeIf { !it.startsWith("data:") && it.isNotBlank() }
        val poster = rawPoster?.let { fixUrl(it) }

        val plot = document.selectFirst("div.tv-story p, div.tv-story, div.dizi-header p.description, div.dizi-ozet")?.text()?.trim()

        val yearText = document.selectFirst("a[href*='/yil/'], div.meta a[href*='/yil/'], span.yil, div.release")?.text()
        val year = Regex("""\b(19\d{2}|20\d{2})\b""").find(yearText ?: "")?.value?.toIntOrNull()

        val tags = document.select("a[href*='/tur/'], div.meta a[href*='/tur/'], div.kategoriler a")
            .map { it.text().trim() }
            .filter { it.isNotBlank() }
            .distinct()
        val actors = document.select("div.oyuncular a, div.cast a").map { it.text().trim() }.filter { it.isNotBlank() }

        val episodes = mutableListOf<Episode>()
        val seenUrls = mutableSetOf<String>()
        val episodeElements = document.select(".post-title a, div.episodes-list a.season-episode, ul.bolum-listesi li a, div.bolumler a, a[href*='-bolum-']")

        episodeElements.forEach { el ->
            val epHref = fixUrl(el.attr("href").trim())
            val epText = el.text().trim()

            if (epHref.isNotBlank() && isEpisodeUrl(epHref) && !seenUrls.contains(epHref)) {
                val hasEpInfo = Regex("""(?i)(\d+)\.?\s*(sezon|b[oö]l[uü]m)""").containsMatchIn(epText) ||
                        Regex("""(?i)-(\d+)-sezon-(\d+)-bolum""").containsMatchIn(epHref)
                if (hasEpInfo) {
                    seenUrls.add(epHref)
                    val seasonNum = Regex("""(?i)(\d+)\.?\s*sezon""").find(epText)?.groupValues?.get(1)?.toIntOrNull()
                        ?: Regex("""(?i)-(\d+)-sezon-""").find(epHref)?.groupValues?.get(1)?.toIntOrNull()
                        ?: Regex("""(?i)s(\d+)""").find(epHref)?.groupValues?.get(1)?.toIntOrNull()
                        ?: 1
                    val episodeNum = Regex("""(?i)(\d+)\.?\s*b[oö]l[uü]m""").find(epText)?.groupValues?.get(1)?.toIntOrNull()
                        ?: Regex("""(?i)-(\d+)-bolum""").find(epHref)?.groupValues?.get(1)?.toIntOrNull()
                        ?: Regex("""(?i)e(\d+)""").find(epHref)?.groupValues?.get(1)?.toIntOrNull()
                        ?: 1

                    episodes.add(
                        newEpisode(data = epHref) {
                            this.name = epText.ifBlank { "$seasonNum. Sezon $episodeNum. Bölüm" }
                            this.season = seasonNum
                            this.episode = episodeNum
                        }
                    )
                }
            }
        }

        return newTvSeriesLoadResponse(title, url, TvType.TvSeries, episodes) {
            this.posterUrl = poster
            this.plot = plot
            this.year = year
            this.tags = tags
            addActors(actors)
        }
    }

    suspend fun MainAPI.parseEpisodeDetail(document: Document, url: String): TvSeriesLoadResponse {
        val titleText = document.selectFirst("h1.entry-title, h1.dizi-adi, h1")?.text()?.trim() ?: "Bilinmeyen Bölüm"
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
            this.plot = document.selectFirst("div.tv-story p, div.tv-story, div.episode-summary, div.description, p")?.text()?.trim()
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

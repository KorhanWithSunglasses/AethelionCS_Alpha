package com.aethelion

import com.aethelion.DiziBoxParser.discoverServers
import com.aethelion.DiziBoxParser.extractPlayerIframes
import com.aethelion.DiziBoxParser.isEpisodeUrl
import com.aethelion.DiziBoxParser.parseEpisodeDetail
import com.aethelion.DiziBoxParser.parseSearch
import com.aethelion.DiziBoxParser.parseSeriesDetail
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.mvvm.safeApiCall
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor

class DiziBoxProvider : MainAPI() {
    companion object {
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        val requestHeaders = mapOf(
            "User-Agent" to USER_AGENT
        )
    }

    override var mainUrl = "https://www.dizibox.live"
    override var name = "DiziBox"
    override val hasMainPage = true
    override var lang = "tr"
    override val supportedTypes = setOf(TvType.TvSeries)

    override val mainPage = mainPageOf(
        "$mainUrl/" to "Son Eklenen Bölümler",
        "$mainUrl/diziler/" to "Tüm Diziler",
        "$mainUrl/populer-diziler/" to "Popüler Diziler"
    )

    private suspend fun getDocument(url: String) =
        app.get(url, headers = requestHeaders).document

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val targetUrl = if (page <= 1) request.data else "${request.data.removeSuffix("/")}/page/$page/"
        val document = getDocument(targetUrl)
        val items = parseSearch(document)
        return newHomePageResponse(
            listOf(HomePageList(request.name, items)),
            hasNext = items.isNotEmpty()
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchUrl = "$mainUrl/?s=$query"
        val document = getDocument(searchUrl)
        return parseSearch(document)
    }

    override suspend fun load(url: String): LoadResponse {
        val document = getDocument(url)
        return if (isEpisodeUrl(url)) {
            parseEpisodeDetail(document, url)
        } else {
            parseSeriesDetail(document, url)
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        // 1. Fetch initial episode page
        val initialDoc = getDocument(data)

        // 2. Discover all server options dynamically from select.woca-linkpages-dd
        val serverOptions = discoverServers(initialDoc, data)

        // 3. Concurrently resolve each server mirror independently
        serverOptions.amap { server ->
            safeApiCall {
                val serverDoc = if (server.url == data) initialDoc else getDocument(server.url)
                val iframes = extractPlayerIframes(serverDoc)

                iframes.forEach { iframeUrl ->
                    safeApiCall {
                        loadExtractor(
                            url = iframeUrl,
                            referer = "$mainUrl/",
                            subtitleCallback = subtitleCallback,
                            callback = { link ->
                                callback.invoke(link)
                            }
                        )
                    }
                }
            }
        }

        return true
    }
}

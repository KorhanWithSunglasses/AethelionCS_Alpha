package com.aethelion

import com.aethelion.DiziBoxParser.discoverServers
import com.aethelion.DiziBoxParser.extractPlayerIframes
import com.aethelion.DiziBoxParser.parseSearch
import com.aethelion.DiziBoxParser.parseSeriesDetail
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.mvvm.safeApiCall
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor

class DiziBoxProvider : MainAPI() {
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

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val targetUrl = if (page <= 1) request.data else "${request.data.removeSuffix("/")}/page/$page/"
        val document = app.get(targetUrl).document
        val items = parseSearch(document)
        return newHomePageResponse(
            listOf(HomePageList(request.name, items)),
            hasNext = items.isNotEmpty()
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchUrl = "$mainUrl/?s=$query"
        val document = app.get(searchUrl).document
        return parseSearch(document)
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        return parseSeriesDetail(document, url)
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        // 1. Fetch initial episode page
        val initialDoc = app.get(data).document

        // 2. Discover all server options dynamically from select.woca-linkpages-dd
        val serverOptions = discoverServers(initialDoc, data)

        // 3. Concurrently resolve each server mirror independently
        serverOptions.amap { server ->
            safeApiCall {
                val serverDoc = if (server.url == data) initialDoc else app.get(server.url).document
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

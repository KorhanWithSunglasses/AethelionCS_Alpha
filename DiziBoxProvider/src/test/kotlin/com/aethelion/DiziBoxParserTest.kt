package com.aethelion

import com.aethelion.DiziBoxParser.discoverServers
import com.aethelion.DiziBoxParser.extractPlayerIframes
import com.aethelion.DiziBoxParser.parseSearch
import com.aethelion.DiziBoxParser.parseSeriesDetail
import com.lagradost.cloudstream3.TvType
import kotlinx.coroutines.test.runTest
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test

class DiziBoxParserTest {

    private val provider = DiziBoxProvider()

    private val sampleEpisodeHtml = """
        <!DOCTYPE html>
        <html>
        <head><title>The Ghost in the Shell 1.Sezon 1.Bölüm - DiziBOX</title></head>
        <body>
            <div class="player-wrapper">
                <select class="woca-linkpages-dd" name="woca-linkpages-dd">
                    <option value="https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/" selected="selected">DBX Pro</option>
                    <option value="https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/2/">Moly+</option>
                    <option value="https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/3/">Odnok</option>
                </select>
                <div id="player">
                    <iframe src="https://vidmoly.to/embed-abc123xyz.html" width="100%" height="100%"></iframe>
                </div>
            </div>
        </body>
        </html>
    """.trimIndent()

    private val sampleSeriesHtml = """
        <!DOCTYPE html>
        <html>
        <head><title>The Ghost in the Shell izle | DiziBOX</title></head>
        <body>
            <div class="dizi-bilgi">
                <h1 class="dizi-adi">The Ghost in the Shell</h1>
                <div class="dizi-afis">
                    <img src="https://www.dizibox.live/posters/ghost-in-the-shell.jpg" alt="Poster" />
                </div>
                <div class="dizi-ozet">Gelecekte siber suçlarla mücadele eden özel bir ekip.</div>
                <span class="yil">Yapım Yılı: 2026</span>
                <span class="puan">7.8</span>
                <div class="kategoriler">
                    <a href="/kategori/aksiyon/">Aksiyon</a>
                    <a href="/kategori/bilimkurgu/">Bilim Kurgu</a>
                </div>
                <div class="oyuncular">
                    <a href="/oyuncu/motoko/">Motoko Kusanagi</a>
                    <a href="/oyuncu/batou/">Batou</a>
                </div>
            </div>
            <div class="sezon-bolumleri">
                <ul class="bolum-listesi">
                    <li><a href="https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/">1. Sezon 1. Bölüm</a></li>
                    <li><a href="https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-2-bolum-izle/">1. Sezon 2. Bölüm</a></li>
                </ul>
            </div>
        </body>
        </html>
    """.trimIndent()

    private val sampleSearchHtml = """
        <!DOCTYPE html>
        <html>
        <body>
            <div class="search-results">
                <article class="post-item">
                    <a href="https://www.dizibox.live/diziler/the-ghost-in-the-shell/" title="The Ghost in the Shell">
                        <img src="https://www.dizibox.live/posters/ghost.jpg" />
                    </a>
                    <h2 class="title">
                        <a href="https://www.dizibox.live/diziler/the-ghost-in-the-shell/">The Ghost in the Shell</a>
                    </h2>
                </article>
                <article class="post-item">
                    <a href="https://www.dizibox.live/diziler/chernobyl/" title="Chernobyl">
                        <img src="https://www.dizibox.live/posters/chernobyl.jpg" />
                    </a>
                    <h2 class="title">
                        <a href="https://www.dizibox.live/diziler/chernobyl/">Chernobyl</a>
                    </h2>
                </article>
            </div>
        </body>
        </html>
    """.trimIndent()

    @Test
    fun testDynamicServerDiscovery_SelectElement() {
        val doc = Jsoup.parse(sampleEpisodeHtml)
        val servers = discoverServers(doc, "https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/")

        assertEquals(3, servers.size)
        assertEquals("DBX Pro", servers[0].name)
        assertEquals("Moly+", servers[1].name)
        assertEquals("Odnok", servers[2].name)
    }

    @Test
    fun testDynamicServerDiscovery_DBXBaseRoute() {
        val doc = Jsoup.parse(sampleEpisodeHtml)
        val servers = discoverServers(doc, "https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/")

        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/", servers[0].url)
        assertFalse(servers[0].url.endsWith("/1/"))
    }

    @Test
    fun testDynamicServerDiscovery_MolyPlusRoute() {
        val doc = Jsoup.parse(sampleEpisodeHtml)
        val servers = discoverServers(doc, "https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/")

        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/2/", servers[1].url)
    }

    @Test
    fun testDynamicServerDiscovery_OdnokRoute() {
        val doc = Jsoup.parse(sampleEpisodeHtml)
        val servers = discoverServers(doc, "https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/")

        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/3/", servers[2].url)
    }

    @Test
    fun testDynamicServerDiscovery_FallbackDefault() {
        val doc = Jsoup.parse("<html><body><div>No player here</div></body></html>")
        val servers = discoverServers(doc, "https://www.dizibox.live/test-episode/")

        assertEquals(1, servers.size)
        assertEquals("DBX Pro", servers[0].name)
        assertEquals("https://www.dizibox.live/test-episode/", servers[0].url)
    }

    @Test
    fun testDynamicServerDiscovery_InvalidOptionFiltering() {
        val html = """
            <select class="woca-linkpages-dd">
                <option value="">Empty Value</option>
                <option value="   ">Blank Value</option>
                <option value="https://www.dizibox.live/ep/2/">Moly+</option>
            </select>
        """
        val doc = Jsoup.parse(html)
        val servers = discoverServers(doc, "https://www.dizibox.live/ep/")

        assertEquals(1, servers.size)
        assertEquals("Moly+", servers[0].name)
        assertEquals("https://www.dizibox.live/ep/2/", servers[0].url)
    }

    @Test
    fun testSearchParsing() {
        val doc = Jsoup.parse(sampleSearchHtml)
        val results = with(provider) { parseSearch(doc) }

        assertEquals(2, results.size)
        assertEquals("The Ghost in the Shell", results[0].name)
        assertEquals("https://www.dizibox.live/diziler/the-ghost-in-the-shell/", results[0].url)
        assertEquals(TvType.TvSeries, results[0].type)
        assertEquals("Chernobyl", results[1].name)
    }

    @Test
    fun testSeriesDetailParsing() = runTest {
        val doc = Jsoup.parse(sampleSeriesHtml)
        val response = with(provider) { parseSeriesDetail(doc, "https://www.dizibox.live/diziler/the-ghost-in-the-shell/") }

        assertEquals("The Ghost in the Shell", response.name)
        assertEquals(2026, response.year)
        assertEquals("Gelecekte siber suçlarla mücadele eden özel bir ekip.", response.plot)
        assertTrue(response.tags?.contains("Aksiyon") == true)
        assertTrue(response.tags?.contains("Bilim Kurgu") == true)
        assertEquals(2, response.episodes.size)
    }

    @Test
    fun testEpisodeIdentityIsolation() = runTest {
        val doc = Jsoup.parse(sampleSeriesHtml)
        val response = with(provider) { parseSeriesDetail(doc, "https://www.dizibox.live/diziler/the-ghost-in-the-shell/") }

        val ep1 = response.episodes[0]
        val ep2 = response.episodes[1]

        assertEquals(1, ep1.season)
        assertEquals(1, ep1.episode)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/", ep1.data)

        assertEquals(1, ep2.season)
        assertEquals(2, ep2.episode)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-2-bolum-izle/", ep2.data)

        assertNotEquals(ep1.data, ep2.data)
    }

    @Test
    fun testPlayerIframeExtraction() {
        val doc = Jsoup.parse(sampleEpisodeHtml)
        val iframes = extractPlayerIframes(doc)

        assertEquals(1, iframes.size)
        assertEquals("https://vidmoly.to/embed-abc123xyz.html", iframes[0])
    }
}

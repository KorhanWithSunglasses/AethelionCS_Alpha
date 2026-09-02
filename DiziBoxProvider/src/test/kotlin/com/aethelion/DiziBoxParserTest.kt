package com.aethelion

import com.aethelion.DiziBoxParser.discoverServers
import com.aethelion.DiziBoxParser.extractPlayerIframes
import com.aethelion.DiziBoxParser.fixUrl
import com.aethelion.DiziBoxParser.parseSearch
import com.aethelion.DiziBoxParser.parseSeriesDetail
import com.lagradost.cloudstream3.TvType
import kotlinx.coroutines.test.runTest
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test
import java.io.InputStreamReader

class DiziBoxParserTest {

    private val provider = DiziBoxProvider()

    private fun loadFixture(fixtureName: String): String {
        val stream = javaClass.classLoader?.getResourceAsStream("fixtures/$fixtureName")
            ?: throw IllegalArgumentException("Fixture not found: fixtures/$fixtureName")
        return InputStreamReader(stream, Charsets.UTF_8).readText()
    }

    // ============================================================
    // 1. UNIT TESTS: fixUrl() Determinism
    // ============================================================

    @Test
    fun testFixUrl_AbsoluteUrl_UNIT() {
        val input = "https://www.dizibox.live/dizi/the-ghost-in-the-shell/"
        val result = fixUrl(input)
        assertEquals("https://www.dizibox.live/dizi/the-ghost-in-the-shell/", result)
    }

    @Test
    fun testFixUrl_HttpUrl_UNIT() {
        val input = "http://vidmoly.to/embed-test.html"
        val result = fixUrl(input)
        assertEquals("http://vidmoly.to/embed-test.html", result)
    }

    @Test
    fun testFixUrl_ProtocolRelativeUrl_UNIT() {
        val input = "//cdn.dizibox.live/images/poster.jpg"
        val result = fixUrl(input)
        assertEquals("https://cdn.dizibox.live/images/poster.jpg", result)
    }

    @Test
    fun testFixUrl_RootRelativeUrl_UNIT() {
        val input = "/diziler/the-ghost-in-the-shell/"
        val result = fixUrl(input)
        assertEquals("https://www.dizibox.live/diziler/the-ghost-in-the-shell/", result)
    }

    @Test
    fun testFixUrl_RelativePath_UNIT() {
        val input = "the-ghost-in-the-shell-1-sezon-2-bolum-izle/2/"
        val result = fixUrl(input)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-2-bolum-izle/2/", result)
    }

    @Test
    fun testFixUrl_Blank_UNIT() {
        assertEquals("", fixUrl("   "))
    }

    // ============================================================
    // 2. FIXTURE TESTS: Real DiziBox Series Page
    // ============================================================

    @Test
    fun testRealSeriesDetailParsing_FIXTURE() = runTest {
        val html = loadFixture("series_the_ghost_in_the_shell.html")
        val doc = Jsoup.parse(html)
        val response = with(provider) { parseSeriesDetail(doc, "https://www.dizibox.live/diziler/the-ghost-in-the-shell/") }

        assertEquals("The Ghost in the Shell", response.name)
        assertEquals(2026, response.year)
        assertTrue(response.plot?.contains("Motoko Kusanagi") == true)
        assertTrue(response.tags?.contains("Aksiyon") == true)
        assertTrue(response.tags?.contains("Animasyon") == true)
        assertTrue(response.tags?.contains("Bilimkurgu") == true)

        assertEquals(9, response.episodes.size)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/", response.episodes[0].data)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-9-bolum-izle/", response.episodes[8].data)
    }

    // ============================================================
    // 3. FIXTURE TESTS: Episode Identity Isolation
    // ============================================================

    @Test
    fun testEpisodeIdentityIsolation_FIXTURE() = runTest {
        val html = loadFixture("series_the_ghost_in_the_shell.html")
        val doc = Jsoup.parse(html)
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

    // ============================================================
    // 4. FIXTURE TESTS: Dynamic Server Discovery (Real Episodes)
    // ============================================================

    @Test
    fun testDynamicServerDiscovery_EpisodeA_FIXTURE() {
        val html = loadFixture("episode_the_ghost_in_the_shell_s01e01.html")
        val doc = Jsoup.parse(html)
        val servers = discoverServers(doc, "https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/")

        assertEquals(3, servers.size)

        // Verify DBX Pro = BASE EPISODE URL (without /1/)
        assertEquals("DBX Pro", servers[0].name)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/", servers[0].url)
        assertFalse(servers[0].url.endsWith("/1/"))

        // Verify Moly+ = /2/
        assertEquals("Moly+", servers[1].name)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/2/", servers[1].url)

        // Verify Odnok = /3/
        assertEquals("Odnok", servers[2].name)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/3/", servers[2].url)
    }

    @Test
    fun testDynamicServerDiscovery_EpisodeB_FIXTURE() {
        val html = loadFixture("episode_the_ghost_in_the_shell_s01e02.html")
        val doc = Jsoup.parse(html)
        val servers = discoverServers(doc, "https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-2-bolum-izle/")

        assertEquals(3, servers.size)
        assertEquals("DBX Pro", servers[0].name)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-2-bolum-izle/", servers[0].url)
        assertEquals("Moly+", servers[1].name)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-2-bolum-izle/2/", servers[1].url)
        assertEquals("Odnok", servers[2].name)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-2-bolum-izle/3/", servers[2].url)
    }

    @Test
    fun testDynamicServerDiscovery_Chernobyl_FIXTURE() {
        val html = loadFixture("episode_chernobyl_s01e01.html")
        val doc = Jsoup.parse(html)
        val servers = discoverServers(doc, "https://www.dizibox.live/chernobyl-1-sezon-1-bolum-hd-izle/")

        assertEquals(3, servers.size)
        assertEquals("DBX Pro", servers[0].name)
        assertEquals("https://www.dizibox.live/chernobyl-1-sezon-1-bolum-hd-izle/", servers[0].url)
        assertEquals("Moly+", servers[1].name)
        assertEquals("https://www.dizibox.live/chernobyl-1-sezon-1-bolum-hd-izle/2/", servers[1].url)
        assertEquals("Odnok", servers[2].name)
        assertEquals("https://www.dizibox.live/chernobyl-1-sezon-1-bolum-hd-izle/3/", servers[2].url)
    }

    @Test
    fun testPlayerIframeExtraction_FIXTURE() {
        val html = loadFixture("episode_the_ghost_in_the_shell_s01e01.html")
        val doc = Jsoup.parse(html)
        val iframes = extractPlayerIframes(doc)

        assertEquals(1, iframes.size)
        assertEquals("https://vidmoly.to/embed-ghost101.html", iframes[0])
    }

    // ============================================================
    // 5. SYNTHETIC TESTS: Search & Fallback Validation
    // ============================================================

    @Test
    fun testSearchParsing_SYNTHETIC() {
        val sampleSearchHtml = """
            <div class="search-results">
                <article class="post-item">
                    <a href="https://www.dizibox.live/diziler/the-ghost-in-the-shell/" title="The Ghost in the Shell">
                        <img src="https://www.dizibox.live/posters/ghost.jpg" />
                    </a>
                    <h2 class="title">
                        <a href="https://www.dizibox.live/diziler/the-ghost-in-the-shell/">The Ghost in the Shell</a>
                    </h2>
                </article>
            </div>
        """.trimIndent()
        val doc = Jsoup.parse(sampleSearchHtml)
        val results = with(provider) { parseSearch(doc) }

        assertEquals(1, results.size)
        assertEquals("The Ghost in the Shell", results[0].name)
        assertEquals(TvType.TvSeries, results[0].type)
    }

    @Test
    fun testServerDiscoveryFallback_SYNTHETIC() {
        val doc = Jsoup.parse("<html><body><div>No server list</div></body></html>")
        val servers = discoverServers(doc, "https://www.dizibox.live/sample-episode/")

        assertEquals(1, servers.size)
        assertEquals("DBX Pro", servers[0].name)
        assertEquals("https://www.dizibox.live/sample-episode/", servers[0].url)
    }
}

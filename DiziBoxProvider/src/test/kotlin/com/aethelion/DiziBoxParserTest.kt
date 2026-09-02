package com.aethelion

import com.aethelion.DiziBoxParser.discoverServers
import com.aethelion.DiziBoxParser.extractPlayerIframes
import com.aethelion.DiziBoxParser.fixUrl
import com.aethelion.DiziBoxParser.isEpisodeUrl
import com.aethelion.DiziBoxParser.parseEpisodeDetail
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
    // 1. UNIT TESTS: fixUrl() & isEpisodeUrl() Determinism
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

    @Test
    fun testIsEpisodeUrl_Detection_UNIT() {
        assertTrue(isEpisodeUrl("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/"))
        assertTrue(isEpisodeUrl("https://www.dizibox.live/chernobyl-1-sezon-1-bolum-hd-izle/2/"))
        assertFalse(isEpisodeUrl("https://www.dizibox.live/diziler/the-ghost-in-the-shell/"))
        assertFalse(isEpisodeUrl("https://www.dizibox.live/populer-diziler/"))
    }

    // ============================================================
    // 2. FIXTURE TESTS: Homepage & Search Sanitized Fixtures
    // ============================================================

    @Test
    fun testHomepageParsing_FIXTURE() {
        val html = loadFixture("homepage_dizibox.html")
        val doc = Jsoup.parse(html)
        val results = with(provider) { parseSearch(doc) }

        assertEquals(4, results.size)
        assertEquals("Harry Potter", results[0].name)
        assertEquals("https://www.dizibox.live/diziler/harry-potter/", results[0].url)
        assertEquals("The Ghost in the Shell", results[3].name)
        assertEquals("https://www.dizibox.live/diziler/the-ghost-in-the-shell/", results[3].url)
    }

    @Test
    fun testSearchParsing_FIXTURE() {
        val html = loadFixture("search_dizibox.html")
        val doc = Jsoup.parse(html)
        val results = with(provider) { parseSearch(doc) }

        assertEquals(3, results.size)
        assertEquals("The Ghost in the Shell", results[0].name)
        assertEquals("https://www.dizibox.live/diziler/the-ghost-in-the-shell/", results[0].url)
        assertEquals("City of Ghosts", results[1].name)
        assertEquals("Ghosts of Beirut", results[2].name)
    }

    // ============================================================
    // 3. FIXTURE TESTS: Series Load vs Episode Load
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

    @Test
    fun testRealEpisodeDirectLoad_FIXTURE() = runTest {
        val html = loadFixture("episode_the_ghost_in_the_shell_s01e01.html")
        val doc = Jsoup.parse(html)
        val response = with(provider) { parseEpisodeDetail(doc, "https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/") }

        assertEquals("The Ghost in the Shell", response.name)
        assertEquals(1, response.episodes.size)
        assertEquals(1, response.episodes[0].season)
        assertEquals(1, response.episodes[0].episode)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/", response.episodes[0].data)
    }

    // ============================================================
    // 4. FIXTURE TESTS: Episode Identity Isolation
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
    // 5. FIXTURE TESTS: Dynamic Server Discovery (Real Episodes)
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

    @Test
    fun testProviderHeaders_UserAgent_UNIT() {
        assertEquals(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            DiziBoxProvider.USER_AGENT
        )
        assertTrue(DiziBoxProvider.requestHeaders.containsKey("User-Agent"))
        assertEquals(DiziBoxProvider.USER_AGENT, DiziBoxProvider.requestHeaders["User-Agent"])
    }
}


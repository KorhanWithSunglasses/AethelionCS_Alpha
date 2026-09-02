package com.aethelion

import com.aethelion.DiziBoxParser.discoverServers
import com.aethelion.DiziBoxParser.extractPlayerIframes
import com.aethelion.DiziBoxParser.fixUrl
import com.aethelion.DiziBoxParser.isEpisodeUrl
import com.aethelion.DiziBoxParser.parseEpisodeDetail
import com.aethelion.DiziBoxParser.parseEpisodes
import com.aethelion.DiziBoxParser.parseSearch
import com.aethelion.DiziBoxParser.parseSeries
import com.aethelion.DiziBoxParser.parseSeriesDetail
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
    // UNIT TESTS: URL Helpers & Configuration
    // ============================================================

    @Test
    fun testFixUrl_AbsoluteUrl_UNIT() {
        val input = "https://www.dizibox.live/dizi/the-ghost-in-the-shell/"
        assertEquals("https://www.dizibox.live/dizi/the-ghost-in-the-shell/", fixUrl(input))
    }

    @Test
    fun testFixUrl_ProtocolRelativeUrl_UNIT() {
        val input = "//cdn.dizibox.live/images/poster.jpg"
        assertEquals("https://cdn.dizibox.live/images/poster.jpg", fixUrl(input))
    }

    @Test
    fun testFixUrl_RootRelativeUrl_UNIT() {
        val input = "/diziler/the-ghost-in-the-shell/"
        assertEquals("https://www.dizibox.live/diziler/the-ghost-in-the-shell/", fixUrl(input))
    }

    @Test
    fun testIsEpisodeUrl_Detection_UNIT() {
        assertTrue(isEpisodeUrl("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/"))
        assertTrue(isEpisodeUrl("https://www.dizibox.live/chernobyl-1-sezon-1-bolum-hd-izle/2/"))
        assertFalse(isEpisodeUrl("https://www.dizibox.live/diziler/the-ghost-in-the-shell/"))
        assertFalse(isEpisodeUrl("https://www.dizibox.live/populer-diziler/"))
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

    // ============================================================
    // 10 DETERMINISTIC PARSER TESTS
    // ============================================================

    // TEST 1: Homepage parses real episode cards.
    @Test
    fun test1_HomepageParsesRealEpisodeCards_FIXTURE() {
        val html = loadFixture("homepage_dizibox.html")
        val doc = Jsoup.parse(html)
        val episodes = with(provider) { parseEpisodes(doc) }

        assertEquals(4, episodes.size)
        assertTrue(episodes[0].name.contains("Ted Lasso"))
        assertTrue(episodes[0].url.contains("ted-lasso-4-sezon-5-bolum-izle"))
        assertTrue(episodes[1].name.contains("The Ghost in the Shell"))
        assertTrue(episodes[1].url.contains("the-ghost-in-the-shell-1-sezon-9-bolum-izle"))
    }

    // TEST 2: Homepage parses real series posters.
    @Test
    fun test2_HomepageParsesRealSeriesPosters_FIXTURE() {
        val html = loadFixture("homepage_dizibox.html")
        val doc = Jsoup.parse(html)
        val series = with(provider) { parseSeries(doc) }

        assertEquals(4, series.size)
        assertEquals("The Ghost in the Shell", series[0].name)
        assertEquals("https://www.dizibox.live/diziler/the-ghost-in-the-shell/", series[0].url)
        assertEquals("The Five-Star Weekend", series[1].name)
    }

    // TEST 3: Homepage excludes navigation/menu junk.
    @Test
    fun test3_HomepageExcludesNavigationMenuJunk_FIXTURE() {
        val html = loadFixture("homepage_dizibox.html")
        val doc = Jsoup.parse(html)
        val allItems = with(provider) { parseSearch(doc) }

        val forbiddenJunk = listOf("dizibox.", "king of the tv series", "Arama yapın", "arşiv", "dizi takvimi", "yardım", "KAYIT OL", "GİRİŞ")
        allItems.forEach { item ->
            forbiddenJunk.forEach { junk ->
                assertFalse("Found junk text in parsed item: $junk", item.name.equals(junk, ignoreCase = true))
            }
        }
    }

    // TEST 4: Lazy-loaded poster data-src is used correctly.
    @Test
    fun test4_LazyLoadedPosterDataSrcUsedCorrectly_FIXTURE() {
        val html = loadFixture("homepage_dizibox.html")
        val doc = Jsoup.parse(html)
        val series = with(provider) { parseSeries(doc) }

        val ghost = series.first { it.name == "The Ghost in the Shell" }
        assertNotNull(ghost.posterUrl)
        assertTrue("Poster should not be placeholder data URI", !ghost.posterUrl!!.startsWith("data:"))
        assertEquals("https://www.dizibox.live/wp-content/uploads/afisler/the-ghost-in-the-shell-200x290.jpg", ghost.posterUrl)
    }

    // TEST 5: Search returns correct title/url/poster.
    @Test
    fun test5_SearchReturnsCorrectTitleUrlPoster_FIXTURE() {
        val html = loadFixture("search_dizibox.html")
        val doc = Jsoup.parse(html)
        val results = with(provider) { parseSearch(doc) }

        assertTrue(results.isNotEmpty())
        val ghost = results.first { it.name == "The Ghost in the Shell" }
        assertEquals("https://www.dizibox.live/diziler/the-ghost-in-the-shell/", ghost.url)
        assertNotNull(ghost.posterUrl)
        assertTrue(ghost.posterUrl!!.contains("the-ghost-in-the-shell"))
    }

    // TEST 6: Series detail parses metadata.
    @Test
    fun test6_SeriesDetailParsesMetadata_FIXTURE() = runTest {
        val html = loadFixture("series_the_ghost_in_the_shell.html")
        val doc = Jsoup.parse(html)
        val response = with(provider) { parseSeriesDetail(doc, "https://www.dizibox.live/diziler/the-ghost-in-the-shell/") }

        assertEquals("The Ghost in the Shell", response.name)
        assertEquals(2026, response.year)
        assertNotNull(response.plot)
        assertTrue(response.plot!!.contains("Motoko Kusanagi"))
        assertNotNull(response.tags)
        assertTrue(response.tags!!.contains("Aksiyon"))
        assertTrue(response.tags!!.contains("Animasyon"))
        assertTrue(response.tags!!.contains("Bilimkurgu"))
        assertEquals("https://www.dizibox.live/wp-content/uploads/afisler/the-ghost-in-the-shell-200x290.jpg", response.posterUrl)
    }

    // TEST 7: Series detail parses episodes.
    @Test
    fun test7_SeriesDetailParsesEpisodes_FIXTURE() = runTest {
        val html = loadFixture("series_the_ghost_in_the_shell.html")
        val doc = Jsoup.parse(html)
        val response = with(provider) { parseSeriesDetail(doc, "https://www.dizibox.live/diziler/the-ghost-in-the-shell/") }

        assertEquals(9, response.episodes.size)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/", response.episodes[0].data)
        assertEquals(1, response.episodes[0].season)
        assertEquals(1, response.episodes[0].episode)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-9-bolum-izle/", response.episodes[8].data)
        assertEquals(1, response.episodes[8].season)
        assertEquals(9, response.episodes[8].episode)
    }

    // TEST 8: E01 and E02 have different data values.
    @Test
    fun test8_EpisodeIdentityIsolation_FIXTURE() = runTest {
        val html = loadFixture("series_the_ghost_in_the_shell.html")
        val doc = Jsoup.parse(html)
        val response = with(provider) { parseSeriesDetail(doc, "https://www.dizibox.live/diziler/the-ghost-in-the-shell/") }

        val ep1 = response.episodes[0]
        val ep2 = response.episodes[1]

        assertNotEquals(ep1.data, ep2.data)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/", ep1.data)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-2-bolum-izle/", ep2.data)
    }

    // TEST 9: Dynamic server discovery finds all currently available server options.
    @Test
    fun test9_DynamicServerDiscoveryFindsAllOptions_FIXTURE() {
        val html = loadFixture("episode_the_ghost_in_the_shell_s01e01.html")
        val doc = Jsoup.parse(html)
        val servers = discoverServers(doc, "https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/")

        assertEquals(3, servers.size)
        assertEquals("DBX Pro", servers[0].name)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/", servers[0].url)
        assertEquals("Moly+", servers[1].name)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/2/", servers[1].url)
        assertEquals("Odnok", servers[2].name)
        assertEquals("https://www.dizibox.live/the-ghost-in-the-shell-1-sezon-1-bolum-izle/3/", servers[2].url)
    }

    // TEST 10: Parser does not rely on hardcoded server names.
    @Test
    fun test10_DynamicServerDiscoveryNotRelyOnHardcodedNames_UNIT() {
        val syntheticHtml = """
            <html><body>
                <select class="woca-linkpages-dd">
                    <option value="https://www.dizibox.live/test-ep/alpha/">CustomStreamAlpha</option>
                    <option value="https://www.dizibox.live/test-ep/beta/">OmegaPlayer</option>
                    <option value="https://www.dizibox.live/test-ep/gamma/">QuantumCDN</option>
                </select>
            </body></html>
        """.trimIndent()
        val doc = Jsoup.parse(syntheticHtml)
        val servers = discoverServers(doc, "https://www.dizibox.live/test-ep/")

        assertEquals(3, servers.size)
        assertEquals("CustomStreamAlpha", servers[0].name)
        assertEquals("https://www.dizibox.live/test-ep/alpha/", servers[0].url)
        assertEquals("OmegaPlayer", servers[1].name)
        assertEquals("https://www.dizibox.live/test-ep/beta/", servers[1].url)
        assertEquals("QuantumCDN", servers[2].name)
        assertEquals("https://www.dizibox.live/test-ep/gamma/", servers[2].url)
    }
}

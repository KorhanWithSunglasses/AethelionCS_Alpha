# AETHELIONCS: ACCEPTANCE & PLAYBACK IDENTITY TEST PLAN

============================================================
1. PLAYBACK IDENTITY TEST PLAN (PRODUCTION PATH)
============================================================

To eliminate the "same video for multiple episodes" failure mode observed in legacy projects, the test suite must execute the real production path across two distinct episodes:

```text
TEST CASE: EPISODE_IDENTITY_INTEGRATION_TEST

STEP 1: Load Series
  Target: "https://www.dizibox.live/dizi/breaking-bad/"
  Action: Invoke DiziBoxProvider.load(seriesUrl)
  Verify: episodes.size >= 2
  Extract:
    Episode 1: ep1 = episodes.first { it.season == 1 && it.episode == 1 }
    Episode 2: ep2 = episodes.first { it.season == 1 && it.episode == 2 }
  Assertion: ep1.data != ep2.data

STEP 2: Resolve Episode 1 Links
  Action: Invoke DiziBoxProvider.loadLinks(ep1.data, isCasting = false, subtitleCallback, callback)
  Capture: links_ep1 = List<ExtractorLink>, subs_ep1 = List<SubtitleFile>
  Verify: links_ep1 isNotEmpty

STEP 3: Resolve Episode 2 Links
  Action: Invoke DiziBoxProvider.loadLinks(ep2.data, isCasting = false, subtitleCallback, callback)
  Capture: links_ep2 = List<ExtractorLink>, subs_ep2 = List<SubtitleFile>
  Verify: links_ep2 isNotEmpty

STEP 4: Identity Verification
  Verify: links_ep1.map { it.url }.toSet() intersect links_ep2.map { it.url }.toSet() == emptySet()
  Result: Proves Episode 1 and Episode 2 resolve to completely distinct, non-overlapping stream URLs.
```

============================================================
2. ERROR TAXONOMY & FAILURE BOUNDARY CHECKS
============================================================

| Failure Layer | Observable Condition | Provider Handling |
|---|---|---|
| **HTTP 403 / Cloudflare** | WAF Challenge Block | CloudStream WebView challenge clears session |
| **HTTP 404 / Broken Series** | Series deleted or renamed | Returns `null` or throws clean descriptive exception |
| **Dead Server / Iframe** | Individual host down (503/404) | `safeApiCall` isolates error, remaining servers resolve |
| **Dead Extractor** | Host changed DOM/obfuscation | Extractor fails safely; remaining extractors proceed |
| **Corrupted Media Stream** | Video file missing on CDN | Player error; provider metadata remains intact |

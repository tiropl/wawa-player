package com.wawa_player.android.tv.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UrlUtilTest {
    @Test public void extractsNormalizedComponents() {
        assertEquals("https", UrlUtil.scheme(" HTTPS://Example.COM/a/b.m3u8 "));
        assertEquals("example.com", UrlUtil.host("https://Example.COM/a/b.m3u8"));
        assertEquals("b.m3u8", UrlUtil.path("https://Example.COM/a/b.m3u8?token=x"));
    }

    @Test public void handlesFileAndFallbackNames() {
        assertEquals("file", UrlUtil.scheme("/tmp/video.mp4"));
        assertEquals("video.mp4", UrlUtil.getName("file:///tmp/video.mp4"));
        assertEquals("example.com", UrlUtil.getName("https://Example.COM/"));
        assertEquals("plain", UrlUtil.getName("plain"));
    }

    @Test public void canonicalizesHeadersCaseInsensitively() {
        assertEquals("User-Agent", UrlUtil.fixHeader("user-agent"));
        assertEquals("Referer", UrlUtil.fixHeader("REFERER"));
        assertEquals("Cookie", UrlUtil.fixHeader("cookie"));
        assertEquals("X-Test", UrlUtil.fixHeader("X-Test"));
    }

    @Test public void resolvesReferences() {
        assertEquals("https://example.com/dir/next.m3u8", UrlUtil.resolve("https://example.com/dir/list.m3u8", "next.m3u8"));
        assertEquals("https://example.com/other", UrlUtil.resolve("https://example.com/dir/list", "/other"));
    }
}

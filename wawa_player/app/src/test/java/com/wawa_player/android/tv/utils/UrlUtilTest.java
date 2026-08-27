package com.wawa_player.android.tv.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class UrlUtilTest {
    @Test
    public void extractsNormalizedSchemeHostAndPath() {
        assertThat(UrlUtil.scheme("HTTPS://Example.COM/live.m3u8")).isEqualTo("https");
        assertThat(UrlUtil.host("HTTPS://Example.COM/live.m3u8")).isEqualTo("example.com");
        assertThat(UrlUtil.path("https://example.com/live.m3u8?token=abc#part")).isEqualTo("live.m3u8");
    }

    @Test
    public void returnsEmptyPartsForNullAndNormalizesHeaders() {
        assertThat(UrlUtil.scheme((String) null)).isEmpty();
        assertThat(UrlUtil.host((String) null)).isEmpty();
        assertThat(UrlUtil.path((String) null)).isEmpty();
        assertThat(UrlUtil.scheme("")).isEmpty();
        assertThat(UrlUtil.fixHeader("user-agent")).isEqualTo("User-Agent");
        assertThat(UrlUtil.fixHeader("REFERER")).isEqualTo("Referer");
        assertThat(UrlUtil.fixHeader("cookie")).isEqualTo("Cookie");
        assertThat(UrlUtil.fixHeader("X-Test")).isEqualTo("X-Test");
        assertThat(UrlUtil.fixHeader(null)).isNull();
    }

    @Test
    public void uriRemovesBackslashesAndGetNameUsesPathThenHost() {
        assertThat(UrlUtil.uri("https:\\\\example.com\\live.m3u8").toString())
                .isEqualTo("https:example.comlive.m3u8");
        assertThat(UrlUtil.getName("https://example.com/live.m3u8")).isEqualTo("live.m3u8");
        assertThat(UrlUtil.getName("https://example.com")).isEqualTo("example.com");
        assertThat(UrlUtil.host("example.com/path")).isEmpty();
    }

    @Test
    public void resolveHandlesRelativeReferences() {
        assertThat(UrlUtil.resolve("https://example.com/a/", "b.m3u8"))
                .isEqualTo("https://example.com/a/b.m3u8");
        assertThat(UrlUtil.resolve("https://example.com/a/main.m3u8", "../b.m3u8"))
                .isEqualTo("https://example.com/b.m3u8");
    }
}

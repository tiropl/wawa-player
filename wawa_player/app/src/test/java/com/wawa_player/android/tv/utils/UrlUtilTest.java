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
        assertThat(UrlUtil.path("https://example.com/live.m3u8?token=abc")).isEqualTo("live.m3u8");
    }

    @Test
    public void returnsEmptyPartsForNullAndNormalizesHeaders() {
        assertThat(UrlUtil.scheme((String) null)).isEmpty();
        assertThat(UrlUtil.host((String) null)).isEmpty();
        assertThat(UrlUtil.path((String) null)).isEmpty();
        assertThat(UrlUtil.fixHeader("user-agent")).isEqualTo("User-Agent");
        assertThat(UrlUtil.fixHeader("REFERER")).isEqualTo("Referer");
        assertThat(UrlUtil.fixHeader("cookie")).isEqualTo("Cookie");
        assertThat(UrlUtil.fixHeader("X-Test")).isEqualTo("X-Test");
    }

    @Test
    public void extractsFileNameOrHostAsName() {
        assertThat(UrlUtil.getName("https://example.com/live.m3u8")).isEqualTo("live.m3u8");
        assertThat(UrlUtil.getName("https://example.com")).isEqualTo("example.com");
    }
}

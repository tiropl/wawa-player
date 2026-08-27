package com.wawa_player.android.tv.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.text.Spanned;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class SnifferTest {
    @Test public void extractsPushProtocols() {
        assertEquals("magnet:?xt=urn:btih:abc", Sniffer.getUrl("watch magnet:?xt=urn:btih:abc now"));
        assertEquals("https://example.com/api?$json", Sniffer.getUrl("https://example.com/api?$json"));
    }

    @Test public void detectsCommonVideoUrlsAndExcludesWrappers() {
        assertTrue(Sniffer.isVideoFormat("https://cdn.example.com/video/movie.m3u8"));
        assertTrue(Sniffer.isVideoFormat("rtmp://stream.example/live"));
        assertFalse(Sniffer.isVideoFormat("https://example.com/page.html"));
        assertFalse(Sniffer.isVideoFormat("https://example.com/watch?v=https://cdn.example.com/x.mp4"));
    }

    @Test public void buildsClickableTextAndPreservesPlainText() {
        var value = Sniffer.buildClickable("A [a=cr:{\"url\":\"x\"}/]Go[/a] B", result -> new android.text.style.ClickableSpan() {
            public void onClick(android.view.View widget) { }
        });
        assertEquals("A Go B", value.toString());
        assertEquals(1, value.getSpans(0, value.length(), android.text.style.ClickableSpan.class).length);
        assertEquals(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE, value.getSpanFlags(value.getSpans(0, value.length(), android.text.style.ClickableSpan.class)[0]));
    }
}

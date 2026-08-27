package com.wawa_player.android.tv.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilTest {
    @Test public void parsesEpisodeNumbers() {
        assertEquals(12, Util.getNumber("Show S01E12 1080p"));
        assertEquals(3, Util.getNumber("第03集"));
        assertEquals(2024, Util.getNumber("release 2024"));
        assertEquals(-1, Util.getNumber("title"));
    }

    @Test public void cleansHtmlAndTrimsSuffixes() {
        assertEquals("hello\nworld", Util.clean(" <b>hello</b>\n world "));
        assertEquals("abc", Util.substring("abcd", 1));
        assertEquals("ab", Util.substring("abcd", 2));
        assertEquals("x", Util.substring("x", 1));
    }

    @Test public void formatsPlaybackTime() {
        assertEquals("01:05", Util.timeMs(65000));
        assertEquals("01:01:05", Util.timeMs(3665000));
    }
}

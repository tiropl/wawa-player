package com.wawa_player.android.tv.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SubtitleArchiveTest {
    @Test public void recognizesSubtitleExtensionsAndUrls() {
        assertTrue(SubtitleArchive.isSupported("caption.SRT", ""));
        assertTrue(SubtitleArchive.isSupported("", "https://cdn.test/sub.vtt?token=1#x"));
        assertTrue(SubtitleArchive.isZip("episode", "https://cdn.test/sub.ZIP?x=1"));
        assertFalse(SubtitleArchive.isSupported("movie.mp4", "https://cdn.test/movie.mp4"));
    }

    @Test public void rejectsExtensionLookalikesAndEmptyNames() {
        assertFalse(SubtitleArchive.isSupported("caption.srt.bak", ""));
        assertFalse(SubtitleArchive.isSupported("", ""));
        assertFalse(SubtitleArchive.isZip("archive.zip.tmp", ""));
    }
}

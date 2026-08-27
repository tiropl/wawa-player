package com.wawa_player.android.tv.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AssrtResponseTest {

    @Test
    public void fromReadsErrorAndSubtitleFields() {
        AssrtResponse response = AssrtResponse.from("{\"status\":1,\"errmsg\":\"  failed  \",\"sub\":{\"subs\":[{\"id\":7,\"native_name\":\" Native \",\"title\":\" Title \",\"filename\":\" file.srt \",\"videoname\":\" Video \",\"url\":\" https://example.test/sub \",\"lang\":{\"desc\":\" English \"},\"filelist\":{\"f\":\"file.srt\",\"url\":\"https://example.test/file\"}}]}}");

        assertEquals("failed", response.getError());
        assertEquals(1, response.getSubtitles().size());
        AssrtResponse.Subtitle subtitle = response.getSubtitles().get(0);
        assertEquals(7, subtitle.getId());
        assertEquals("Native", subtitle.getNativeName());
        assertEquals("Title", subtitle.getTitle());
        assertEquals("file.srt", subtitle.getFileName());
        assertEquals("Video", subtitle.getVideoName());
        assertEquals("https://example.test/sub", subtitle.getUrl());
        assertEquals("English", subtitle.getLanguage());
        assertTrue(subtitle.hasFileList());
        assertEquals(1, subtitle.getFiles().size());
        assertEquals("file.srt", subtitle.getFiles().get(0).getName());
        assertEquals("https://example.test/file", subtitle.getFiles().get(0).getUrl());
    }

    @Test
    public void fromSupportsAlternateFieldsAndLanguageForms() {
        AssrtResponse response = AssrtResponse.from("{\"status\":0,\"message\":\"ignored\",\"sub\":{\"subs\":[{\"lang\":\" Chinese \",\"filelist\":[{\"name\":\"a.ass\"},{\"filename\":\"b.srt\"}]}]}}");

        assertEquals("", response.getError());
        AssrtResponse.Subtitle subtitle = response.getSubtitles().get(0);
        assertEquals("Chinese", subtitle.getLanguage());
        assertEquals(2, subtitle.getFiles().size());
        assertEquals("a.ass", subtitle.getFiles().get(0).getName());
        assertEquals("b.srt", subtitle.getFiles().get(1).getName());
    }

    @Test
    public void nullResponseUsesSafeDefaults() {
        AssrtResponse empty = AssrtResponse.from("null");

        assertEquals("", empty.getError());
        assertTrue(empty.getSubtitles().isEmpty());
        assertFalse(empty.getSubtitles() == null);
    }
}

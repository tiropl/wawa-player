package com.wawa_player.android.tv.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class SubtitleSearchItemTest {

    @Test
    public void fromExposesValuesAndDerivedProperties() {
        SubtitleSearchItem item = SubtitleSearchItem.from(12, "  English.srt  ", "https://example.test/sub", " en ");

        assertEquals(12, item.getId());
        assertEquals("  English.srt  ", item.getName());
        assertEquals("https://example.test/sub", item.getUrl());
        assertEquals(" en ", item.getLang());
        assertEquals("  English.srt   -  en ", item.getText());
        assertTrue(item.hasUrl());
        assertFalse(item.isZip());
        assertEquals("  English.srt  ", item.toSub().getName());
        assertEquals("https://example.test/sub", item.toSub().getUrl());
        assertEquals(" en ", item.toSub().getLang());
    }

    @Test
    public void emptyValuesUseDefaults() {
        SubtitleSearchItem item = SubtitleSearchItem.from(3, "", "", "");

        assertEquals("3", item.getName());
        assertEquals("", item.getUrl());
        assertEquals("", item.getLang());
        assertEquals("3", item.getText());
        assertFalse(item.hasUrl());
        assertFalse(item.isZip());
    }

    @Test
    public void fromFilesCreatesLocalItems() {
        File first = new File("movie.srt");
        File second = new File("captions.zip");
        List<SubtitleSearchItem> items = SubtitleSearchItem.fromFiles(Arrays.asList(first, second), "en");

        assertEquals(2, items.size());
        assertEquals("movie.srt", items.get(0).getName());
        assertEquals(first.getAbsolutePath(), items.get(0).getUrl());
        assertEquals("en", items.get(0).getLang());
        assertTrue(items.get(1).isZip());
    }
}

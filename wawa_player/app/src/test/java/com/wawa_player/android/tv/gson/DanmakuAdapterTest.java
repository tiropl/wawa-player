package com.wawa_player.android.tv.gson;

import com.wawa_player.android.tv.bean.Danmaku;
import com.wawa_player.android.tv.bean.Result;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class DanmakuAdapterTest {



    @Test
    public void deserializePrimitivePathCreatesDanmaku() {
        Result result = Result.objectFrom("{\"danmaku\":\" https://example.com/danmaku.xml \"}");

        assertEquals(1, result.getDanmaku().size());
        assertEquals("https://example.com/danmaku.xml", result.getDanmaku().get(0).getUrl());
        assertEquals("https://example.com/danmaku.xml", result.getDanmaku().get(0).getName());
    }

    @Test
    public void deserializePrimitiveJsonArrayAndFiltersEmptyEntries() {
        Result result = Result.objectFrom("{\"danmaku\":\"[{\\\"name\\\":\\\"One\\\",\\\"url\\\":\\\"one.xml\\\"},{\\\"name\\\":\\\"Two\\\",\\\"url\\\":\\\"two.xml\\\"},{\\\"name\\\":\\\"empty\\\",\\\"url\\\":\\\"\\\"}]\"}");

        List<Danmaku> items = result.getDanmaku();
        assertEquals(2, items.size());
        assertEquals("one.xml", items.get(0).getUrl());
        assertEquals("Two", items.get(1).getName());
        assertEquals("two.xml", items.get(1).getUrl());
    }

    @Test
    public void deserializeArrayPreservesObjectsAndFiltersEmptyEntries() {
        Result result = Result.objectFrom("{\"danmaku\":[{\"name\":\"A\",\"url\":\"a.xml\"},{\"name\":\"empty\",\"url\":\"\"}]}");

        assertEquals(1, result.getDanmaku().size());
        assertEquals("a.xml", result.getDanmaku().get(0).getUrl());
    }

    @Test
    public void deserializeEmptyArrayReturnsEmptyList() {
        assertEquals(0, Result.objectFrom("{\"danmaku\":[]}").getDanmaku().size());
    }

    @Test
    public void deserializeMixedArrayFiltersInvalidEntries() {
        Result result = Result.objectFrom("{\"danmaku\":[{\"url\":\"ok.xml\"},{\"url\":\"\"},{\"name\":\"missing-url\"}]}");

        assertEquals(1, result.getDanmaku().size());
        assertEquals("ok.xml", result.getDanmaku().get(0).getUrl());
    }

    @Test
    public void deserializeJsonNullReturnsEmptyListThroughResult() {
        assertTrue(Result.objectFrom("{\"danmaku\":null}").getDanmaku().isEmpty());
    }

    @Test
    public void deserializeEmptyPrimitiveIsFilteredOut() {
        assertTrue(Result.objectFrom("{\"danmaku\":\"\"}").getDanmaku().isEmpty());
    }

    @Test
    public void deserializeObjectFailureIsConvertedToEmptyResult() {
        assertTrue(Result.objectFrom("{\"danmaku\":{\"url\":\"one.xml\"}}").getDanmaku().isEmpty());
    }
}

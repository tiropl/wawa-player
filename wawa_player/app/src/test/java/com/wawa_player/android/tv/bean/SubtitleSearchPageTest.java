package com.wawa_player.android.tv.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class SubtitleSearchPageTest {

    @Test
    public void fromRetainsItemsAndCount() {
        List<SubtitleSearchItem> items = Collections.singletonList(SubtitleSearchItem.from(1, "sub.srt", "url", "en"));
        SubtitleSearchPage page = SubtitleSearchPage.from(items, 9);

        assertSame(items, page.getItems());
        assertEquals(9, page.getResultCount());
        assertEquals(1, page.getItems().size());
    }

    @Test
    public void fromAllowsEmptyOrNullItems() {
        assertEquals(0, SubtitleSearchPage.from(Collections.emptyList(), 0).getResultCount());
        assertEquals(null, SubtitleSearchPage.from(null, 2).getItems());
    }
}

package com.wawa_player.android.tv.browse;

import static com.google.common.truth.Truth.assertThat;

import androidx.media3.common.MediaItem;

import com.google.common.collect.ImmutableList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class BrowseTreeTest {

    // --- wrapIndex tests ---

    @Test
    public void wrapIndex_forward() {
        assertThat(BrowseTree.wrapIndex(0, 1, 5)).isEqualTo(1);
        assertThat(BrowseTree.wrapIndex(3, 1, 5)).isEqualTo(4);
    }

    @Test
    public void wrapIndex_backward() {
        assertThat(BrowseTree.wrapIndex(4, -1, 5)).isEqualTo(3);
        assertThat(BrowseTree.wrapIndex(0, -1, 5)).isEqualTo(4);
    }

    @Test
    public void wrapIndex_wrapAroundForward() {
        assertThat(BrowseTree.wrapIndex(4, 1, 5)).isEqualTo(0);
        assertThat(BrowseTree.wrapIndex(4, 6, 5)).isEqualTo(0);
    }

    @Test
    public void wrapIndex_wrapAroundBackward() {
        assertThat(BrowseTree.wrapIndex(0, -1, 5)).isEqualTo(4);
        assertThat(BrowseTree.wrapIndex(0, -6, 5)).isEqualTo(4);
    }

    @Test
    public void wrapIndex_largeDelta() {
        assertThat(BrowseTree.wrapIndex(0, 17, 5)).isEqualTo(2);
        assertThat(BrowseTree.wrapIndex(0, -17, 5)).isEqualTo(3);
    }

    @Test
    public void wrapIndex_singleItem() {
        assertThat(BrowseTree.wrapIndex(0, 1, 1)).isEqualTo(0);
        assertThat(BrowseTree.wrapIndex(0, -1, 1)).isEqualTo(0);
    }

    @Test
    public void wrapIndex_zeroDelta() {
        assertThat(BrowseTree.wrapIndex(3, 0, 5)).isEqualTo(3);
    }

    // --- page tests ---

    private ImmutableList<MediaItem> makeItems(int count) {
        ImmutableList.Builder<MediaItem> builder = ImmutableList.builder();
        for (int i = 0; i < count; i++) {
            builder.add(new MediaItem.Builder().setMediaId("item_" + i).build());
        }
        return builder.build();
    }

    @Test
    public void page_normalPagination() {
        ImmutableList<MediaItem> items = makeItems(10);
        ImmutableList<MediaItem> page0 = BrowseTree.page(items, 0, 3);
        assertThat(page0).hasSize(3);
        assertThat(page0.get(0).mediaId).isEqualTo("item_0");
        assertThat(page0.get(2).mediaId).isEqualTo("item_2");

        ImmutableList<MediaItem> page1 = BrowseTree.page(items, 1, 3);
        assertThat(page1).hasSize(3);
        assertThat(page1.get(0).mediaId).isEqualTo("item_3");
    }

    @Test
    public void page_lastPartialPage() {
        ImmutableList<MediaItem> items = makeItems(10);
        ImmutableList<MediaItem> lastPage = BrowseTree.page(items, 3, 3);
        assertThat(lastPage).hasSize(1);
        assertThat(lastPage.get(0).mediaId).isEqualTo("item_9");
    }

    @Test
    public void page_pageBeyondSize_returnsEmpty() {
        ImmutableList<MediaItem> items = makeItems(5);
        ImmutableList<MediaItem> result = BrowseTree.page(items, 10, 3);
        assertThat(result).isEmpty();
    }

    @Test
    public void page_zeroPageSize_returnsAll() {
        ImmutableList<MediaItem> items = makeItems(5);
        ImmutableList<MediaItem> result = BrowseTree.page(items, 0, 0);
        assertThat(result).hasSize(5);
    }

    @Test
    public void page_negativePage_treatedAsZero() {
        ImmutableList<MediaItem> items = makeItems(5);
        ImmutableList<MediaItem> result = BrowseTree.page(items, -1, 3);
        assertThat(result).hasSize(3);
        assertThat(result.get(0).mediaId).isEqualTo("item_0");
    }

    @Test
    public void page_emptyList_returnsEmpty() {
        ImmutableList<MediaItem> items = ImmutableList.of();
        assertThat(BrowseTree.page(items, 0, 3)).isEmpty();
    }

    @Test
    public void page_pageSizeLargerThanList_returnsAll() {
        ImmutableList<MediaItem> items = makeItems(3);
        ImmutableList<MediaItem> result = BrowseTree.page(items, 0, 100);
        assertThat(result).hasSize(3);
    }
}

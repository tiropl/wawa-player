package com.wawa_player.android.tv.playback.vod;

import static org.junit.Assert.*;

import com.wawa_player.android.tv.bean.Episode;
import com.wawa_player.android.tv.bean.Flag;
import com.wawa_player.android.tv.bean.Result;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class VodPlaybackStateTest {
    @Test public void requestAndPreloadLifecycle() {
        VodPlaybackState state = new VodPlaybackState(); Flag flag = Flag.create("f", "u"); Episode ep = flag.getEpisodes().get(0);
        VodPlayRequest request = VodPlayRequest.create("k", flag, ep);
        state.setPendingRequest(request); assertSame(request, state.getActiveRequest());
        state.beginPreload(request); assertSame(request, state.getPreloadRequest()); assertNull(state.getPreloadResult());
        Result result = Result.empty(); state.completePreload(result);
        assertSame(result, state.consumePreload("k", flag, ep)); assertNull(state.getPreloadRequest()); assertNull(state.getPreloadResult());
        state.setPlayingRequest(request); assertSame(request, state.getActiveRequest()); assertNull(state.getPendingRequest());
    }

    @Test public void resetRestoresDefaultsAndClearsCollections() {
        VodPlaybackState state = new VodPlaybackState(); state.setSources(Collections.singletonList(null)); state.setQuality(Result.error("x"));
        state.setSearchKeyword(null); state.setSelectFirstSource(true); state.setAutoFallback(true); state.setUseParse(true); state.reset();
        assertFalse(state.hasSources()); assertSame(Result.empty().getClass(), state.getQuality().getClass());
        assertEquals("", state.getSearchKeyword()); assertFalse(state.isSelectFirstSource()); assertFalse(state.isAutoFallback()); assertFalse(state.isUseParse());
    }

    @Test public void failedIdsSourcesAndFlagsRespectBoundaries() {
        VodPlaybackState state = new VodPlaybackState();
        state.addFailedId("one");
        state.addFailedId("two");
        state.addFailedId("");
        state.addFailedId(null);
        assertTrue(state.hasFailedId("one"));
        assertTrue(state.hasFailedId("two"));
        assertFalse(state.hasFailedId(""));
        assertFalse(state.hasFailedId(null));

        Flag first = Flag.create("first", "one");
        Flag second = Flag.create("second", "two");
        second.setSelected(second);
        state.setFlags(java.util.List.of(first, second));
        assertTrue(state.hasFlags());
        assertEquals(1, state.getFlagPosition());
        assertSame(second, state.getFlag());
        assertTrue(state.hasEpisode());
        assertSame(second.getEpisodes().get(0), state.getEpisode());

        state.setSources(java.util.List.of(new com.wawa_player.android.tv.bean.Vod(), new com.wawa_player.android.tv.bean.Vod()));
        assertTrue(state.hasSources());
        assertNotNull(state.removeFirstSource());
        assertTrue(state.hasSources());
        assertNotNull(state.removeFirstSource());
        assertFalse(state.hasSources());
    }

    @Test public void emptyFlagsAndInvalidEpisodePositionUseCurrentDefaults() {
        VodPlaybackState state = new VodPlaybackState();
        assertFalse(state.hasFlags());
        assertEquals(0, state.getFlagPosition());
        try {
            state.getFlag();
            fail("getFlag should reject an empty flag list");
        } catch (IndexOutOfBoundsException expected) {
        }

        Flag flag = Flag.create("flag", "episode");
        flag.setPosition(99);
        state.setFlags(java.util.List.of(flag));
        assertSame(flag.getEpisodes().get(0), state.getEpisode());
        assertTrue(state.hasEpisode());
    }

    @Test public void detailMetadataAndSearchStateUpdateAndReset() {
        VodPlaybackState state = new VodPlaybackState();
        state.setDetailRequest(null, null);
        assertTrue(state.isDetailRequested("", ""));
        assertFalse(state.isDetailRequested("key", "id"));
        state.setDetailRequest("key", "id");
        assertTrue(state.isDetailRequested("key", "id"));

        androidx.media3.common.MediaMetadata metadata = new androidx.media3.common.MediaMetadata.Builder().setTitle("title").build();
        state.setPlaybackMetadata(metadata);
        state.setSearchKeyword(null);
        assertSame(metadata, state.getPlaybackMetadata());
        assertEquals("", state.getSearchKeyword());
        state.setSearchKeyword("  query  ");
        assertEquals("  query  ", state.getSearchKeyword());
        state.reset();
        assertNull(state.getPlaybackMetadata());
        assertTrue(state.isDetailRequested("", ""));
    }

    @Test public void preloadOnlyMatchesAndIsConsumedOnce() {
        VodPlaybackState state = new VodPlaybackState();
        Flag flag = Flag.create("f", "url");
        Episode episode = flag.getEpisodes().get(0);
        VodPlayRequest request = VodPlayRequest.create("key", flag, episode);
        Result result = Result.empty();
        state.beginPreload(request);
        state.completePreload(result);
        assertNull(state.consumePreload("other", flag, episode));
        assertSame(result, state.getPreloadResult());
        assertNull(state.consumePreload("key", flag, Episode.create("other", "other-url")));
        assertSame(result, state.consumePreload("key", flag, episode));
        assertNull(state.consumePreload("key", flag, episode));
    }
}

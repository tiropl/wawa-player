package com.wawa_player.android.tv.playback.vod;

import static org.junit.Assert.*;

import com.wawa_player.android.tv.bean.Episode;
import com.wawa_player.android.tv.bean.Flag;
import com.wawa_player.android.tv.bean.Result;
import java.util.Collections;
import org.junit.Test;

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
}

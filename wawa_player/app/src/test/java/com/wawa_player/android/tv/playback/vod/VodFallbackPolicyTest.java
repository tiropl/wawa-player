package com.wawa_player.android.tv.playback.vod;

import static com.google.common.truth.Truth.assertThat;

import androidx.media3.common.MediaMetadata;

import com.wawa_player.android.tv.bean.Episode;
import com.wawa_player.android.tv.bean.Flag;
import com.wawa_player.android.tv.bean.Result;
import com.wawa_player.android.tv.bean.Site;
import com.wawa_player.android.tv.bean.Vod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class VodFallbackPolicyTest {

    private static class FakeHost implements VodPlaybackHost {
        String vodKey = "key";
        String vodId = "id123";
        String vodName = "Movie";
        boolean siteChangeable = true;
        List<String> searchKeywords = new ArrayList<>();
        List<Vod> renderedSources = new ArrayList<>();
        Vod switchedSource = null;

        @Override public String getVodKey() { return vodKey; }
        @Override public String getVodId() { return vodId; }
        @Override public void setVodId(String id) { this.vodId = id; }
        @Override public String getVodName() { return vodName; }
        @Override public String getVodPic() { return ""; }
        @Override public String getVodMark() { return ""; }
        @Override public String getHistoryKey() { return ""; }
        @Override public boolean isSiteChangeable() { return siteChangeable; }
        @Override public boolean isFromCollect() { return false; }
        @Override public boolean isHostFinishing() { return false; }
        @Override public boolean isPlayerEmpty() { return true; }
        @Override public boolean hasPlaybackSession() { return false; }
        @Override public boolean isFullscreenForPlayback() { return false; }
        @Override public boolean isLivePlayback() { return false; }
        @Override public boolean canTrackPlaybackProgress() { return false; }
        @Override public boolean canPreloadNext() { return false; }
        @Override public long getPlayerPosition() { return 0; }
        @Override public long getPlayerDuration() { return 0; }
        @Override public void usePushId(String id) {}
        @Override public void requestDetail(String key, String id) {}
        @Override public void requestPlayer(VodPlayRequest request) {}
        @Override public void requestPreload(VodPlayRequest request) {}
        @Override public void requestSearch(List<Site> sites, String keyword) { searchKeywords.add(keyword); }
        @Override public void prepareSource(Vod item) {}
        @Override public void stopPlaybackForRefresh() {}
        @Override public void resetPlaybackForError(String msg) {}
        @Override public void replay(long position) {}
        @Override public void startPlayback(Result result, boolean useParse, long startPositionMs, MediaMetadata metadata) {}
        @Override public boolean preloadPlayback(Result result, long startPositionMs, MediaMetadata metadata) { return false; }
        @Override public void clearPreload() {}
        @Override public void loadDanmaku(Result result, com.wawa_player.android.tv.bean.History history, Episode episode) {}
        @Override public void renderDetail(com.wawa_player.android.tv.bean.Vod item, com.wawa_player.android.tv.bean.History history) {}
        @Override public void renderVodUpdate(com.wawa_player.android.tv.bean.Vod item) {}
        @Override public void renderEmptyDetail() {}
        @Override public void renderFallbackName(String name) {}
        @Override public void renderFlags(List<Flag> items) {}
        @Override public void renderEpisodes(List<Episode> items) {}
        @Override public void renderFlagSelection(Flag item) {}
        @Override public void renderEpisodeSelection(Episode item) {}
        @Override public void renderReverseEpisodes(List<Episode> items, boolean scroll) {}
        @Override public void renderQuality(Result result, boolean visible) {}
        @Override public void renderQualityVisible(boolean visible) {}
        @Override public void renderSources(List<Vod> items) { renderedSources = new ArrayList<>(items); }
        @Override public void renderHistory(com.wawa_player.android.tv.bean.History history) {}
        @Override public void renderUseParse(boolean useParse) {}
        @Override public void renderArtwork(String url) {}
        @Override public void renderDescription(String desc) {}
        @Override public void renderPlaybackMetadata(MediaMetadata metadata) {}
        @Override public void onDetailFallbackScheduled() {}
        @Override public void onDetailFallbackCancelled() {}
        @Override public void onSearchStarted(String keyword) {}
        @Override public void onSearchResult() {}
        @Override public void showDetailMessage(String msg) {}
        @Override public void showSwitchLine(Flag flag) {}
        @Override public void showSwitchSource(Vod item) { switchedSource = item; }
        @Override public void showEpisodeReady(Episode item) {}
        @Override public void showNoNext(boolean reversed) {}
        @Override public void showNoPrev(boolean reversed) {}
        @Override public void finishVod() {}
    }

    @Test
    public void search_setsSearchKeywordAndCallsHost() {
        FakeHost host = new FakeHost();
        VodPlaybackState state = new VodPlaybackState();
        VodPlaybackController controller = new VodPlaybackController(host, state);
        VodFallbackPolicy policy = new VodFallbackPolicy(controller, state, host);

        policy.search("test query", true);

        assertThat(state.getSearchKeyword()).isEqualTo("test query");
        assertThat(state.isAutoFallback()).isTrue();
        assertThat(state.isSelectFirstSource()).isTrue();
        assertThat(host.searchKeywords).contains("test query");
    }

    @Test
    public void search_autoFallbackFalse_doesNotSelectFirstSource() {
        FakeHost host = new FakeHost();
        VodPlaybackState state = new VodPlaybackState();
        VodPlaybackController controller = new VodPlaybackController(host, state);
        VodFallbackPolicy policy = new VodFallbackPolicy(controller, state, host);

        policy.search("query", false);

        assertThat(state.isAutoFallback()).isFalse();
        assertThat(state.isSelectFirstSource()).isFalse();
    }

    @Test
    public void onSearchResult_filtersOutCurrentVod() {
        FakeHost host = new FakeHost();
        host.vodId = "current-id";
        host.vodName = "Movie";
        VodPlaybackState state = new VodPlaybackState();
        state.setSearchKeyword("Movie");
        state.setAutoFallback(false);
        VodPlaybackController controller = new VodPlaybackController(host, state);
        VodFallbackPolicy policy = new VodFallbackPolicy(controller, state, host);

        Vod current = new Vod();
        current.setId("current-id");
        current.setName("Movie");

        Vod match = new Vod();
        match.setId("other-id");
        match.setName("Movie");

        Vod mismatch = new Vod();
        mismatch.setId("another-id");
        mismatch.setName("Different");

        Result result = new Result();
        result.setList(List.of(current, match, mismatch));

        policy.onSearchResult(result);

        // current is filtered out (same id), mismatch is filtered out (name doesn't contain keyword in non-autoFallback)
        assertThat(host.renderedSources).hasSize(1);
        assertThat(host.renderedSources.get(0).getId()).isEqualTo("other-id");
    }

    @Test
    public void onSearchResult_autoFallback_requiresExactNameMatch() {
        FakeHost host = new FakeHost();
        host.vodId = "current-id";
        host.vodName = "Movie";
        VodPlaybackState state = new VodPlaybackState();
        state.setSearchKeyword("Movie");
        state.setAutoFallback(true);
        VodPlaybackController controller = new VodPlaybackController(host, state);
        VodFallbackPolicy policy = new VodFallbackPolicy(controller, state, host);

        Vod exactMatch = new Vod();
        exactMatch.setId("id1");
        exactMatch.setName("Movie");

        Vod containsMatch = new Vod();
        containsMatch.setId("id2");
        containsMatch.setName("My Movie Extra");

        Result result = new Result();
        result.setList(List.of(exactMatch, containsMatch));

        policy.onSearchResult(result);

        // In autoFallback mode, name must equal keyword exactly
        assertThat(host.renderedSources).hasSize(1);
        assertThat(host.renderedSources.get(0).getId()).isEqualTo("id1");
    }

    @Test
    public void onSearchResult_filtersOutFailedIds() {
        FakeHost host = new FakeHost();
        host.vodId = "current-id";
        host.vodName = "Movie";
        VodPlaybackState state = new VodPlaybackState();
        state.setSearchKeyword("Movie");
        state.setAutoFallback(false);
        state.addFailedId("failed-id");
        VodPlaybackController controller = new VodPlaybackController(host, state);
        VodFallbackPolicy policy = new VodFallbackPolicy(controller, state, host);

        Vod failed = new Vod();
        failed.setId("failed-id");
        failed.setName("Movie");

        Vod good = new Vod();
        good.setId("good-id");
        good.setName("Movie");

        Result result = new Result();
        result.setList(List.of(failed, good));

        policy.onSearchResult(result);

        assertThat(host.renderedSources).hasSize(1);
        assertThat(host.renderedSources.get(0).getId()).isEqualTo("good-id");
    }

    @Test
    public void emptyDetail_searchesForVodNameEvenWhenNotChangeable() {
        // emptyDetail() calls fallbackToNextSource(false) which always searches
        // when there are no sources, regardless of siteChangeable
        FakeHost host = new FakeHost();
        host.siteChangeable = false;
        host.vodName = "Test Movie";
        VodPlaybackState state = new VodPlaybackState();
        VodPlaybackController controller = new VodPlaybackController(host, state);
        VodFallbackPolicy policy = new VodFallbackPolicy(controller, state, host);

        policy.emptyDetail();

        assertThat(host.searchKeywords).contains("Test Movie");
    }

    @Test
    public void playbackError_whenNotChangeable_doesNothing() {
        FakeHost host = new FakeHost();
        host.siteChangeable = false;
        VodPlaybackState state = new VodPlaybackState();
        VodPlaybackController controller = new VodPlaybackController(host, state);
        VodFallbackPolicy policy = new VodFallbackPolicy(controller, state, host);

        policy.playbackError();

        assertThat(host.searchKeywords).isEmpty();
        assertThat(host.switchedSource).isNull();
    }

    @Test
    public void emptyFlag_whenNotChangeable_doesNothing() {
        FakeHost host = new FakeHost();
        host.siteChangeable = false;
        VodPlaybackState state = new VodPlaybackState();
        VodPlaybackController controller = new VodPlaybackController(host, state);
        VodFallbackPolicy policy = new VodFallbackPolicy(controller, state, host);

        policy.emptyFlag();

        assertThat(host.searchKeywords).isEmpty();
    }

    @Test
    public void emptyDetail_whenChangeable_searchesForVodName() {
        FakeHost host = new FakeHost();
        host.siteChangeable = true;
        host.vodName = "Test Movie";
        VodPlaybackState state = new VodPlaybackState();
        VodPlaybackController controller = new VodPlaybackController(host, state);
        VodFallbackPolicy policy = new VodFallbackPolicy(controller, state, host);

        policy.emptyDetail();

        assertThat(host.searchKeywords).contains("Test Movie");
    }
}

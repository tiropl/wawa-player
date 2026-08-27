package com.wawa_player.android.tv.playback.vod;

import static com.google.common.truth.Truth.assertThat;

import com.wawa_player.android.tv.bean.Episode;
import com.wawa_player.android.tv.bean.Flag;
import com.wawa_player.android.tv.bean.Result;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class VodPlayRequestTest {

    @Test
    public void factoryCapturesKeyFlagAndEpisodeUrl() {
        Flag flag = Flag.create("line");
        Episode episode = Episode.create("01", "https://example/episode");
        VodPlayRequest request = VodPlayRequest.create("vod-key", flag, episode);

        assertThat(request.getKey()).isEqualTo("vod-key");
        assertThat(request.getFlag()).isEqualTo("line");
        assertThat(request.getId()).isEqualTo("https://example/episode");
        assertThat(request.matches("vod-key", flag, episode)).isTrue();
        assertThat(request.matches("other", flag, episode)).isFalse();
    }

    @Test
    public void matchesRequestAndAcceptsUnscopedOrMatchingResult() {
        Flag flag = Flag.create("line");
        Episode episode = Episode.create("01", "episode");
        VodPlayRequest request = VodPlayRequest.create("key", flag, episode);

        assertThat(request.matches(VodPlayRequest.create("key", Flag.create("line"), Episode.create("01", "episode")))).isTrue();
        assertThat(request.matches((VodPlayRequest) null)).isFalse();
        assertThat(request.accepts(Result.empty())).isTrue();
        Result scoped = Result.empty();
        scoped.setFlag("line");
        assertThat(request.accepts(scoped)).isTrue();
        scoped.setFlag("other");
        assertThat(request.accepts(scoped)).isFalse();
        assertThat(request.accepts(null)).isFalse();
    }
}

package com.wawa_player.android.tv.playback;

import static com.google.common.truth.Truth.assertThat;

import com.wawa_player.android.tv.bean.Result;
import com.wawa_player.android.tv.playback.vod.VodDetailResult;

import org.junit.Test;

public class PlaybackValueTest {

    @Test
    public void playbackResultRecordPreservesRequestAndResult() {
        Object request = new Object();
        Result result = Result.empty();
        PlaybackResult<Object> playback = new PlaybackResult<>(request, result);

        assertThat(playback.request()).isSameInstanceAs(request);
        assertThat(playback.result()).isSameInstanceAs(result);
    }

    @Test
    public void detailResultRecordUsesValueEquality() {
        Result result = Result.empty();
        VodDetailResult first = new VodDetailResult("key", "id", result);
        VodDetailResult same = new VodDetailResult("key", "id", result);

        assertThat(first).isEqualTo(same);
        assertThat(first.hashCode()).isEqualTo(same.hashCode());
    }
}

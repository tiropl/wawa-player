package com.wawa_player.android.tv.playback;

import static com.google.common.truth.Truth.assertThat;

import com.wawa_player.android.tv.bean.Result;

import org.junit.Test;

public class PlaybackResultTest {

    @Test
    public void record_accessors() {
        Result result = new Result();
        PlaybackResult<String> pr = new PlaybackResult<>("req1", result);
        assertThat(pr.request()).isEqualTo("req1");
        assertThat(pr.result()).isSameInstanceAs(result);
    }

    @Test
    public void record_with_nulls() {
        PlaybackResult<Object> pr = new PlaybackResult<>(null, null);
        assertThat(pr.request()).isNull();
        assertThat(pr.result()).isNull();
    }

    @Test
    public void record_equality() {
        Result r = new Result();
        PlaybackResult<String> a = new PlaybackResult<>("x", r);
        PlaybackResult<String> b = new PlaybackResult<>("x", r);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void record_inequality() {
        PlaybackResult<String> a = new PlaybackResult<>("x", new Result());
        PlaybackResult<String> b = new PlaybackResult<>("y", new Result());
        assertThat(a).isNotEqualTo(b);
    }
}

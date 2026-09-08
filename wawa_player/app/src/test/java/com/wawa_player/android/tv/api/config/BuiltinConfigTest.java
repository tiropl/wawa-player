package com.wawa_player.android.tv.api.config;

import com.google.common.truth.Truth;

import org.junit.Test;

public class BuiltinConfigTest {

    @Test
    public void vod_url_default_empty() {
        Truth.assertThat(BuiltinConfig.VOD_URL).isEmpty();
    }

    @Test
    public void live_url_default_empty() {
        Truth.assertThat(BuiltinConfig.LIVE_URL).isEmpty();
    }

    @Test
    public void wall_url_default_empty() {
        Truth.assertThat(BuiltinConfig.WALL_URL).isEmpty();
    }
}

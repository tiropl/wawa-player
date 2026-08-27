package com.wawa_player.android.tv.player.effect.video;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class VideoEffectPresetTest {

    @Test
    public void clampsVideoPresetRange() {
        assertThat(VideoEffectPreset.clamp(-1)).isEqualTo(VideoEffectPreset.OFF);
        assertThat(VideoEffectPreset.clamp(VideoEffectPreset.NATURAL)).isEqualTo(VideoEffectPreset.NATURAL);
        assertThat(VideoEffectPreset.clamp(VideoEffectPreset.CUSTOM)).isEqualTo(VideoEffectPreset.CUSTOM);
        assertThat(VideoEffectPreset.clamp(99)).isEqualTo(VideoEffectPreset.CUSTOM);
    }
}

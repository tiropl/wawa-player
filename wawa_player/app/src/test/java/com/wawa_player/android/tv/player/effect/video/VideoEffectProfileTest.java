package com.wawa_player.android.tv.player.effect.video;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class VideoEffectProfileTest {

    @Test
    public void offProfileIsNoOp() {
        VideoEffectProfile profile = VideoEffectProfile.off();

        assertThat(profile.isNoOp()).isTrue();
        assertThat(profile.getSaturation()).isEqualTo(1.0f);
        assertThat(profile.getContrast()).isEqualTo(1.0f);
        assertThat(profile.getGamma()).isEqualTo(1.0f);
        assertThat(profile.getTemperature()).isEqualTo(0.0f);
    }

    @Test
    public void customMapsAllParametersAndTemperatureGains() {
        VideoEffectProfile profile = VideoEffectProfile.custom(1.2f, 1.1f, 0.1f, 0.2f, 0.3f, 0.9f, 5.0f, 20.0f);

        assertThat(profile.getSaturation()).isEqualTo(1.2f);
        assertThat(profile.getContrast()).isEqualTo(1.1f);
        assertThat(profile.getBrightness()).isEqualTo(0.1f);
        assertThat(profile.getSharpness()).isEqualTo(0.2f);
        assertThat(profile.getShadowLift()).isEqualTo(0.3f);
        assertThat(profile.getGamma()).isEqualTo(0.9f);
        assertThat(profile.getHue()).isEqualTo(5.0f);
        assertThat(profile.getTemperature()).isEqualTo(20.0f);
        assertThat(profile.redGain()).isEqualTo(1.03f);
        assertThat(profile.blueGain()).isEqualTo(0.976f);
        assertThat(profile.isNoOp()).isFalse();
    }

    @Test
    public void presetClampsAndCustomPresetMapsToOff() {
        assertThat(VideoEffectProfile.of(-1)).isSameInstanceAs(VideoEffectProfile.off());
        assertThat(VideoEffectProfile.of(VideoEffectPreset.CUSTOM)).isSameInstanceAs(VideoEffectProfile.off());
        assertThat(VideoEffectProfile.of(VideoEffectPreset.VIVID).isNoOp()).isFalse();
    }
}

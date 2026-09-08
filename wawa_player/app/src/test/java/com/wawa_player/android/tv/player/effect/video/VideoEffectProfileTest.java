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

    @Test
    public void negativeTemperatureGainsAreSymmetric() {
        VideoEffectProfile profile = VideoEffectProfile.custom(1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -20.0f);
        // negative temperature: redGain = 1 + (-20)*0.0012 = 0.976, blueGain = 1 - (-20)*0.0015 = 1.03
        assertThat(profile.redGain()).isWithin(0.001f).of(0.976f);
        assertThat(profile.blueGain()).isWithin(0.001f).of(1.03f);
    }

    @Test
    public void zeroTemperatureGainsAreOne() {
        VideoEffectProfile profile = VideoEffectProfile.off();
        assertThat(profile.redGain()).isEqualTo(1.0f);
        assertThat(profile.blueGain()).isEqualTo(1.0f);
    }

    @Test
    public void colorNoOpChecksAllColorParams() {
        VideoEffectProfile off = VideoEffectProfile.off();
        assertThat(off.isColorNoOp()).isTrue();

        // Changed saturation alone breaks color no-op
        VideoEffectProfile sat = VideoEffectProfile.custom(1.1f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
        assertThat(sat.isColorNoOp()).isFalse();
    }

    @Test
    public void toneNoOpChecksGammaAndHue() {
        VideoEffectProfile off = VideoEffectProfile.off();
        assertThat(off.isToneNoOp()).isTrue();

        VideoEffectProfile gamma = VideoEffectProfile.custom(1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.9f, 0.0f, 0.0f);
        assertThat(gamma.isToneNoOp()).isFalse();

        VideoEffectProfile hue = VideoEffectProfile.custom(1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 5.0f, 0.0f);
        assertThat(hue.isToneNoOp()).isFalse();
    }

    @Test
    public void detailNoOpChecksSharpnessAndShadow() {
        VideoEffectProfile off = VideoEffectProfile.off();
        assertThat(off.isDetailNoOp()).isTrue();

        VideoEffectProfile sharp = VideoEffectProfile.custom(1.0f, 1.0f, 0.0f, 0.1f, 0.0f, 1.0f, 0.0f, 0.0f);
        assertThat(sharp.isDetailNoOp()).isFalse();

        VideoEffectProfile shadow = VideoEffectProfile.custom(1.0f, 1.0f, 0.0f, 0.0f, 0.2f, 1.0f, 0.0f, 0.0f);
        assertThat(shadow.isDetailNoOp()).isFalse();
    }

    @Test
    public void allPresetsHaveExpectedCharacteristics() {
        // NATURAL is a basic preset (gamma=1, hue=0)
        VideoEffectProfile natural = VideoEffectProfile.of(VideoEffectPreset.NATURAL);
        assertThat(natural.isToneNoOp()).isTrue();
        assertThat(natural.isNoOp()).isFalse();

        // CINEMA has gamma != 1
        VideoEffectProfile cinema = VideoEffectProfile.of(VideoEffectPreset.CINEMA);
        assertThat(cinema.getGamma()).isNotEqualTo(1.0f);

        // WARM has positive temperature
        VideoEffectProfile warm = VideoEffectProfile.of(VideoEffectPreset.WARM);
        assertThat(warm.getTemperature()).isGreaterThan(0.0f);

        // COOL has negative temperature
        VideoEffectProfile cool = VideoEffectProfile.of(VideoEffectPreset.COOL);
        assertThat(cool.getTemperature()).isLessThan(0.0f);
    }
}

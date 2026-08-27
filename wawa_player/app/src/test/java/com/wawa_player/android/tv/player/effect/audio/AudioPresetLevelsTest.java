package com.wawa_player.android.tv.player.effect.audio;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class AudioPresetLevelsTest {

    @Test
    public void offAndCustomPresetsProduceZeroLevels() {
        AudioEffectBands bands = new AudioEffectBands((short) -1200, (short) 1200, new int[]{100_000, 1_000_000});

        assertThat(AudioPresetLevels.of(AudioEffectPreset.OFF, bands)).asList().containsExactly((short) 0, (short) 0).inOrder();
        assertThat(AudioPresetLevels.of(AudioEffectPreset.CUSTOM, bands)).asList().containsExactly((short) 0, (short) 0).inOrder();
        assertThat(AudioPresetLevels.of(-1, bands)).asList().containsExactly((short) 0, (short) 0).inOrder();
    }

    @Test
    public void presetLevelsAreClampedToBandBounds() {
        AudioEffectBands bands = new AudioEffectBands((short) -100, (short) 100, new int[]{32_000, 8_000_000});
        short[] levels = AudioPresetLevels.of(AudioEffectPreset.BASS, bands);

        assertThat(levels).asList().containsExactly((short) 100, (short) -40).inOrder();
    }

    @Test
    public void emptyBandsProduceEmptyLevels() {
        assertThat(AudioPresetLevels.of(AudioEffectPreset.ROCK, AudioEffectBands.EMPTY)).isEmpty();
    }
}

package com.wawa_player.android.tv.player.effect.audio;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class AudioEffectBandsTest {

    @Test
    public void copiesFrequenciesAndReportsBounds() {
        int[] frequencies = {100, 1000, 10000};
        AudioEffectBands bands = new AudioEffectBands((short) -10, (short) 10, frequencies);
        frequencies[0] = 999;

        assertThat(bands.getCount()).isEqualTo(3);
        assertThat(bands.getCenterFrequency(0)).isEqualTo(100);
        assertThat(bands.getMinLevel()).isEqualTo((short) -10);
        assertThat(bands.getMaxLevel()).isEqualTo((short) 10);
        assertThat(bands.isEmpty()).isFalse();
    }

    @Test
    public void clampsAndSnapsLevels() {
        AudioEffectBands bands = new AudioEffectBands((short) -10, (short) 10, new int[]{100});

        assertThat(bands.clamp(-20)).isEqualTo((short) -10);
        assertThat(bands.clamp(20)).isEqualTo((short) 10);
        assertThat(bands.snapToStep(7, 5)).isEqualTo((short) 5);
        assertThat(bands.snapToStep(7, 0)).isEqualTo((short) 7);
    }

    @Test
    public void nullOrInvalidBoundsAreEmpty() {
        assertThat(new AudioEffectBands((short) 0, (short) 0, null).isEmpty()).isTrue();
        assertThat(AudioEffectBands.EMPTY.isEmpty()).isTrue();
    }
}

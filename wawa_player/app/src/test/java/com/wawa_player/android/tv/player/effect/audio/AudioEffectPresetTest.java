package com.wawa_player.android.tv.player.effect.audio;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class AudioEffectPresetTest {

    @Test
    public void clampsAudioPresetRange() {
        assertThat(AudioEffectPreset.clamp(-1)).isEqualTo(AudioEffectPreset.OFF);
        assertThat(AudioEffectPreset.clamp(AudioEffectPreset.NATURAL)).isEqualTo(AudioEffectPreset.NATURAL);
        assertThat(AudioEffectPreset.clamp(AudioEffectPreset.CUSTOM)).isEqualTo(AudioEffectPreset.CUSTOM);
        assertThat(AudioEffectPreset.clamp(99)).isEqualTo(AudioEffectPreset.CUSTOM);
    }
}

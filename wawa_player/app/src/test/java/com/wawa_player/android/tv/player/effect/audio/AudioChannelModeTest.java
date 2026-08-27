package com.wawa_player.android.tv.player.effect.audio;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class AudioChannelModeTest {

    @Test
    public void clampsModesAndReportsBalanceAvailability() {
        assertThat(AudioChannelMode.clamp(-1)).isEqualTo(AudioChannelMode.AUTO);
        assertThat(AudioChannelMode.clamp(99)).isEqualTo(AudioChannelMode.REVERSE);
        assertThat(AudioChannelMode.isAuto(AudioChannelMode.AUTO)).isTrue();
        assertThat(AudioChannelMode.isAuto(AudioChannelMode.MONO)).isFalse();
        assertThat(AudioChannelMode.canBalance(AudioChannelMode.MONO)).isFalse();
        assertThat(AudioChannelMode.canBalance(AudioChannelMode.STEREO)).isTrue();
    }

    @Test
    public void validatesChannelCountsAndModeAvailability() {
        assertThat(AudioChannelMode.isAvailable(1)).isFalse();
        assertThat(AudioChannelMode.isAvailable(2)).isTrue();
        assertThat(AudioChannelMode.isAvailable(8)).isTrue();
        assertThat(AudioChannelMode.isAvailable(9)).isFalse();
        assertThat(AudioChannelMode.isAvailable(AudioChannelMode.STEREO, 2)).isFalse();
        assertThat(AudioChannelMode.isAvailable(AudioChannelMode.STEREO, 6)).isTrue();
        assertThat(AudioChannelMode.resolve(AudioChannelMode.STEREO, 2)).isEqualTo(AudioChannelMode.AUTO);
        assertThat(AudioChannelMode.resolve(AudioChannelMode.MONO, 2)).isEqualTo(AudioChannelMode.MONO);
    }

    @Test
    public void reportsClipCapabilityByModeAndChannels() {
        assertThat(AudioChannelMode.canClip(AudioChannelMode.MONO, 2)).isTrue();
        assertThat(AudioChannelMode.canClip(AudioChannelMode.STEREO, 2)).isFalse();
        assertThat(AudioChannelMode.canClip(AudioChannelMode.STEREO, 6)).isTrue();
        assertThat(AudioChannelMode.canClip(AudioChannelMode.AUTO, 6)).isFalse();
        assertThat(AudioChannelMode.canClip(AudioChannelMode.MONO, 1)).isFalse();
    }
}

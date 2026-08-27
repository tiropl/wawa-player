package com.wawa_player.android.tv.player.effect.audio;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class AudioLimiterTest {

    @Test
    public void limitClampsSamplesToPointNineEight() {
        AudioLimiter limiter = new AudioLimiter();

        assertThat(limiter.limit(2.0f)).isEqualTo(0.98f);
        assertThat(limiter.limit(-2.0f)).isEqualTo(-0.98f);
        assertThat(limiter.limit(0.5f)).isEqualTo(0.5f);
    }

    @Test
    public void gainReducesAmplifiedPeakAndResetClearsEnvelope() {
        AudioLimiter limiter = new AudioLimiter();
        limiter.configure(1000);

        assertThat(limiter.getGain(1.0f, 2.0f)).isWithin(0.0001f).of(0.98f);
        assertThat(limiter.getGain(0.0f, 1.0f)).isLessThan(1.0f);
        limiter.reset();
        assertThat(limiter.getGain(0.0f, 1.0f)).isEqualTo(1.0f);
    }
}

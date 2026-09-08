package com.wawa_player.android.tv.player.effect.audio;

import static com.google.common.truth.Truth.assertThat;

import com.wawa_player.android.tv.setting.AudioSetting;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class AudioEffectConfigTest {

    @Test
    public void disabledConfigHasNoEffect() {
        AudioEffectConfig disabled = AudioEffectConfig.disabled();
        assertThat(disabled.hasEffect()).isFalse();
        assertThat(disabled.hasBands()).isFalse();
        assertThat(disabled.hasProcessorEffect()).isFalse();
        assertThat(disabled.getLevels()).isEmpty();
        assertThat(disabled.getStability()).isEqualTo(0);
        assertThat(disabled.getBoost()).isEqualTo(0);
        assertThat(disabled.getPreamp()).isEqualTo(0);
        assertThat(disabled.isLoudnessEnabled()).isFalse();
        assertThat(disabled.getCenterGain()).isEqualTo(0);
        assertThat(disabled.getBalance()).isEqualTo(0);
    }

    @Test
    public void constructorClampsAndCopiesLevels() {
        short[] levels = {100, -200, 300};
        AudioEffectConfig config = new AudioEffectConfig(
                levels, 150, 1500, -1500, true, 1500, 150, 99);
        // levels are copied defensively
        levels[0] = 9999;
        assertThat(config.getLevels()[0]).isEqualTo(100);
        // stability clamped to MAX
        assertThat(config.getStability()).isEqualTo(AudioSetting.MAX_STABILITY);
        // boost clamped to MAX
        assertThat(config.getBoost()).isEqualTo(AudioSetting.MAX_BOOST);
        // centerGain clamped to MAX
        assertThat(config.getCenterGain()).isEqualTo(AudioSetting.MAX_CENTER_GAIN);
        // channelMode clamped
        assertThat(config.getChannelMode()).isEqualTo(AudioChannelMode.REVERSE);
        // hasBands because levels.length > 0
        assertThat(config.hasBands()).isTrue();
        assertThat(config.hasEffect()).isTrue();
    }

    @Test
    public void effectivePreampSubtractsMaxBandLevel() {
        AudioEffectConfig config = new AudioEffectConfig(
                new short[]{0, 500, -100}, 0, 0, -500, false, 0, 0, AudioChannelMode.AUTO);
        // preamp = clamp(-500, MIN, MAX) - maxLevel(500) = -500 - 500 = -1000
        assertThat(config.getPreamp()).isEqualTo(-1000);
    }

    @Test
    public void gainCalculationsForZeroAndNonZeroLevels() {
        AudioEffectConfig config = new AudioEffectConfig(
                new short[0], 0, 0, 0, false, 0, 0, AudioChannelMode.AUTO);
        assertThat(config.getBoostGain()).isEqualTo(1.0f);
        assertThat(config.getPreampGain()).isEqualTo(1.0f);
        assertThat(config.getCenterGainFactor()).isEqualTo(1.0f);

        AudioEffectConfig amplified = new AudioEffectConfig(
                new short[0], 0, 2000, 0, false, 0, 0, AudioChannelMode.AUTO);
        assertThat(amplified.getBoostGain()).isGreaterThan(1.0f);
    }

    @Test
    public void stabilityAmountReflectsRatio() {
        AudioEffectConfig config = new AudioEffectConfig(
                new short[0], 50, 0, 0, false, 0, 0, AudioChannelMode.AUTO);
        assertThat(config.getStabilityAmount()).isEqualTo(50f / AudioSetting.MAX_STABILITY);
    }

    @Test
    public void centerGainAvailabilityDependsOnChannelCount() {
        assertThat(AudioEffectConfig.isCenterGainAvailable(2)).isFalse();
        assertThat(AudioEffectConfig.isCenterGainAvailable(6)).isTrue();
        assertThat(AudioEffectConfig.isCenterGainAvailable(8)).isTrue();
        assertThat(AudioEffectConfig.isCenterGainAvailable(10)).isFalse();
    }

    @Test
    public void shouldLimitProcessorAndOutputDependOnEffects() {
        AudioEffectConfig withBoost = new AudioEffectConfig(
                new short[0], 0, 100, 0, false, 0, 0, AudioChannelMode.AUTO);
        assertThat(withBoost.shouldLimitProcessor(2)).isTrue();
        assertThat(withBoost.shouldLimitOutput(2)).isTrue();

        AudioEffectConfig withCenterGain = new AudioEffectConfig(
                new short[0], 0, 0, 0, false, 500, 0, AudioChannelMode.AUTO);
        assertThat(withCenterGain.shouldLimitProcessor(6)).isTrue();
        assertThat(withCenterGain.shouldLimitOutput(6)).isTrue();
        assertThat(withCenterGain.shouldLimitProcessor(2)).isFalse();

        AudioEffectConfig withPositiveBand = new AudioEffectConfig(
                new short[]{100}, 0, 0, 0, false, 0, 0, AudioChannelMode.AUTO);
        assertThat(withPositiveBand.shouldLimitOutput(2)).isTrue();
    }

    @Test
    public void balanceZeroedForNonBalanceableChannelMode() {
        AudioEffectConfig config = new AudioEffectConfig(
                new short[0], 0, 0, 0, false, 0, 50, AudioChannelMode.MONO);
        assertThat(config.getBalance()).isEqualTo(0);
    }
}

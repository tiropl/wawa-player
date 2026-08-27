package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AudioSettingTest {
    @Test public void advancedValuesAreClamped() {
        AudioSetting.putStability(999); assertEquals(AudioSetting.MAX_STABILITY, AudioSetting.getStability());
        AudioSetting.putDialogue(-1); assertEquals(AudioSetting.MIN_DIALOGUE, AudioSetting.getDialogue());
        AudioSetting.putBoost(9999); assertEquals(AudioSetting.MAX_BOOST, AudioSetting.getBoost());
        AudioSetting.putPreamp(-9999); assertEquals(AudioSetting.MIN_PREAMP, AudioSetting.getPreamp());
        AudioSetting.putBalance(999); assertEquals(AudioSetting.MAX_BALANCE, AudioSetting.getBalance());
    }

    @Test public void enabledAndLoudnessStateContributeEffect() {
        AudioSetting.reset();
        assertFalse(AudioSetting.isEnabled());
        assertFalse(AudioSetting.isLoudnessEnabled());
        assertFalse(AudioSetting.hasEffect(2));
        AudioSetting.putLoudness(true);
        assertTrue(AudioSetting.hasEffect(2));
        AudioSetting.putLoudness(false);
        AudioSetting.putPreset(1);
        assertTrue(AudioSetting.isEnabled());
    }
}

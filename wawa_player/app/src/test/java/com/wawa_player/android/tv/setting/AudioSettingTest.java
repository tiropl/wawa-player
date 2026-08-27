package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
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

    @Test public void resetClearsAdvancedStateAndPresetClamps() {
        AudioSetting.putStability(50);
        AudioSetting.putDialogue(50);
        AudioSetting.putBoost(50);
        AudioSetting.putPreamp(-50);
        AudioSetting.putCenterGain(50);
        AudioSetting.putBalance(50);
        AudioSetting.putChannelMode(99);
        AudioSetting.putPreset(-1);
        assertEquals(1, AudioSetting.getPreset());
        AudioSetting.resetAdvanced();
        assertEquals(0, AudioSetting.getStability());
        assertEquals(0, AudioSetting.getDialogue());
        assertEquals(0, AudioSetting.getBoost());
        assertEquals(0, AudioSetting.getCenterGain());
        assertEquals(0, AudioSetting.getBalance());
        AudioSetting.reset();
        assertFalse(AudioSetting.isEnabled());
    }
}

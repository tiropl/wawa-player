package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PlayerSettingTest {
    @Test public void defaultAndClampedRangesAreStable() {
        assertEquals(PlayerSetting.ENGINE_EXO, PlayerSetting.getEngine());
        assertEquals(2, PlayerSetting.getSize());
        assertEquals(0, PlayerSetting.getBackground());
        PlayerSetting.putSize(99);
        assertEquals(3, PlayerSetting.getSize());
        PlayerSetting.putBuffer(-1);
        assertEquals(1, PlayerSetting.getBuffer());
    }

    @Test public void backgroundModesHaveCorrectPredicates() {
        PlayerSetting.putBackground(0); assertTrue(PlayerSetting.isBackgroundOff()); assertFalse(PlayerSetting.isBackgroundOn());
        PlayerSetting.putBackground(2); assertFalse(PlayerSetting.isBackgroundOff()); assertTrue(PlayerSetting.isBackgroundOn()); assertTrue(PlayerSetting.isBackgroundPiP());
    }
}

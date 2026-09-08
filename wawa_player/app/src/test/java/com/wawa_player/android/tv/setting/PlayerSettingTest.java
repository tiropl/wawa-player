package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
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
        PlayerSetting.putBackground(1); assertFalse(PlayerSetting.isBackgroundOff()); assertTrue(PlayerSetting.isBackgroundOn()); assertFalse(PlayerSetting.isBackgroundPiP());
        PlayerSetting.putBackground(2); assertFalse(PlayerSetting.isBackgroundOff()); assertTrue(PlayerSetting.isBackgroundOn()); assertTrue(PlayerSetting.isBackgroundPiP());
    }

    @Test public void engineDebugGpuAndVulkanStatesAreStored() {
        PlayerSetting.putEngine(PlayerSetting.ENGINE_MPV);
        assertTrue(PlayerSetting.isMpv());
        assertFalse(PlayerSetting.isExo());
        PlayerSetting.putDebug(true);
        PlayerSetting.putMpvGpuNext(true);
        PlayerSetting.putMpvVulkan(true);
        assertTrue(PlayerSetting.isDebug());
        assertTrue(PlayerSetting.isMpvGpuNext());
        assertTrue(PlayerSetting.isMpvVulkan());
        PlayerSetting.putEngine(-1);
        assertTrue(PlayerSetting.isExo());
        PlayerSetting.putBackground(99);
        assertEquals(2, PlayerSetting.getBackground());
    }
}

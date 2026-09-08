package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class LiveSettingTest {

    @Test
    public void bootDefaultIsFalse() {
        assertFalse(LiveSetting.isBoot());
        LiveSetting.putBoot(true);
        assertTrue(LiveSetting.isBoot());
    }

    @Test
    public void acrossDefaultIsTrue() {
        assertTrue(LiveSetting.isAcross());
        LiveSetting.putAcross(false);
        assertFalse(LiveSetting.isAcross());
    }

    @Test
    public void changeDefaultIsTrue() {
        assertTrue(LiveSetting.isChange());
        LiveSetting.putChange(false);
        assertFalse(LiveSetting.isChange());
    }

    @Test
    public void invertDefaultIsFalse() {
        assertFalse(LiveSetting.isInvert());
        LiveSetting.putInvert(true);
        assertTrue(LiveSetting.isInvert());
    }

    @Test
    public void scaleIsClamped() {
        LiveSetting.putScale(-1);
        assertEquals(PlayerSetting.MIN_SCALE, LiveSetting.getScale());
        LiveSetting.putScale(99);
        assertEquals(PlayerSetting.MAX_SCALE, LiveSetting.getScale());
        LiveSetting.putScale(2);
        assertEquals(2, LiveSetting.getScale());
    }
}

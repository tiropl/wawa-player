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
public class SubtitleSettingTest {
    @Test public void clampsAdjustmentsAndTracks() {
        SubtitleSetting.putScale(9); assertEquals(SubtitleSetting.MAX_SCALE, SubtitleSetting.getScale(), 0);
        SubtitleSetting.putPosition(-99); assertEquals(SubtitleSetting.MIN_POSITION, SubtitleSetting.getPosition(), 0);
        SubtitleSetting.putSecondaryTrackId(-9); assertEquals(SubtitleSetting.SECONDARY_SUBTITLE_OFF, SubtitleSetting.getSecondaryTrackId());
        SubtitleSetting.putSecondaryPosition(999); assertEquals(SubtitleSetting.MAX_SECONDARY_POSITION, SubtitleSetting.getSecondaryPosition(), 0);
    }

    @Test public void styleAndOpacityStateIsReported() {
        SubtitleSetting.putStyleSource(SubtitleSetting.STYLE_SOURCE_CUSTOM);
        assertTrue(SubtitleSetting.isCustomStyle());
        assertTrue(SubtitleSetting.isStyleForced());
        SubtitleSetting.putTextColor(0x80FF0000);
        SubtitleSetting.putTextOpacity(0.5f);
        assertEquals(0x40FF0000, SubtitleSetting.getTextColor());
        SubtitleSetting.putStyleSource(SubtitleSetting.STYLE_SOURCE_ORIGINAL);
        assertFalse(SubtitleSetting.isStyleForced());
    }
}

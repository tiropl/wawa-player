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

    @Test public void opacityEdgeAndPositionValuesClamp() {
        SubtitleSetting.putTextOpacity(2);
        SubtitleSetting.putBackgroundOpacity(-1);
        SubtitleSetting.putEdgeOpacity(2);
        SubtitleSetting.putEdgeWidth(99);
        SubtitleSetting.putShadow(-1);
        SubtitleSetting.putPosition(0.2f);
        assertEquals(1.0f, SubtitleSetting.getTextOpacity(), 0);
        assertEquals(0.0f, SubtitleSetting.getBackgroundOpacity(), 0);
        assertEquals(1.0f, SubtitleSetting.getEdgeOpacity(), 0);
        assertEquals(SubtitleSetting.MAX_EDGE_WIDTH, SubtitleSetting.getEdgeWidth(), 0);
        assertEquals(SubtitleSetting.MIN_SHADOW, SubtitleSetting.getShadow(), 0);
        assertEquals(20.0f, SubtitleSetting.getPosition(), 0);
        assertTrue(SubtitleSetting.isPositionSet());
    }

    @Test public void resetGroupsRestoreDefaultsAndForceState() {
        SubtitleSetting.putStyleSource(SubtitleSetting.STYLE_SOURCE_CUSTOM);
        SubtitleSetting.putScale(2);
        SubtitleSetting.putPosition(10);
        SubtitleSetting.putSecondaryTrackId(4);
        SubtitleSetting.putSecondaryPosition(100);
        SubtitleSetting.resetAdjust();
        assertFalse(SubtitleSetting.isScaleForced());
        assertFalse(SubtitleSetting.isPositionSet());
        SubtitleSetting.resetAdvanced();
        assertEquals(SubtitleSetting.SECONDARY_SUBTITLE_OFF, SubtitleSetting.getSecondaryTrackId());
        assertEquals(10.0f, SubtitleSetting.getSecondaryPosition(), 0);
        SubtitleSetting.reset();
        assertFalse(SubtitleSetting.isCustomStyle());
        assertFalse(SubtitleSetting.isStyleForced());
    }
}

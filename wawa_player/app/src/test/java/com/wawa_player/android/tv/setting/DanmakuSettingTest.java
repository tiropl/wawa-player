package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class DanmakuSettingTest {

    @Test
    public void textScaleIsClamped() {
        DanmakuSetting.putTextScale(0.0f);
        assertEquals(0.5f, DanmakuSetting.getTextScale(), 0);
        DanmakuSetting.putTextScale(99.0f);
        assertEquals(3.0f, DanmakuSetting.getTextScale(), 0);
        DanmakuSetting.putTextScale(1.5f);
        assertEquals(1.5f, DanmakuSetting.getTextScale(), 0);
    }

    @Test
    public void transparencyIsClamped() {
        DanmakuSetting.putTransparency(-1.0f);
        assertEquals(0.0f, DanmakuSetting.getTransparency(), 0);
        DanmakuSetting.putTransparency(1.0f);
        assertEquals(0.9f, DanmakuSetting.getTransparency(), 0);
    }

    @Test
    public void shadowTransparencyIsClamped() {
        DanmakuSetting.putShadowTransparency(-1.0f);
        assertEquals(0.0f, DanmakuSetting.getShadowTransparency(), 0);
        DanmakuSetting.putShadowTransparency(1.0f);
        assertEquals(0.9f, DanmakuSetting.getShadowTransparency(), 0);
    }

    @Test
    public void strokeWidthMultiplierIsClamped() {
        DanmakuSetting.putStrokeWidthMultiplier(0.0f);
        assertEquals(0.05f, DanmakuSetting.getStrokeWidthMultiplier(), 0);
        DanmakuSetting.putStrokeWidthMultiplier(1.0f);
        assertEquals(0.3f, DanmakuSetting.getStrokeWidthMultiplier(), 0);
    }

    @Test
    public void projectionOffsetsAreClamped() {
        DanmakuSetting.putProjectionOffsetX(0.0f);
        assertEquals(0.02f, DanmakuSetting.getProjectionOffsetX(), 0);
        DanmakuSetting.putProjectionOffsetX(1.0f);
        assertEquals(0.15f, DanmakuSetting.getProjectionOffsetX(), 0);

        DanmakuSetting.putProjectionOffsetY(0.0f);
        assertEquals(0.02f, DanmakuSetting.getProjectionOffsetY(), 0);
        DanmakuSetting.putProjectionOffsetY(1.0f);
        assertEquals(0.15f, DanmakuSetting.getProjectionOffsetY(), 0);
    }

    @Test
    public void projectionTransparencyIsClamped() {
        DanmakuSetting.putProjectionTransparency(-1.0f);
        assertEquals(0.0f, DanmakuSetting.getProjectionTransparency(), 0);
        DanmakuSetting.putProjectionTransparency(1.0f);
        assertEquals(0.9f, DanmakuSetting.getProjectionTransparency(), 0);
    }

    @Test
    public void durationMsIsClamped() {
        DanmakuSetting.putDurationMs(0L);
        assertEquals(3000L, DanmakuSetting.getDurationMs());
        DanmakuSetting.putDurationMs(99999L);
        assertEquals(15000L, DanmakuSetting.getDurationMs());
    }

    @Test
    public void fixedDurationMsIsClamped() {
        DanmakuSetting.putFixedDurationMs(0L);
        assertEquals(2000L, DanmakuSetting.getFixedDurationMs());
        DanmakuSetting.putFixedDurationMs(99999L);
        assertEquals(10000L, DanmakuSetting.getFixedDurationMs());
    }

    @Test
    public void timeOffsetMsIsClamped() {
        DanmakuSetting.putTimeOffsetMs(-999999L);
        assertEquals(-300000L, DanmakuSetting.getTimeOffsetMs());
        DanmakuSetting.putTimeOffsetMs(999999L);
        assertEquals(300000L, DanmakuSetting.getTimeOffsetMs());
    }

    @Test
    public void maxOnScreenIsClamped() {
        DanmakuSetting.putMaxOnScreen(0);
        assertEquals(10, DanmakuSetting.getMaxOnScreen());
        DanmakuSetting.putMaxOnScreen(9999);
        assertEquals(500, DanmakuSetting.getMaxOnScreen());
    }

    @Test
    public void scrollAreaRatioIsClamped() {
        DanmakuSetting.putScrollAreaRatio(0.0f);
        assertEquals(0.1f, DanmakuSetting.getScrollAreaRatio(), 0);
        DanmakuSetting.putScrollAreaRatio(2.0f);
        assertEquals(1.0f, DanmakuSetting.getScrollAreaRatio(), 0);
    }

    @Test
    public void maxScrollLinesIsClamped() {
        DanmakuSetting.putMaxScrollLines(-1);
        assertEquals(0, DanmakuSetting.getMaxScrollLines());
        DanmakuSetting.putMaxScrollLines(99);
        assertEquals(20, DanmakuSetting.getMaxScrollLines());
    }

    @Test
    public void maxTopAndBottomLinesAreClamped() {
        DanmakuSetting.putMaxTopLines(-1);
        assertEquals(0, DanmakuSetting.getMaxTopLines());
        DanmakuSetting.putMaxTopLines(99);
        assertEquals(10, DanmakuSetting.getMaxTopLines());

        DanmakuSetting.putMaxBottomLines(-1);
        assertEquals(0, DanmakuSetting.getMaxBottomLines());
        DanmakuSetting.putMaxBottomLines(99);
        assertEquals(10, DanmakuSetting.getMaxBottomLines());
    }

    @Test
    public void lineSpacingIsClamped() {
        DanmakuSetting.putLineSpacing(0.0f);
        assertEquals(1.0f, DanmakuSetting.getLineSpacing(), 0);
        DanmakuSetting.putLineSpacing(99.0f);
        assertEquals(2.0f, DanmakuSetting.getLineSpacing(), 0);
    }

    @Test
    public void scrollGapRatioIsClamped() {
        DanmakuSetting.putScrollGapRatio(-1.0f);
        assertEquals(0.0f, DanmakuSetting.getScrollGapRatio(), 0);
        DanmakuSetting.putScrollGapRatio(99.0f);
        assertEquals(5.0f, DanmakuSetting.getScrollGapRatio(), 0);
    }

    @Test
    public void booleanTogglesStoreAndRetrieve() {
        DanmakuSetting.putSpiderFirst(true);
        assertTrue(DanmakuSetting.isSpiderFirst());

        DanmakuSetting.putShow(false);
        assertFalse(DanmakuSetting.isShow());

        DanmakuSetting.putTextBold(true);
        assertTrue(DanmakuSetting.isTextBold());

        DanmakuSetting.putShowScroll(false);
        assertFalse(DanmakuSetting.isShowScroll());

        DanmakuSetting.putShowTop(false);
        assertFalse(DanmakuSetting.isShowTop());

        DanmakuSetting.putShowBottom(false);
        assertFalse(DanmakuSetting.isShowBottom());

        DanmakuSetting.putShowReverse(false);
        assertFalse(DanmakuSetting.isShowReverse());

        DanmakuSetting.putShowPositioned(false);
        assertFalse(DanmakuSetting.isShowPositioned());

        DanmakuSetting.putShowSubtitle(false);
        assertFalse(DanmakuSetting.isShowSubtitle());

        DanmakuSetting.putShowSpecial(false);
        assertFalse(DanmakuSetting.isShowSpecial());
    }

    @Test
    public void apiUrlStoresAndRetrieves() {
        DanmakuSetting.putApiUrl("https://danmaku.example.com");
        assertEquals("https://danmaku.example.com", DanmakuSetting.getApiUrl());
        DanmakuSetting.putApiUrl("");
        assertEquals("", DanmakuSetting.getApiUrl());
    }

    @Test
    public void styleAndColorModeStoreAndRetrieve() {
        DanmakuSetting.putStyleMode(5);
        assertEquals(5, DanmakuSetting.getStyleMode());
        DanmakuSetting.putColorMode(2);
        assertEquals(2, DanmakuSetting.getColorMode());
    }

    @Test
    public void getEffectiveApiUrlReturnsUserUrlWhenSet() {
        DanmakuSetting.putApiUrl("https://custom.danmaku.api");
        assertEquals("https://custom.danmaku.api", DanmakuSetting.getEffectiveApiUrl());
    }
}

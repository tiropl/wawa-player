package com.wawa_player.android.tv.playback;

import static org.junit.Assert.assertEquals;

import android.content.pm.ActivityInfo;

import org.junit.Test;

public class PlaybackOrientationTest {
    @Test
    public void booleanOrientationChoicesUseExpectedConstants() {
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT,
                PlaybackOrientation.getEnterFullscreenOrientation(true));
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
                PlaybackOrientation.getEnterFullscreenOrientation(false));
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT,
                PlaybackOrientation.getExitFullscreenOrientation(true));
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_FULL_USER,
                PlaybackOrientation.getExitFullscreenOrientation(false));
    }

    @Test
    public void fixedOrientationChoicesUseExpectedConstants() {
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_FULL_USER,
                PlaybackOrientation.getPortAutoRotateOrientation());
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE,
                PlaybackOrientation.getLandAutoRotateOrientation());
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT,
                PlaybackOrientation.getPortraitVideoSizeOrientation());
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE,
                PlaybackOrientation.getLandscapeVideoSizeOrientation());
    }
}

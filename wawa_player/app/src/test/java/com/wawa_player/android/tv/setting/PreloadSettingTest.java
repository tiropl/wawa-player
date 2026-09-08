package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class PreloadSettingTest {

    @Test
    public void enabledDefaultIsFalse() {
        assertFalse(PreloadSetting.isEnabled());
        PreloadSetting.putEnabled(true);
        assertTrue(PreloadSetting.isEnabled());
    }

    @Test
    public void nextEpisodeEnabledDefaultIsFalse() {
        assertFalse(PreloadSetting.isNextEpisodeEnabled());
        PreloadSetting.putNextEpisodeEnabled(true);
        assertTrue(PreloadSetting.isNextEpisodeEnabled());
    }

    @Test
    public void threadsIsClamped() {
        PreloadSetting.putThreads(0);
        assertEquals(PreloadSetting.MIN_THREADS, PreloadSetting.getThreads());
        PreloadSetting.putThreads(99);
        assertEquals(PreloadSetting.MAX_THREADS, PreloadSetting.getThreads());
        PreloadSetting.putThreads(5);
        assertEquals(5, PreloadSetting.getThreads());
    }

    @Test
    public void sizeMbRoundsToStep() {
        // 200 is on step (128 + 72, nearest step is 128 + 128 = 256... let me recalculate)
        // Step formula: MIN + round((value - MIN) / STEP) * STEP
        // 200: 128 + round(72/128)*128 = 128 + round(0.5625)*128 = 128 + 1*128 = 256
        PreloadSetting.putSizeMb(200);
        assertEquals(256, PreloadSetting.getSizeMb());

        // 128: on step
        PreloadSetting.putSizeMb(128);
        assertEquals(128, PreloadSetting.getSizeMb());

        // 256: on step
        PreloadSetting.putSizeMb(256);
        assertEquals(256, PreloadSetting.getSizeMb());
    }

    @Test
    public void sizeMbIsClamped() {
        PreloadSetting.putSizeMb(0);
        assertEquals(PreloadSetting.MIN_SIZE_MB, PreloadSetting.getSizeMb());
        PreloadSetting.putSizeMb(99999);
        assertEquals(PreloadSetting.MAX_SIZE_MB, PreloadSetting.getSizeMb());
    }

    @Test
    public void sizeBytesConvertsFromMb() {
        PreloadSetting.putSizeMb(128);
        assertEquals(128L * 1024 * 1024, PreloadSetting.getSizeBytes());
    }

    @Test
    public void timeSecondsRoundsToStep() {
        // Step formula: MIN + round((value - MIN) / STEP) * STEP
        // 35: 20 + round(15/10)*10 = 20 + 2*10 = 40
        PreloadSetting.putTimeSeconds(35);
        assertEquals(40, PreloadSetting.getTimeSeconds());

        // 20: on step
        PreloadSetting.putTimeSeconds(20);
        assertEquals(20, PreloadSetting.getTimeSeconds());

        // 120: on step (max)
        PreloadSetting.putTimeSeconds(120);
        assertEquals(120, PreloadSetting.getTimeSeconds());
    }

    @Test
    public void timeSecondsIsClamped() {
        PreloadSetting.putTimeSeconds(0);
        assertEquals(PreloadSetting.MIN_TIME_SECONDS, PreloadSetting.getTimeSeconds());
        PreloadSetting.putTimeSeconds(999);
        assertEquals(PreloadSetting.MAX_TIME_SECONDS, PreloadSetting.getTimeSeconds());
    }

    @Test
    public void durationMsConvertsFromSeconds() {
        PreloadSetting.putTimeSeconds(20);
        assertEquals(20000L, PreloadSetting.getDurationMs());
    }
}

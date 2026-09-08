package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

public class SpeedSettingTest {
    @Test public void clampsAndFormats() {
        assertEquals(0.1f, SpeedSetting.clamp(-1), 0);
        assertEquals(5.0f, SpeedSetting.clamp(9), 0);
        assertEquals(2.0f, SpeedSetting.clampLongPress(0), 0);
        assertEquals(5.0f, SpeedSetting.clampLongPress(9), 0);
        assertEquals("1.0x", SpeedSetting.format(1));
        assertEquals("1.25x", SpeedSetting.format(1.25f));
    }

    @Test public void advancesPresetsAndReturnsCopy() {
        assertEquals(0.5f, SpeedSetting.next(0.1f), 0);
        assertEquals(1.2f, SpeedSetting.next(1.0f), 0);
        assertEquals(0.1f, SpeedSetting.next(5.0f), 0);
        float[] presets = SpeedSetting.getPresets();
        presets[0] = 99;
        assertArrayEquals(new float[]{0.1f, 0.5f},
                new float[]{SpeedSetting.getPresets()[0], SpeedSetting.getPresets()[1]}, 0);
    }

    @Test public void formatUsesLocaleAndClampsValues() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            assertEquals("5.0x", SpeedSetting.format(99));
            assertEquals("0.1x", SpeedSetting.format(-1));
            Locale.setDefault(Locale.GERMANY);
            assertEquals("1,25x", SpeedSetting.format(1.25f));
        } finally {
            Locale.setDefault(original);
        }
    }
}

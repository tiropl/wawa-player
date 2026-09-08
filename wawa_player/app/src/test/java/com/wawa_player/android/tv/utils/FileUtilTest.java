package com.wawa_player.android.tv.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class FileUtilTest {
    @Test
    public void formatsByteCountsAtUnitBoundaries() {
        assertEquals("None", FileUtil.byteCountToDisplaySize(0));
        assertEquals("None", FileUtil.byteCountToDisplaySize(-1));
        assertEquals("1 bytes", FileUtil.byteCountToDisplaySize(1));
        assertEquals("1 KB", FileUtil.byteCountToDisplaySize(1024));
        assertEquals("1 MB", FileUtil.byteCountToDisplaySize(1024L * 1024));
        assertEquals("1 GB", FileUtil.byteCountToDisplaySize(1024L * 1024 * 1024));
    }

    @Test
    public void formatsLargeValuesWithoutLeavingSupportedUnits() {
        assertEquals("1 TB", FileUtil.byteCountToDisplaySize(1024L * 1024 * 1024 * 1024));
        assertEquals("1.5 KB", FileUtil.byteCountToDisplaySize(1536));
    }
}

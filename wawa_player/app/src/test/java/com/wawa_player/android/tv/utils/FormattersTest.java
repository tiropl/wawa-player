package com.wawa_player.android.tv.utils;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.Test;

public class FormattersTest {
    @Test
    public void formatsFixedDateTimesAndUtcRanges() {
        Instant instant = Instant.parse("2024-01-02T03:04:05Z");
        assertEquals("2024-01-02", Formatters.DATE.format(instant.atOffset(ZoneOffset.UTC)));
        assertEquals("03:04", Formatters.TIME.format(instant.atOffset(ZoneOffset.UTC)));
        assertEquals(Formatters.TIME_SEC.format(instant.atZone(java.time.ZoneId.systemDefault())), Formatters.TIME_SEC.format(instant));
        assertEquals("20240102T030405Z", Formatters.EPG_RANGE.format(instant));
        assertEquals("20240102030405 +0000", Formatters.EPG_FULL.format(instant.atOffset(ZoneOffset.UTC)));
    }
}

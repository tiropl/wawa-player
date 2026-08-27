package com.wawa_player.android.tv.bean;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.ZoneId;
import org.junit.Test;

public class EpgDataTest {
    @Test public void formattingBranches() {
        EpgData item = new EpgData();
        item.setTitle("Show");
        assertEquals("Show", item.format());
        assertEquals("", item.getTime());
        item.setStart("10:00"); item.setEnd("11:00");
        assertEquals("10:00 ~ 11:00  Show", item.format());
        assertEquals("10:00 ~ 11:00", item.getTime());
    }

    @Test public void blankValuesAndEqualityAreStable() {
        EpgData a = new EpgData();
        EpgData b = new EpgData();
        assertEquals("", a.getTitle()); assertEquals("", a.getStart()); assertEquals("", a.getEnd());
        assertEquals(a, b); assertEquals(a.hashCode(), b.hashCode());
        a.setTitle("x"); a.setStart("09:00"); a.setEnd("10:00");
        b.setTitle("x"); b.setStart("09:00"); b.setEnd("10:00");
        assertEquals(a, b);
    }

    @Test public void rangeAndDayCorrection() {
        long start = System.currentTimeMillis() - 1000;
        long end = System.currentTimeMillis() + 1000;
        EpgData item = new EpgData(); item.setStartTime(start); item.setEndTime(end);
        assertTrue(item.isInRange()); assertFalse(item.isFuture());
        item.setStartTime(end + 1); assertTrue(item.isFuture());
        item.setEndTime(Instant.parse("2026-08-28T23:00:00Z").toEpochMilli());
        item.checkDay(ZoneId.of("UTC"));
        assertEquals(Instant.parse("2026-08-29T23:00:00Z").toEpochMilli(), item.getEndTime());
    }
}

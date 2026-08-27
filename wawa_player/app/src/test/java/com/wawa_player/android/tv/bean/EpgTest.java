package com.wawa_player.android.tv.bean;

import static org.junit.Assert.*;

import java.util.Arrays;
import org.junit.Test;

public class EpgTest {
    @Test public void createAndSelectionDefaults() {
        Epg epg = Epg.create("news", "2026-08-28");
        assertEquals("news", epg.getKey());
        assertEquals("2026-08-28", epg.getDate());
        assertTrue(epg.getList().isEmpty());
        assertEquals(-1, epg.getSelected());
        assertEquals(-1, epg.getInRange());
        assertEquals("", epg.getEpgData().getTitle());
        assertTrue(epg.equal("2026-08-28"));
    }

    @Test public void selectedAndInRangeUseListOrder() {
        EpgData first = new EpgData();
        first.setTitle("first"); first.setSelected(true);
        EpgData second = new EpgData();
        second.setTitle("second"); second.setSelected(true);
        Epg epg = Epg.create("k", "d");
        epg.setList(Arrays.asList(first, second));
        assertEquals(0, epg.getSelected());
        assertSame(first, epg.getEpgData());
        epg.selected();
        assertFalse(first.isSelected());
        assertFalse(second.isSelected());
    }
}

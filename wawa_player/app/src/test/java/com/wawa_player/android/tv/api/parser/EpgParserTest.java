package com.wawa_player.android.tv.api.parser;

import static org.junit.Assert.*;

import com.wawa_player.android.tv.bean.Epg;
import java.time.ZoneId;
import org.junit.Test;

public class EpgParserTest {
    private static final ZoneId UTC = ZoneId.of("UTC");

    @Test public void parsesDateAndProgramme() {
        String xml = "<tv date=\"20260828120000 +0000\"><programme channel=\"c\" start=\"20260828130000 +0000\" stop=\"20260828140000 +0000\"><title>News</title></programme></tv>";
        Epg epg = EpgParser.getEpg(xml, "c", UTC);
        assertEquals("c", epg.getKey()); assertEquals("2026-08-28", epg.getDate());
        assertEquals(1, epg.getList().size());
        assertEquals("News", epg.getList().get(0).getTitle());
        assertEquals("13:00", epg.getList().get(0).getStart());
    }

    @Test public void invalidXmlReturnsEmptyEpg() {
        Epg epg = EpgParser.getEpg("not xml", "key", UTC);
        assertTrue(epg.getList().isEmpty());
        assertEquals("", epg.getKey());
    }

    @Test public void dateMissingUsesZoneDate() {
        Epg epg = EpgParser.getEpg("<tv><programme channel=\"c\" start=\"bad\" stop=\"bad\"><title></title></programme></tv>", "k", UTC);
        assertEquals("k", epg.getKey());
        assertEquals(1, epg.getList().size());
        assertEquals(0, epg.getList().get(0).getStartTime());
    }
}

package com.wawa_player.android.tv.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class BackupTest {

    @Test
    public void objectFromReadsListsAndPrefers() {
        Backup backup = Backup.objectFrom("{\"site\":[{}],\"live\":[{}],\"keep\":[{}],\"config\":[{}],\"history\":[{}],\"prefers\":{\"volume\":75}}");

        assertEquals(1, backup.getSite().size());
        assertEquals(1, backup.getLive().size());
        assertEquals(1, backup.getKeep().size());
        assertEquals(1, backup.getConfig().size());
        assertEquals(1, backup.getHistory().size());
        assertEquals(75L, ((Number) backup.getPrefers().get("volume")).longValue());
    }

    @Test
    public void missingOrMalformedDataUsesEmptyDefaults() {
        Backup empty = Backup.objectFrom("{}");
        Backup malformed = Backup.objectFrom("not json");
        Backup nullValue = Backup.objectFrom("null");

        for (Backup backup : Arrays.asList(empty, malformed, nullValue)) {
            assertEquals(Collections.emptyList(), backup.getSite());
            assertEquals(Collections.emptyList(), backup.getLive());
            assertEquals(Collections.emptyList(), backup.getKeep());
            assertEquals(Collections.emptyList(), backup.getConfig());
            assertEquals(Collections.emptyList(), backup.getHistory());
            assertTrue(backup.getPrefers().isEmpty());
        }
    }
}

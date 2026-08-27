package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class EpgTest {

    @Test
    public void createsEpgWithKeyDateAndMutableList() {
        Epg epg = Epg.create("CCTV1", "2024-01-01");

        assertThat(epg.getKey()).isEqualTo("CCTV1");
        assertThat(epg.getDate()).isEqualTo("2024-01-01");
        assertThat(epg.getList()).isEmpty();
    }

    @Test
    public void parsesJsonDeduplicatesProgramsAndHandlesOvernightTimes() {
        String json = "{\"date\":\"2024-01-01\",\"epg_data\":["
                + "{\"title\":\"Night\",\"start\":\"23:30\",\"end\":\"00:30\"},"
                + "{\"title\":\"Night\",\"start\":\"23:30\",\"end\":\"00:30\"}]}";

        Epg epg = Epg.objectFrom(json, "CCTV1", ZoneOffset.UTC);

        assertThat(epg.getKey()).isEqualTo("CCTV1");
        assertThat(epg.getList()).hasSize(1);
        EpgData data = epg.getList().get(0);
        assertThat(data.getStartTime()).isEqualTo(Instant.parse("2024-01-01T23:30:00Z").toEpochMilli());
        assertThat(data.getEndTime()).isEqualTo(Instant.parse("2024-01-02T00:30:00Z").toEpochMilli());
    }

    @Test
    public void invalidJsonReturnsEmptyEpg() {
        Epg epg = Epg.objectFrom("{bad json", "CCTV1", ZoneOffset.UTC);

        assertThat(epg.getKey()).isEmpty();
        assertThat(epg.getList()).isEmpty();
    }
}

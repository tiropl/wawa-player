package com.wawa_player.android.tv.api.parser;

import static com.google.common.truth.Truth.assertThat;

import com.wawa_player.android.tv.bean.Epg;
import com.wawa_player.android.tv.bean.EpgData;

import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class EpgParserTest {

    @Test
    public void parsesXmltvProgramsAndUsesChannelId() {
        String xml = "<tv date=\"20240101120000 +0000\">"
                + "<channel id=\"cctv1\"><display-name>CCTV1</display-name></channel>"
                + "<programme start=\"20240101123000 +0000\" stop=\"20240101133000 +0000\" channel=\"cctv1\">"
                + "<title>News</title></programme></tv>";

        Epg epg = EpgParser.getEpg(xml, "CCTV1", ZoneOffset.UTC);

        assertThat(epg.getKey()).isEqualTo("CCTV1");
        assertThat(epg.getDate()).isEqualTo("2024-01-01");
        assertThat(epg.getList()).hasSize(1);
        EpgData data = epg.getList().get(0);
        assertThat(data.getTitle()).isEqualTo("News");
        assertThat(data.getStartTime()).isEqualTo(Instant.parse("2024-01-01T12:30:00Z").toEpochMilli());
        assertThat(data.getEndTime()).isEqualTo(Instant.parse("2024-01-01T13:30:00Z").toEpochMilli());
    }

    @Test
    public void emptyOrMalformedXmlReturnsEmptyEpg() {
        assertThat(EpgParser.getEpg("<tv></tv>", "CCTV1", ZoneOffset.UTC).getList()).isEmpty();
        assertThat(EpgParser.getEpg("not xml", "CCTV1", ZoneOffset.UTC).getKey()).isEmpty();
    }
}

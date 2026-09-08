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

    @Test
    public void parsesMultipleProgrammesAndPreservesInputOrder() {
        String xml = "<tv date=\"20240101120000 +0000\">"
                + "<channel id=\"one\"><display-name>One</display-name></channel>"
                + "<programme start=\"20240101100000 +0000\" stop=\"20240101110000 +0000\" channel=\"one\"><title>Morning</title></programme>"
                + "<programme start=\"20240101110000 +0000\" stop=\"20240101120000 +0000\" channel=\"other\"><title>Other</title></programme>"
                + "<programme start=\"20240101120000 +0000\" stop=\"20240101130000 +0000\" channel=\"one\"><title>Noon</title></programme>"
                + "</tv>";

        Epg epg = EpgParser.getEpg(xml, "one", ZoneOffset.UTC);

        assertThat(epg.getList()).hasSize(3);
        assertThat(epg.getList().get(0).getTitle()).isEqualTo("Morning");
        assertThat(epg.getList().get(1).getTitle()).isEqualTo("Other");
        assertThat(epg.getList().get(2).getTitle()).isEqualTo("Noon");
    }

    @Test
    public void missingProgrammeFieldsBecomeEpochAndEmptyTitle() {
        String xml = "<tv><channel id=\"one\"><display-name>One</display-name></channel>"
                + "<programme channel=\"one\"><title></title></programme></tv>";

        EpgData data = EpgParser.getEpg(xml, "key", ZoneOffset.UTC).getList().get(0);

        assertThat(data.getTitle()).isEmpty();
        assertThat(data.getStartTime()).isEqualTo(0L);
        assertThat(data.getEndTime()).isEqualTo(0L);
        assertThat(data.getStart()).isEqualTo("00:00");
        assertThat(data.getEnd()).isEqualTo("00:00");
    }

    @Test
    public void parsesOffsetAndConvertsToRequestedZone() {
        String xml = "<tv date=\"20240101120000 +0000\">"
                + "<programme start=\"20240101230000 +0000\" stop=\"20240102010000 +0000\" channel=\"one\"><title>Late</title></programme>"
                + "</tv>";

        EpgData data = EpgParser.getEpg(xml, "key", ZoneOffset.ofHours(8)).getList().get(0);

        assertThat(data.getStart()).isEqualTo("07:00");
        assertThat(data.getEnd()).isEqualTo("09:00");
        assertThat(data.getStartTime()).isEqualTo(Instant.parse("2024-01-01T23:00:00Z").toEpochMilli());
    }

    @Test
    public void invalidDateFallsBackToEpochDate() {
        String xml = "<tv date=\"invalid\"><programme start=\"invalid\" stop=\"invalid\" channel=\"one\"><title>Broken</title></programme></tv>";

        Epg epg = EpgParser.getEpg(xml, "key", ZoneOffset.UTC);

        assertThat(epg.getDate()).isEqualTo("1970-01-01");
        assertThat(epg.getList()).hasSize(1);
        assertThat(epg.getList().get(0).getStartTime()).isEqualTo(0L);
    }

    @Test
    public void emptyKeyAndNoTimezoneUseCurrentZoneDateAndRetainPrograms() {
        String xml = "<tv><programme start=\"20240101080000\" stop=\"20240101090000\" channel=\"one\"><title>Local</title></programme></tv>";

        Epg epg = EpgParser.getEpg(xml, "", ZoneOffset.UTC);

        assertThat(epg.getKey()).isEmpty();
        assertThat(epg.getList()).hasSize(1);
        assertThat(epg.getList().get(0).getTitle()).isEqualTo("Local");
    }
}

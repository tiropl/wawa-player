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
public class EpgDataTest {

    @Test
    public void formatHandlesEmptyAndCompleteFields() {
        EpgData empty = new EpgData();
        assertThat(empty.format()).isEmpty();
        assertThat(empty.getTime()).isEmpty();

        EpgData titleOnly = new EpgData();
        titleOnly.setTitle("News");
        assertThat(titleOnly.format()).isEqualTo("News");

        EpgData complete = new EpgData();
        complete.setTitle("News");
        complete.setStart("08:00");
        complete.setEnd("09:00");
        assertThat(complete.format()).isEqualTo("08:00 ~ 09:00  News");
        assertThat(complete.getTime()).isEqualTo("08:00 ~ 09:00");
    }

    @Test
    public void rangeUsesUtcAndCheckDayAddsOneDay() {
        EpgData data = new EpgData();
        data.setStartTime(Instant.parse("2024-01-01T01:00:00Z").toEpochMilli());
        data.setEndTime(Instant.parse("2024-01-01T02:00:00Z").toEpochMilli());

        assertThat(data.getRange()).isEqualTo("clock=20240101T010000Z-20240101T020000Z");
        long oldEnd = data.getEndTime();
        data.checkDay(ZoneOffset.UTC);
        assertThat(data.getEndTime()).isEqualTo(oldEnd + 24 * 60 * 60 * 1000L);
    }

    @Test
    public void equalityIgnoresSelectionAndEpochFields() {
        EpgData first = new EpgData();
        first.setTitle("News");
        first.setStart("08:00");
        first.setEnd("09:00");
        first.setSelected(true);
        first.setStartTime(100);

        EpgData second = new EpgData();
        second.setTitle("News");
        second.setStart("08:00");
        second.setEnd("09:00");
        second.setEndTime(200);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }
}

package com.wawa_player.android.tv;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ConstantTest {

    @Test
    public void timeout_constants() {
        assertThat(Constant.INTERVAL_SEEK).isEqualTo(10_000);
        assertThat(Constant.INTERVAL_HIDE).isEqualTo(3_000);
        assertThat(Constant.TIMEOUT_VOD).isEqualTo(10_000);
        assertThat(Constant.TIMEOUT_LIVE).isEqualTo(10_000);
        assertThat(Constant.TIMEOUT_EPG).isEqualTo(5_000);
        assertThat(Constant.TIMEOUT_XML).isEqualTo(15_000);
        assertThat(Constant.TIMEOUT_PLAY).isEqualTo(15_000);
        assertThat(Constant.TIMEOUT_SYNC).isEqualTo(2_000);
        assertThat(Constant.TIMEOUT_SEARCH).isEqualTo(10_000);
        assertThat(Constant.TIMEOUT_PARSE_DEF).isEqualTo(15_000);
        assertThat(Constant.TIMEOUT_PARSE_WEB).isEqualTo(15_000);
        assertThat(Constant.TIMEOUT_PARSE_LIVE).isEqualTo(10_000);
        assertThat(Constant.TIMEOUT_CONFIG).isEqualTo(15_000);
        assertThat(Constant.TIMEOUT_SCAN).isEqualTo(1_000);
        assertThat(Constant.HISTORY_TIME).isEqualTo(TimeUnit.DAYS.toMillis(60));
    }

    @Test
    public void getOpEdLimit_short_duration() {
        // < 15 min → 3 min
        assertThat(Constant.getOpEdLimit(TimeUnit.MINUTES.toMillis(14))).isEqualTo(TimeUnit.MINUTES.toMillis(3));
        assertThat(Constant.getOpEdLimit(0)).isEqualTo(TimeUnit.MINUTES.toMillis(3));
        assertThat(Constant.getOpEdLimit(TimeUnit.MINUTES.toMillis(15) - 1)).isEqualTo(TimeUnit.MINUTES.toMillis(3));
    }

    @Test
    public void getOpEdLimit_medium_duration() {
        // 15-29 min → 6 min
        assertThat(Constant.getOpEdLimit(TimeUnit.MINUTES.toMillis(15))).isEqualTo(TimeUnit.MINUTES.toMillis(6));
        assertThat(Constant.getOpEdLimit(TimeUnit.MINUTES.toMillis(29))).isEqualTo(TimeUnit.MINUTES.toMillis(6));
    }

    @Test
    public void getOpEdLimit_long_duration() {
        // >= 30 min → 10 min
        assertThat(Constant.getOpEdLimit(TimeUnit.MINUTES.toMillis(30))).isEqualTo(TimeUnit.MINUTES.toMillis(10));
        assertThat(Constant.getOpEdLimit(TimeUnit.HOURS.toMillis(2))).isEqualTo(TimeUnit.MINUTES.toMillis(10));
    }
}

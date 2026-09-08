package com.wawa_player.android.tv.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class SliderUtilTest {

    @Test
    public void clampsValuesAndLeavesStepDisabledValuesUnchanged() {
        assertThat(SliderUtil.snap(-1f, 0f, 10f, 1f)).isEqualTo(0f);
        assertThat(SliderUtil.snap(11f, 0f, 10f, 1f)).isEqualTo(10f);
        assertThat(SliderUtil.snap(4.25f, 0f, 10f, 0f)).isEqualTo(4.25f);
    }

    @Test
    public void snapsFromConfiguredMinimumAcrossNegativeRange() {
        assertThat(SliderUtil.snap(-0.74f, -2f, 2f, 0.5f)).isEqualTo(-0.5f);
        assertThat(SliderUtil.snap(0.24f, -2f, 2f, 0.5f)).isEqualTo(0f);
        assertThat(SliderUtil.snap(1.9f, -2f, 2f, 0.5f)).isEqualTo(2f);
    }
}

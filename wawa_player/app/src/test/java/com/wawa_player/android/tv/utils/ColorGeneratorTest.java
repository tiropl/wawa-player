package com.wawa_player.android.tv.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class ColorGeneratorTest {

    @Test
    public void returnsStableColorsForSameKey() {
        assertThat(ColorGenerator.get400("channel")).isEqualTo(ColorGenerator.get400("channel"));
        assertThat(ColorGenerator.get700("channel")).isEqualTo(ColorGenerator.get700("channel"));
    }

    @Test
    public void returnsDefinedColorsForEmptyAndUnicodeKeys() {
        assertThat(ColorGenerator.get400("")).isNotEqualTo(0);
        assertThat(ColorGenerator.get700("中文频道")).isNotEqualTo(0);
    }
}

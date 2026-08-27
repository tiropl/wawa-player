package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class FilterTest {

    @Test
    public void arrayFromParsesValuesAndSetSelectedMarksMatchingValue() {
        Filter filter = Filter.objectFrom(JsonParser.parseString("{\"key\":\"year\",\"name\":\"Year\",\"init\":\"2024\",\"value\":[{\"n\":\"Now\",\"v\":\"2024\"},{\"v\":\"2023\"}]}"));

        assertThat(filter.getKey()).isEqualTo("year");
        assertThat(filter.getName()).isEqualTo("Year");
        assertThat(filter.getInit()).isEqualTo("2024");
        assertThat(filter.setSelected("2023")).isEqualTo("2023");
        assertThat(filter.getValue().get(1).isSelected()).isTrue();
        assertThat(filter.getValue().get(0).isSelected()).isFalse();
    }

    @Test
    public void copyIsIndependentAndCheckRemovesNullValues() {
        Filter filter = Filter.objectFrom(JsonParser.parseString("{\"key\":\"k\",\"value\":[null,{\"v\":\"one\"}]}"));
        filter.check();
        Filter copy = filter.copy();
        copy.getValue().get(0).setV("two");

        assertThat(filter.getValue()).hasSize(1);
        assertThat(filter.getValue().get(0).getV()).isEqualTo("one");
        assertThat(copy.getValue().get(0).getV()).isEqualTo("two");
    }
}

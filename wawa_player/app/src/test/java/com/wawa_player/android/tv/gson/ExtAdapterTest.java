package com.wawa_player.android.tv.gson;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonParser;

import org.junit.Test;

public class ExtAdapterTest {

    private final ExtAdapter adapter = new ExtAdapter();

    @Test
    public void preservesPrimitiveObjectAndArrayRepresentations() {
        assertThat(adapter.deserialize(JsonParser.parseString("\"value\""), String.class, null)).isEqualTo("value");
        assertThat(adapter.deserialize(JsonParser.parseString("123"), String.class, null)).isEqualTo("123");
        assertThat(adapter.deserialize(JsonParser.parseString("{\"a\":1}"), String.class, null)).isEqualTo("{\"a\":1}");
        assertThat(adapter.deserialize(JsonParser.parseString("[1,2]"), String.class, null)).isEqualTo("[1,2]");
    }

    @Test
    public void returnsEmptyStringForJsonNull() {
        assertThat(adapter.deserialize(JsonParser.parseString("null"), String.class, null)).isEmpty();
    }
}

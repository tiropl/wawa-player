package com.wawa_player.android.tv.gson;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonParser;

import org.junit.Test;

public class MsgAdapterTest {

    private final MsgAdapter adapter = new MsgAdapter();

    @Test
    public void convertsStringNumberAndBooleanPrimitives() {
        assertThat(adapter.deserialize(JsonParser.parseString("\"ok\""), String.class, null)).isEqualTo("ok");
        assertThat(adapter.deserialize(JsonParser.parseString("7"), String.class, null)).isEqualTo("7");
        assertThat(adapter.deserialize(JsonParser.parseString("true"), String.class, null)).isEqualTo("true");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void rejectsJsonNull() {
        adapter.deserialize(JsonParser.parseString("null"), String.class, null);
    }
}

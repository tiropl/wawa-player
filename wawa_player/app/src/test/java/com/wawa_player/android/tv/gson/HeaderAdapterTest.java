package com.wawa_player.android.tv.gson;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonParser;

import org.junit.Test;

public class HeaderAdapterTest {

    @Test
    public void parsesObjectValuesToStringMap() {
        java.util.Map<String, String> headers = new HeaderAdapter().deserialize(
                JsonParser.parseString("{\"User-Agent\":\"Player\",\"Retry\":3,\"Enabled\":true}"),
                null, null);

        assertThat(headers).containsExactly("User-Agent", "Player", "Retry", "3", "Enabled", "true");
    }

    @Test
    public void returnsEmptyMapForNullOrNonObjectInputs() {
        HeaderAdapter adapter = new HeaderAdapter();
        assertThat(adapter.deserialize(JsonParser.parseString("null"), null, null)).isEmpty();
        assertThat(adapter.deserialize(JsonParser.parseString("[]"), null, null)).isEmpty();
    }
}

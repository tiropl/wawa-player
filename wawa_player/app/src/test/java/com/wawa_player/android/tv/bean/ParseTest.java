package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class ParseTest {

    @Test
    public void objectFromReadsFieldsAndEmptyParseHasDefaultValues() {
        Parse parse = Parse.objectFrom(JsonParser.parseString("{\"name\":\"API\",\"type\":1,\"url\":\"https://example.test/api\"}"));

        assertThat(parse.getName()).isEqualTo("API");
        assertThat(parse.getType()).isEqualTo(1);
        assertThat(parse.getUrl()).isEqualTo("https://example.test/api");
        assertThat(parse.isEmpty()).isFalse();
        assertThat(new Parse().isEmpty()).isTrue();
    }

    @Test
    public void extUrlAndMixMapIncludeExtensionData() {
        Parse parse = Parse.get(1, "https://example.test/parse?token=abc");
        parse.getExt().setFlag(java.util.List.of("json"));

        assertThat(parse.extUrl()).startsWith("https://example.test/parse?cat_ext=");
        assertThat(parse.extUrl()).contains("&token=abc");
        assertThat(parse.mixMap()).containsEntry("type", "1");
        assertThat(parse.mixMap()).containsEntry("url", parse.getUrl());
    }

    @Test
    public void equalityAndSelectionUseName() {
        Parse first = Parse.get(1, "one");
        first.setName("same");
        Parse second = Parse.get(2, "two");
        second.setName("same");
        first.setSelected(second);

        assertThat(first).isEqualTo(second);
        assertThat(first.isSelected()).isTrue();
        assertThat(first.isSameItem(second)).isTrue();
    }
}

package com.wawa_player.android.tv.gson;

import static com.google.common.truth.Truth.assertThat;

import com.wawa_player.android.tv.bean.Url;
import com.google.gson.JsonParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class UrlAdapterTest {

    private final UrlAdapter adapter = new UrlAdapter();

    @Test
    public void parsesStringAndPairedArrayForms() {
        Url single = adapter.deserialize(JsonParser.parseString("\"https://one\""), Url.class, null);
        assertThat(single.v()).isEqualTo("https://one");

        Url pairs = adapter.deserialize(JsonParser.parseString("[\"Primary\",\"https://one\",\"Backup\",\"https://two\"]"), Url.class, null);
        assertThat(pairs.getValues()).hasSize(2);
        assertThat(pairs.n(0)).isEqualTo("Primary");
        assertThat(pairs.v(1)).isEqualTo("https://two");
    }

    @Test
    public void oddArrayIgnoresTrailingValueAndEmptyArrayIsEmpty() {
        Url odd = adapter.deserialize(JsonParser.parseString("[\"Primary\",\"https://one\",\"orphan\"]"), Url.class, null);
        assertThat(odd.getValues()).hasSize(1);
        assertThat(adapter.deserialize(JsonParser.parseString("[]"), Url.class, null).isEmpty()).isTrue();
    }

    @Test
    public void arrayPreservesPipeAndAdditionalEqualsInValues() {
        Url url = adapter.deserialize(JsonParser.parseString("[\"name|part\",\"https://host/a|b?x=1=2\"]"), Url.class, null);

        assertThat(url.n(0)).isEqualTo("name|part");
        assertThat(url.v(0)).isEqualTo("https://host/a|b?x=1=2");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void nullIsRejectedByStringConversion() {
        adapter.deserialize(JsonParser.parseString("null"), Url.class, null);
    }

    @Test
    public void nonStringArrayElementIsConvertedByGsonPrimitiveAccess() {
        Url url = adapter.deserialize(JsonParser.parseString("[1,\"url\"]"), Url.class, null);
        assertThat(url.n(0)).isEqualTo("1");
        assertThat(url.v(0)).isEqualTo("url");
    }

    @Test
    public void objectFormUsesObjectValues() {
        Url url = adapter.deserialize(JsonParser.parseString("{\"values\":[{\"n\":\"Name\",\"v\":\"url\"}],\"position\":0}"), Url.class, null);
        assertThat(url.n(0)).isEqualTo("Name");
        assertThat(url.v()).isEqualTo("url");
    }
}

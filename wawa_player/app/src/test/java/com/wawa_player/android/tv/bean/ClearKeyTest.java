package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class ClearKeyTest {

    @Test
    public void convertsHexKeysToUrlSafeUnpaddedBase64Json() {
        ClearKey key = ClearKey.get("00112233445566778899aabbccddeeff:ffeeddccbbaa99887766554433221100");

        String json = key.toString();
        assertThat(json).contains("\"type\":\"temporary\"");
        assertThat(json).contains("\"kty\":\"oct\"");
        assertThat(json).contains("\"kid\":\"ABEiM0RVZneImaq7zN3u_w\"");
        assertThat(json).contains("\"k\":\"_-7dzLuqmYh3ZlVEMyIRAA\"");
        assertThat(json).doesNotContain("=");
    }

    @Test
    public void convertsMultipleKeyPairs() {
        ClearKey key = ClearKey.get("00000000000000000000000000000001:00000000000000000000000000000002,00000000000000000000000000000003:00000000000000000000000000000000004");

        String json = key.toString();
        assertThat(json).contains("\"kid\":\"AAAAAAAAAAAAAAAAAAAAAQ\"");
        assertThat(json).contains("\"kid\":\"AAAAAAAAAAAAAAAAAAAAAw\"");
        assertThat(json).contains("\"k\":\"AAAAAAAAAAAAAAAAAAAAAg\"");
        assertThat(json).contains("\"k\":\"AAAAAAAAAAAAAAAAAAAAAAA\"");
    }

    @Test
    public void parsesCanonicalClearKeyJson() throws Exception {
        ClearKey key = ClearKey.objectFrom("{\"type\":\"temporary\",\"keys\":[{\"kty\":\"oct\",\"kid\":\"kid\",\"k\":\"value\"}]}");

        assertThat(new Gson().toJson(key)).contains("\"type\":\"temporary\"");
        assertThat(new Gson().toJson(key)).contains("\"kid\":\"kid\"");
    }

    @Test(expected = Exception.class)
    public void rejectsJsonWithoutKeys() throws Exception {
        ClearKey.objectFrom("{\"type\":\"temporary\"}");
    }
}

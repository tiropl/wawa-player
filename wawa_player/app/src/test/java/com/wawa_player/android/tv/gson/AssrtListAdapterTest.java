package com.wawa_player.android.tv.gson;

import com.wawa_player.android.tv.bean.AssrtResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class AssrtListAdapterTest {



    @Test
    public void deserializeArrayAndObjectForms() {
        String json = "{\"sub\":{\"subs\":[{\"id\":1,\"title\":\" First \",\"filelist\":{\"f\":\" first.srt \",\"url\":\" https://example.com/first.srt \"}},{\"id\":2,\"title\":\"Second\",\"filelist\":[{\"name\":\"second.ass\",\"url\":\"second-url\"}]}]}}";

        AssrtResponse response = AssrtResponse.from(json);

        assertEquals(2, response.getSubtitles().size());
        assertEquals(1, response.getSubtitles().get(0).getId());
        assertEquals("First", response.getSubtitles().get(0).getTitle());
        assertEquals(1, response.getSubtitles().get(0).getFiles().size());
        assertEquals("first.srt", response.getSubtitles().get(0).getFiles().get(0).getName());
        assertEquals("https://example.com/first.srt", response.getSubtitles().get(0).getFiles().get(0).getUrl());
        assertEquals("second.ass", response.getSubtitles().get(1).getFiles().get(0).getName());
    }

    @Test
    public void deserializeNullAndEmptyFormsReturnEmptyLists() {
        AssrtResponse nullPayload = AssrtResponse.from("{\"sub\":{\"subs\":null,\"filelist\":null}}");
        AssrtResponse emptyPayload = AssrtResponse.from("{\"sub\":{\"subs\":[],\"filelist\":{}}}");

        assertTrue(nullPayload.getSubtitles().isEmpty());
        assertTrue(emptyPayload.getSubtitles().isEmpty());
        assertFalse(emptyPayload.getSubtitles().stream().anyMatch(s -> s.hasFileList()));
    }

    @Test
    public void deserializeSingleObjectSubtitle() {
        AssrtResponse response = AssrtResponse.from("{\"sub\":{\"subs\":{\"id\":7,\"native_name\":\" Name \",\"lang\":{\"desc\":\" Chinese \"}}}}");

        assertEquals(1, response.getSubtitles().size());
        assertEquals(7, response.getSubtitles().get(0).getId());
        assertEquals("Name", response.getSubtitles().get(0).getNativeName());
        assertEquals("Chinese", response.getSubtitles().get(0).getLanguage());
    }

    @Test
    public void deserializeMultipleItemsPreservesOrder() {
        AssrtResponse response = AssrtResponse.from("{\"sub\":{\"subs\":[{\"id\":1},{\"id\":2}]}}");
        assertEquals(2, response.getSubtitles().size());
        assertEquals(1, response.getSubtitles().get(0).getId());
        assertEquals(2, response.getSubtitles().get(1).getId());
    }

    @Test
    public void deserializeEmptyObjectReturnsEmptyList() {
        AssrtResponse response = AssrtResponse.from("{\"sub\":{\"subs\":{}}}");
        assertTrue(response.getSubtitles().isEmpty());
    }

    @Test
    public void deserializeJsonNullReturnsEmptyList() {
        AssrtResponse response = AssrtResponse.from("{\"sub\":{\"subs\":null}}");
        assertTrue(response.getSubtitles().isEmpty());
    }

    @Test(expected = com.google.gson.JsonParseException.class)
    public void directDeserializeWithoutParameterizedTypeIsRejected() {
        new AssrtListAdapter().deserialize(com.google.gson.JsonParser.parseString("[]"), java.util.List.class, null);
    }

    @Test
    public void primitiveAndEmptyStringAreIgnored() {
        AssrtResponse primitive = AssrtResponse.from("{\"sub\":{\"subs\":1}}");
        AssrtResponse empty = AssrtResponse.from("{\"sub\":{\"subs\":\"\"}}");
        assertTrue(primitive.getSubtitles().isEmpty());
        assertTrue(empty.getSubtitles().isEmpty());
    }
}

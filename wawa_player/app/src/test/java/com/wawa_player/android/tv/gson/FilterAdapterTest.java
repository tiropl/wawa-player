package com.wawa_player.android.tv.gson;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.bean.Filter;
import com.wawa_player.android.tv.bean.Result;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FilterAdapterTest {

    @BeforeClass
    public static void setUpApp() {
        new App();
    }

    @Test
    public void deserializeObjectAndArrayPreservesOrderAndValues() {
        String json = "{\"filters\":{\"genre\":{\"key\":\"genre\",\"name\":\" Genre \",\"value\":[{\"n\":\"Drama\",\"v\":\"drama\"}]},\"year\":[{\"key\":\"year\",\"name\":\"Year\",\"value\":[{\"v\":\"2024\"}]}]}}";

        LinkedHashMap<String, List<Filter>> result = Result.objectFrom(json).getFilters();

        assertEquals(List.of("genre", "year"), List.copyOf(result.keySet()));
        assertEquals("genre", result.get("genre").get(0).getKey());
        assertEquals("Genre", result.get("genre").get(0).getName());
        assertEquals("2024", result.get("year").get(0).getValue().get(0).getV());
    }

    @Test
    public void deserializeEmptyObjectReturnsEmptyMap() {
        LinkedHashMap<String, List<Filter>> result = Result.objectFrom("{\"filters\":{}} ").getFilters();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}

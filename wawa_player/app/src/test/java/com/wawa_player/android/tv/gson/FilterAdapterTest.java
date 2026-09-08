package com.wawa_player.android.tv.gson;

import com.wawa_player.android.tv.bean.Filter;
import com.wawa_player.android.tv.bean.Result;
import com.google.gson.JsonParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class FilterAdapterTest {



    @Test
    public void deserializeObjectAndArrayPreservesOrderAndValues() {
        String json = "{\"filters\":{\"genre\":{\"key\":\"genre\",\"name\":\" Genre \",\"value\":[{\"n\":\"Drama\",\"v\":\"drama\"}]},\"year\":[{\"key\":\"year\",\"name\":\"Year\",\"value\":[{\"v\":\"2024\"}]}]}}";

        LinkedHashMap<String, List<Filter>> result = Result.objectFrom(json).getFilters();

        assertEquals(List.of("genre", "year"), List.copyOf(result.keySet()));
        assertEquals("genre", result.get("genre").get(0).getKey());
        assertEquals(" Genre ", result.get("genre").get(0).getName());
        assertEquals("2024", result.get("year").get(0).getValue().get(0).getV());
    }

    @Test
    public void deserializeEmptyObjectReturnsEmptyMap() {
        LinkedHashMap<String, List<Filter>> result = Result.objectFrom("{\"filters\":{}} ").getFilters();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void deserializeObjectWithMissingFiltersUsesEmptyMap() {
        assertEquals(0, Result.objectFrom("{}").getFilters().size());
    }

    @Test(expected = IllegalStateException.class)
    public void deserializeNullIsRejectedByObjectAccess() {
        new FilterAdapter().deserialize(JsonParser.parseString("null"), LinkedHashMap.class, null);
    }

    @Test(expected = IllegalStateException.class)
    public void deserializePrimitiveIsRejectedByObjectAccess() {
        new FilterAdapter().deserialize(JsonParser.parseString("1"), LinkedHashMap.class, null);
    }

    @Test(expected = IllegalStateException.class)
    public void deserializeArrayIsRejectedByObjectAccess() {
        new FilterAdapter().deserialize(JsonParser.parseString("[]"), LinkedHashMap.class, null);
    }

    @Test
    public void deserializeEmptyFilterArrayPreservesEmptyEntry() {
        LinkedHashMap<String, List<Filter>> result = Result.objectFrom("{\"filters\":{\"genre\":[]}}").getFilters();

        assertEquals(List.of("genre"), List.copyOf(result.keySet()));
        assertNotNull(result.get("genre"));
        assertEquals(0, result.get("genre").size());
    }

    @Test
    public void deserializeFilterPrimitiveReturnsEmptyResultOnFailure() {
        assertEquals(0, Result.objectFrom("{\"filters\":{\"genre\":\"drama\"}}").getFilters().size());
    }

    @Test
    public void deserializeMixedObjectAndArrayEntriesReturnsEmptyResultOnFailure() {
        assertEquals(0, Result.objectFrom("{\"filters\":{\"genre\":{\"key\":\"genre\"},\"year\":1}}").getFilters().size());
    }
}

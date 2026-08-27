package com.wawa_player.android.tv.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class ConfigTest {
    @Test
    public void buildersAndDescriptionPreferNameThenUrl() {
        Config config = Config.create(1).url("https://example/config").name("Custom").json("{}");
        assertEquals(1, config.getType());
        assertEquals("https://example/config", config.getUrl());
        assertEquals("Custom", config.getDesc());
        assertFalse(config.isEmpty());
        assertSame(config, config.type(2));
        assertEquals(2, config.getType());
        config.setName("");
        assertEquals("https://example/config", config.getDesc());
    }

    @Test
    public void emptyAndMalformedObjectDefaultsAreObservable() {
        Config empty = Config.create(0);
        assertTrue(empty.isEmpty());
        assertEquals("", empty.getDesc());
        Config parsed = Config.objectFrom("{\"id\":7,\"type\":2,\"url\":\"u\",\"name\":\"n\"}");
        assertEquals(7, parsed.getId());
        assertEquals("n", parsed.getDesc());
        assertEquals(0, Config.arrayFrom("null").size());
    }

    @Test
    public void equalityUsesOnlyId() {
        Config first = Config.create(0).url("one");
        Config same = Config.create(2).url("two");
        first.setId(4);
        same.setId(4);
        assertEquals(first, same);
        same.setId(5);
        assertFalse(first.equals(same));
    }
}

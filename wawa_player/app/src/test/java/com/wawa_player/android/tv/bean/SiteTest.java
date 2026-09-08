package com.wawa_player.android.tv.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.JsonParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class SiteTest {
    @Test
    public void gettersDefaultsTimeoutAndPredicates() {
        Site site = new Site();
        assertEquals("", site.getKey());
        assertEquals("", site.getName());
        assertEquals(0, site.getType().intValue());
        assertEquals(1, site.getSearchable().intValue());
        assertEquals(com.wawa_player.android.tv.Constant.TIMEOUT_PLAY, site.getTimeout());
        assertTrue(site.isEmpty());
        site.setKey("key");
        site.setName("Name");
        site.setExt("  ext  ");
        assertEquals("key", site.getKey());
        assertEquals("ext", site.getExt());
        assertFalse(site.isEmpty());
    }

    @Test
    public void objectFromMapsFieldsAndUsesSpiderWhenJarMissing() {
        Site site = Site.objectFrom(JsonParser.parseString("{\"key\":\"k\",\"name\":\"Name\",\"api\":\"api\",\"ext\":\"ext\",\"timeout\":0}"), "spider.jar");
        assertEquals("k", site.getKey());
        assertEquals("Name", site.getName());
        assertEquals("spider.jar", site.getJar());
        assertEquals(1_000L, site.getTimeout());
    }

    @Test
    public void selectionAndBooleanSettersFollowCurrentFlags() {
        Site site = Site.get("k", "Name");
        site.setSelected(true);
        assertTrue(site.isSelected());
        Site same = Site.get("k", "Other");
        site.setSelected(same);
        assertTrue(site.isSelected());
        site.setSearchable(false);
        site.setChangeable(false);
        assertEquals(2, site.getSearchable().intValue());
        assertEquals(2, site.getChangeable().intValue());
        assertFalse(site.isSearchable());
        assertFalse(site.isChangeable());
    }
}

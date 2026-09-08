package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class SettingTest {

    @Test
    public void wallValueIsClamped() {
        Setting.putWall(-1);
        assertEquals(0, Setting.getWall());
        Setting.putWall(99);
        assertEquals(4, Setting.getWall());
        Setting.putWall(2);
        assertEquals(2, Setting.getWall());
    }

    @Test
    public void wallTypeIsClamped() {
        Setting.putWallType(-1);
        assertEquals(0, Setting.getWallType());
        Setting.putWallType(99);
        assertEquals(2, Setting.getWallType());
    }

    @Test
    public void siteModeIsClamped() {
        Setting.putSiteMode(-1);
        assertEquals(0, Setting.getSiteMode());
        Setting.putSiteMode(99);
        assertEquals(1, Setting.getSiteMode());
    }

    @Test
    public void syncModeIsClamped() {
        Setting.putSyncMode(-1);
        assertEquals(0, Setting.getSyncMode());
        Setting.putSyncMode(99);
        assertEquals(2, Setting.getSyncMode());
    }

    @Test
    public void fontIndexIsClampedAndScaleArrayIsAccessed() {
        Setting.putFont(-1);
        assertEquals(0, Setting.getFont());
        Setting.putFont(99);
        assertEquals(4, Setting.getFont());

        Setting.putFont(0);
        assertEquals(0.9f, Setting.getFontScale(), 0);
        Setting.putFont(1);
        assertEquals(1.0f, Setting.getFontScale(), 0);
        Setting.putFont(2);
        assertEquals(1.1f, Setting.getFontScale(), 0);
        Setting.putFont(3);
        assertEquals(1.25f, Setting.getFontScale(), 0);
        Setting.putFont(4);
        assertEquals(1.45f, Setting.getFontScale(), 0);
    }

    @Test
    public void modeIsClampedAndPredicatesWork() {
        Setting.putMode(-1);
        assertEquals(Setting.MODE_DEFAULT, Setting.getMode());
        Setting.putMode(99);
        assertEquals(Setting.MODE_CHILD, Setting.getMode());

        Setting.putMode(Setting.MODE_DEFAULT);
        assertFalse(Setting.isElderMode());
        assertFalse(Setting.isChildMode());

        Setting.putMode(Setting.MODE_ELDER);
        assertTrue(Setting.isElderMode());
        assertFalse(Setting.isChildMode());

        Setting.putMode(Setting.MODE_CHILD);
        assertFalse(Setting.isElderMode());
        assertTrue(Setting.isChildMode());
    }

    @Test
    public void dynamicColorReturnsZeroForDefaultTheme() {
        Setting.putThemeColor(-1);
        assertEquals(0, Setting.getDynamicColor());
    }

    @Test
    public void dynamicColorReturnsWallColorWhenThemeIsZero() {
        Setting.putThemeColor(0);
        Setting.putWallColor(0xFF00FF00);
        assertEquals(0xFF00FF00, Setting.getDynamicColor());
    }

    @Test
    public void dynamicColorReturnsThemeColorWhenNonZero() {
        Setting.putThemeColor(0xFFFF0000);
        assertEquals(0xFFFF0000, Setting.getDynamicColor());
    }

    @Test
    public void booleanSettingsStoreAndRetrieve() {
        Setting.putIncognito(true);
        assertTrue(Setting.isIncognito());
        Setting.putIncognito(false);
        assertFalse(Setting.isIncognito());

        Setting.putSound(false);
        assertFalse(Setting.isSound());
        Setting.putSound(true);
        assertTrue(Setting.isSound());

        Setting.putUpdate(false);
        assertFalse(Setting.getUpdate());
        Setting.putAdblock(false);
        assertFalse(Setting.isAdblock());
        Setting.putZhuyin(true);
        assertTrue(Setting.isZhuyin());
    }

    @Test
    public void stringSettingsStoreAndRetrieve() {
        Setting.putDoh("https://doh.example.com");
        assertEquals("https://doh.example.com", Setting.getDoh());
        Setting.putUa("TestAgent");
        assertEquals("TestAgent", Setting.getUa());
        Setting.putKeyword("test");
        assertEquals("test", Setting.getKeyword());
        Setting.putHot("hot");
        assertEquals("hot", Setting.getHot());
    }
}

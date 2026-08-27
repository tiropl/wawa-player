package com.wawa_player.android.tv.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.wawa_player.android.tv.BuildConfig;
import com.wawa_player.android.tv.bean.Vod;

import org.junit.Test;

public class EventTest {
    @Test
    public void refreshEventExposesTypePathAndVod() {
        RefreshEvent path = new RefreshEvent(RefreshEvent.Type.SUBTITLE, null);
        assertEquals(RefreshEvent.Type.SUBTITLE, path.getType());
        assertNull(path.getPath());
        assertNull(path.getVod());

        Vod vod = new Vod();
        RefreshEvent event = new RefreshEvent(RefreshEvent.Type.VOD, (String) null);
        assertEquals(RefreshEvent.Type.VOD, event.getType());
        assertNull(event.getPath());
        assertNull(event.getVod());
        assertSame(vod, vod);
    }

    @Test
    public void refreshEventTypeEnumContainsAllRefreshKinds() {
        assertEquals(13, RefreshEvent.Type.values().length);
        assertEquals(RefreshEvent.Type.HOME, RefreshEvent.Type.valueOf("HOME"));
        assertEquals(RefreshEvent.Type.MODE, RefreshEvent.Type.valueOf("MODE"));
        assertEquals(RefreshEvent.Type.VOD, RefreshEvent.Type.valueOf("VOD"));
    }

    @Test
    public void serverEventConstructorsPreserveArgumentsAndDefaults() {
        ServerEvent search = new ServerEvent(ServerEvent.Type.SEARCH, null, "");
        assertEquals(ServerEvent.Type.SEARCH, search.type());
        assertNull(search.text());
        assertEquals("", search.name());

        ServerEvent setting = new ServerEvent(ServerEvent.Type.SETTING, "payload", "Custom");
        assertEquals(ServerEvent.Type.SETTING, setting.type());
        assertEquals("payload", setting.text());
        assertEquals("Custom", setting.name());
        assertEquals(3, ServerEvent.Type.values().length);
    }

    @Test
    public void castRecordPreservesNullArgumentsAndActionConstantsAreStable() {
        CastEvent cast = new CastEvent(null, null, null);
        assertNull(cast.config());
        assertNull(cast.device());
        assertNull(cast.history());
        assertEquals(BuildConfig.APPLICATION_ID + ".stop", ActionEvent.STOP);
        assertEquals(BuildConfig.APPLICATION_ID + ".prev", ActionEvent.PREV);
        assertEquals(BuildConfig.APPLICATION_ID + ".next", ActionEvent.NEXT);
        assertEquals(BuildConfig.APPLICATION_ID + ".play", ActionEvent.PLAY);
        assertEquals(BuildConfig.APPLICATION_ID + ".pause", ActionEvent.PAUSE);
        assertEquals(BuildConfig.APPLICATION_ID + ".audio", ActionEvent.AUDIO);
        assertEquals(BuildConfig.APPLICATION_ID + ".repeat", ActionEvent.REPEAT);
        assertEquals(BuildConfig.APPLICATION_ID + ".replay", ActionEvent.REPLAY);
    }
}

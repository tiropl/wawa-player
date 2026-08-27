package com.wawa_player.android.tv.api.parser;

import static org.junit.Assert.*;

import com.wawa_player.android.tv.bean.Channel;
import com.wawa_player.android.tv.bean.Group;
import com.wawa_player.android.tv.bean.Live;
import org.junit.Test;

public class LiveParserTest {
    @Test public void parsesM3uAttributesAndUrls() {
        Live live = new Live("x", "");
        LiveParser.text(live, "#EXTM3U url-tvg=\"epg.xml\"\n#EXTINF:-1 group-title=\"News\" tvg-id=\"id\" tvg-name=\"N\" tvg-logo=\"logo\" tvg-chno=\"7\",Channel\nhttp://one|User-Agent=UA");
        assertEquals("epg.xml", live.getEpg());
        Channel c = live.getGroups().get(0).getChannel().get(0);
        assertEquals("News", live.getGroups().get(0).getName()); assertEquals("Channel", c.getName());
        assertEquals("id", c.getTvgId()); assertEquals("N", c.getTvgName()); assertEquals("logo", c.getLogo());
        assertEquals("7", c.getNumber()); assertEquals("http://one", c.getUrls().get(0));
    }

    @Test public void parsesTxtGroupsAndMultipleUrls() {
        Live live = new Live("x", "");
        LiveParser.text(live, "Group#genre#\nOne,http://one#http://two\nTwo,http://three");
        assertEquals(1, live.getGroups().size()); assertEquals("Group", live.getGroups().get(0).getName());
        assertEquals(2, live.getGroups().get(0).getChannel().size());
        assertEquals(2, live.getGroups().get(0).getChannel().get(0).getUrls().size());
        assertEquals("001", live.getGroups().get(0).getChannel().get(0).getNumber());
    }

    @Test public void jsonGroupsAreAccepted() {
        Live live = new Live("x", "");
        LiveParser.text(live, "[{\"name\":\"G\",\"channel\":[{\"name\":\"C\",\"urls\":[\"http://x\"]}]}]");
        assertEquals(1, live.getGroups().size()); assertEquals("G", live.getGroups().get(0).getName());
        assertEquals(1, live.getGroups().get(0).getChannel().size());
    }
}

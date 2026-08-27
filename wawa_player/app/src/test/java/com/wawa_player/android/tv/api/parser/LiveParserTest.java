package com.wawa_player.android.tv.api.parser;

import static com.google.common.truth.Truth.assertThat;

import com.wawa_player.android.tv.bean.Channel;
import com.wawa_player.android.tv.bean.Group;
import com.wawa_player.android.tv.bean.Live;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class LiveParserTest {

    @Test
    public void parsesTxtGroupsChannelsAndFallbackUrls() {
        Live live = new Live("Test", "test.txt");
        LiveParser.text(live, "新闻台,#genre#\nCCTV1,http://one.example/live.m3u8#http://two.example/live.m3u8\nCCTV5,http://five.example/live.m3u8");

        assertThat(live.getGroups()).hasSize(1);
        Group group = live.getGroups().get(0);
        assertThat(group.getName()).isEqualTo("新闻台");
        assertThat(group.getChannel()).hasSize(2);
        assertThat(group.getChannel().get(0).getUrls()).containsExactly(
                "http://one.example/live.m3u8", "http://two.example/live.m3u8").inOrder();
        assertThat(group.getChannel().get(0).getNumber()).isEqualTo("001");
        assertThat(group.getChannel().get(1).getNumber()).isEqualTo("002");
    }

    @Test
    public void parsesTxtPersistentInstructionsAndInlineHeaders() {
        Live live = new Live("Test", "test.txt");
        LiveParser.text(live, "体育台,#genre#\nua=TestAgent\nreferer=https://example.com/\nCCTV5,http://example/live.m3u8|Token=abc");

        Channel channel = live.getGroups().get(0).getChannel().get(0);
        assertThat(channel.getUa()).isEqualTo("TestAgent");
        assertThat(channel.getReferer()).isEqualTo("https://example.com/");
        assertThat(channel.getHeader()).containsEntry("Token", "abc");
    }

    @Test
    public void parsesM3uAttributesAndClearsUrlInstructions() {
        Live live = new Live("Test", "test.m3u");
        String text = "#EXTM3U tvg-url=\"https://epg.example/guide.xml\"\n"
                + "#EXTINF:-1 tvg-id=\"CCTV1\" tvg-name=\"CCTV-1\" tvg-chno=\"1\" group-title=\"央视\",CCTV-1\n"
                + "#EXTVLCOPT:http-user-agent=Player\n"
                + "http://one.example/live.m3u8|Referer=https://stream.example/\n"
                + "#EXTINF:-1 group-title=\"央视\",CCTV-5\n"
                + "http://five.example/live.m3u8";
        LiveParser.text(live, text);

        assertThat(live.getEpg()).isEqualTo("https://epg.example/guide.xml");
        assertThat(live.getGroups()).hasSize(1);
        Channel first = live.getGroups().get(0).getChannel().get(0);
        Channel second = live.getGroups().get(0).getChannel().get(1);
        assertThat(first.getTvgId()).isEqualTo("CCTV1");
        assertThat(first.getTvgName()).isEqualTo("CCTV-1");
        assertThat(first.getNumber()).isEqualTo("1");
        assertThat(first.getUa()).isEqualTo("Player");
        assertThat(first.getHeader()).containsEntry("Referer", "https://stream.example/");
        assertThat(second.getUa()).isEmpty();
        assertThat(second.getHeader()).isEmpty();
    }

    @Test
    public void parsesM3uFormatAndCatchupAttributes() {
        Live live = new Live("Test", "test.m3u");
        LiveParser.text(live, "#EXTM3U catchup=\"append\" catchup-source=\"?from={utc}\"\n"
                + "#EXTINF:-1 group-title=\"News\",Channel\n"
                + "format=mpd\n"
                + "http://example/live.mpd");

        Channel channel = live.getGroups().get(0).getChannel().get(0);
        assertThat(channel.getFormat()).isEqualTo("application/dash+xml");
        assertThat(channel.getCatchup().getType()).isEqualTo("append");
        assertThat(channel.getCatchup().getSource()).isEqualTo("?from={utc}");
    }

    @Test
    public void ignoresInvalidTxtLinesAndCreatesDefaultGroup() {
        Live live = new Live("Test", "test.txt");
        LiveParser.text(live, "not a channel\n\nNews,http://example/live.m3u8\n# comment");

        assertThat(live.getGroups()).hasSize(1);
        assertThat(live.getGroups().get(0).getChannel()).hasSize(1);
        assertThat(live.getGroups().get(0).getChannel().get(0).getName()).isEqualTo("News");
    }
}

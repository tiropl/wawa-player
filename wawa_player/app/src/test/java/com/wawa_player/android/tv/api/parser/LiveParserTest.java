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

    @Test
    public void parsesTxtSettingsWithSpacesEqualsAndAllChannelAttributes() {
        Live live = new Live("Test", "test.txt");
        LiveParser.text(live, "News, #genre#\n"
                + "ua=Agent With Spaces\n"
                + "referer=https://ref.example/a=b\n"
                + "parse=1\n"
                + "click=javascript:play now\n"
                + "format=hls\n"
                + "origin=https://origin.example/a=b\n"
                + "header={\"X-Test\":\"value with spaces = here\"}\n"
                + "Channel,https://host/live.m3u8|Header=header value = yes");

        Channel channel = live.getGroups().get(0).getChannel().get(0);
        assertThat(channel.getUa()).isEqualTo("Agent With Spaces");
        assertThat(channel.getReferer()).isEqualTo("https://ref.example/a=b");
        assertThat(channel.getParse()).isEqualTo(1);
        assertThat(channel.getClick()).isEqualTo("javascript:play now");
        assertThat(channel.getFormat()).isEqualTo("application/x-mpegURL");
        assertThat(channel.getOrigin()).isEqualTo("https://origin.example/a=b");
        assertThat(channel.getHeader()).containsEntry("Header", "header value = yes");
    }

    @Test
    public void txtChannelNameCommaAndUrlPipeAreSplitByCurrentRules() {
        Live live = new Live("Test", "test.txt");
        LiveParser.text(live, "News, East,http://host/live.m3u8#http://host/live2.m3u8|Token=a|b");

        Channel channel = live.getGroups().get(0).getChannel().get(0);
        assertThat(channel.getName()).isEqualTo("News");
        assertThat(channel.getUrls()).containsExactly(" East,http://host/live.m3u8", "http://host/live2.m3u8").inOrder();
        assertThat(channel.getHeader()).containsEntry("Token", "a");
    }

    @Test
    public void parsesM3uExtendedSettingsAndDrmBeforeNextUrlOnly() {
        Live live = new Live("Test", "test.m3u");
        LiveParser.text(live, "#EXTM3U\n"
                + "#EXTINF:-1 group-title=\"DRM\",Protected\n"
                + "#EXTVLCOPT:http-user-agent=UA\n"
                + "#EXTVLCOPT:http-referrer=https://ref.example/\n"
                + "#EXTVLCOPT:http-origin=https://origin.example/\n"
                + "#KODIPROP:inputstream.adaptive.license_type=clearkey\n"
                + "#KODIPROP:inputstream.adaptive.license_key=https://license.example/key|Authorization=Bearer token\n"
                + "#KODIPROP:inputstream.adaptive.manifest_type=mpd\n"
                + "https://host/protected.mpd\n"
                + "#EXTINF:-1 group-title=\"DRM\",Plain\n"
                + "https://host/plain.m3u8#fragment");

        Channel protectedChannel = live.getGroups().get(0).getChannel().get(0);
        Channel plain = live.getGroups().get(0).getChannel().get(1);
        assertThat(protectedChannel.getUa()).isEqualTo("UA");
        assertThat(protectedChannel.getReferer()).isEqualTo("https://ref.example/");
        assertThat(protectedChannel.getOrigin()).isEqualTo("https://origin.example/");
        assertThat(protectedChannel.getFormat()).isEqualTo("application/dash+xml");
        assertThat(protectedChannel.getDrm()).isNotNull();
        assertThat(protectedChannel.getDrm().getType()).isEqualTo("clearkey");
        assertThat(protectedChannel.getDrm().getKey()).isEqualTo("https://license.example/key");
        assertThat(protectedChannel.getDrm().getHeader()).containsEntry("Authorization", "Bearer token");
        assertThat(plain.getUa()).isEmpty();
        assertThat(plain.getUrls()).containsExactly("https://host/plain.m3u8#fragment");
    }

    @Test
    public void parsesM3uEpgAliasesCatchupOverridesAndExplicitNumbers() {
        Live live = new Live("Test", "test.m3u");
        LiveParser.text(live, "#EXTM3U url-tvg=\"https://epg.example/guide.xml\" catchup=\"append\" catchup-source=\"?t={utc}\" catchup-replace=\"/live/,/archive/\"\n"
                + "#EXTINF:-1 tvg-chno=\"7\" tvg-logo=\"logo.png\" tvg-id=\"id\" group-title=\"A\",One\n"
                + "https://host/live/one\n"
                + "#EXTINF:-1 group-title=\"B\",Two\n"
                + "https://host/live/two");

        Channel first = live.getGroups().get(0).getChannel().get(0);
        Channel second = live.getGroups().get(1).getChannel().get(0);
        assertThat(live.getEpg()).isEqualTo("https://epg.example/guide.xml");
        assertThat(first.getNumber()).isEqualTo("7");
        assertThat(first.getLogo()).isEqualTo("logo.png");
        assertThat(first.getCatchup().getType()).isEqualTo("append");
        assertThat(first.getCatchup().getSource()).isEqualTo("?t={utc}");
        assertThat(second.getNumber()).isEqualTo("001");
    }

    @Test
    public void emptyAndGroupOnlyTextProduceNoChannels() {
        Live empty = new Live("Test", "test.txt");
        LiveParser.text(empty, "");
        assertThat(empty.getGroups()).isEmpty();

        Live groupOnly = new Live("Test", "test.txt");
        LiveParser.text(groupOnly, "News,#genre#");
        assertThat(groupOnly.getGroups()).hasSize(1);
        assertThat(groupOnly.getGroups().get(0).getChannel()).isEmpty();
    }
}

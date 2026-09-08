package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class LiveModelJsonTest {

    @Test
    public void deserializesLiveGroupsAndChannelFields() {
        String json = "{\"name\":\"News\",\"groups\":[{\"name\":\"TV\",\"channel\":[{"
                + "\"name\":\"CCTV1\",\"urls\":[\"http://example/live.m3u8\"],"
                + "\"number\":\"1\",\"logo\":\"https://example/logo.png\","
                + "\"tvgId\":\"CCTV1\",\"header\":{\"Token\":\"abc\"}}]}]}";

        Live live = new Gson().fromJson(json, Live.class);

        assertThat(live.getName()).isEqualTo("News");
        assertThat(live.getGroups()).hasSize(1);
        Channel channel = live.getGroups().get(0).getChannel().get(0);
        assertThat(channel.getName()).isEqualTo("CCTV1");
        assertThat(channel.getUrls()).containsExactly("http://example/live.m3u8");
        assertThat(channel.getNumber()).isEqualTo("1");
        assertThat(channel.getLogo()).isEqualTo("https://example/logo.png");
        assertThat(channel.getTvgId()).isEqualTo("CCTV1");
        assertThat(channel.getHeader()).containsEntry("Token", "abc");
    }

    @Test
    public void channelDefaultsAndLineSwitchingRemainDeterministic() {
        Channel channel = new Channel("Demo");
        channel.getUrls().add("http://one.example/live.m3u8$One");
        channel.getUrls().add("http://two.example/live.m3u8$Two");

        assertThat(channel.getTvgName()).isEqualTo("Demo");
        assertThat(channel.getTvgId()).isEqualTo("Demo");
        assertThat(channel.getCurrent()).isEqualTo("http://one.example/live.m3u8");
        channel.switchLine(true);
        assertThat(channel.getCurrent()).isEqualTo("http://two.example/live.m3u8");
        assertThat(channel.getLine()).isEqualTo("Two");
    }
}

package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class ChannelTest {

    @Test
    public void numberAndCurrentUsePaddedNumberAndStripLineLabel() {
        Channel channel = Channel.create(7);
        channel.setUrls(List.of("https://one$Main", "https://two"));

        assertThat(channel.getNumber()).isEqualTo("007");
        assertThat(channel.getCurrent()).isEqualTo("https://one");
        assertThat(channel.getLine()).isEqualTo("Main");
        assertThat(channel.isOnly()).isFalse();
    }

    @Test
    public void switchLineWrapsAndSetIndexNeverAcceptsNegative() {
        Channel channel = Channel.create("News");
        channel.setUrls(List.of("one", "two"));
        channel.setIndex(-4);
        channel.switchLine(false);

        assertThat(channel.getIndex()).isEqualTo(1);
        channel.switchLine(true);
        assertThat(channel.getIndex()).isEqualTo(0);
    }

    @Test
    public void setIndexByUrlAndEqualityPreferNameThenNumber() {
        Channel channel = Channel.create("News");
        channel.setUrls(List.of("one$A", "two$B"));
        channel.setIndex("two");

        assertThat(channel.getIndex()).isEqualTo(1);
        assertThat(channel).isEqualTo(Channel.create("News"));
        assertThat(Channel.create(5)).isEqualTo(Channel.create(5));
        assertThat(channel.getHeaders()).isEmpty();
    }
}

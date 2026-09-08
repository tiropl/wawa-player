package com.wawa_player.android.tv.playback.live;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.wawa_player.android.tv.bean.Channel;
import com.wawa_player.android.tv.bean.EpgData;
import com.wawa_player.android.tv.bean.Group;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class LivePlayRequestTest {
    @Test
    public void liveAndCatchupFactoriesExposeTheirState() {
        Channel channel = Channel.create("News");
        EpgData data = new EpgData();
        LivePlayRequest live = LivePlayRequest.live(channel, 12);
        LivePlayRequest catchup = LivePlayRequest.catchup(channel, data, 34);

        assertFalse(live.isCatchup());
        assertTrue(catchup.isCatchup());
        assertSame(data, catchup.getCatchupData());
        assertThrows(IllegalStateException.class, live::getCatchupData);
        assertTrue(live.matches(channel));
        assertTrue(live.matches(LivePlayRequest.live(channel, 12)));
        assertFalse(live.matches((LivePlayRequest) null));
    }

    @Test
    public void matchingIncludesGroupLinePositionAndCatchupData() {
        Group group = new Group("News");
        Channel channel = Channel.create("Channel").group(group);
        channel.setIndex(2);
        EpgData data = new EpgData();
        LivePlayRequest request = LivePlayRequest.catchup(channel, data, 20);

        assertTrue(request.matches(LivePlayRequest.catchup(channel, data, 20)));
        assertFalse(request.matches(LivePlayRequest.catchup(channel, data, 21)));
        assertFalse(request.matches(LivePlayRequest.live(channel, 20)));
        Channel other = Channel.create("Other").group(group);
        other.setIndex(2);
        assertFalse(request.matches(other));
    }
}

package com.wawa_player.android.tv.playback.live;

import static com.google.common.truth.Truth.assertThat;

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
    public void liveRequestCapturesChannelPositionGroupAndLine() {
        Group group = Group.create("News", false);
        Channel channel = Channel.create("CCTV1").group(group);
        channel.setIndex(1);
        LivePlayRequest request = LivePlayRequest.live(channel, 1234L);

        assertThat(request.getChannel()).isSameInstanceAs(channel);
        assertThat(request.getPosition()).isEqualTo(1234L);
        assertThat(request.isCatchup()).isFalse();
        assertThat(request.matches(channel)).isTrue();
    }

    @Test
    public void catchupRequestExposesDataAndMatchesOnlySameRequest() {
        Channel channel = Channel.create("CCTV1");
        EpgData data = new EpgData();
        LivePlayRequest request = LivePlayRequest.catchup(channel, data, 10L);

        assertThat(request.isCatchup()).isTrue();
        assertThat(request.getCatchupData()).isSameInstanceAs(data);
        assertThat(request.matches(LivePlayRequest.catchup(channel, data, 10L))).isTrue();
        assertThat(request.matches(LivePlayRequest.live(channel, 10L))).isFalse();
        assertThat(request.matches((LivePlayRequest) null)).isFalse();
    }

    @Test(expected = IllegalStateException.class)
    public void liveRequestCannotExposeCatchupData() {
        LivePlayRequest.live(Channel.create("CCTV1"), 0L).getCatchupData();
    }
}

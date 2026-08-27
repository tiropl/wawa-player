package com.wawa_player.android.tv.playback.live;

import static org.junit.Assert.*;

import com.wawa_player.android.tv.bean.Channel;
import com.wawa_player.android.tv.bean.Group;
import androidx.media3.common.MediaMetadata;
import org.junit.Test;

public class LivePlaybackStateTest {
    @Test public void pendingAndPlayingRequestsAreExclusive() {
        LivePlaybackState state = new LivePlaybackState(); Channel channel = Channel.create("C");
        LivePlayRequest pending = LivePlayRequest.live(channel, 1);
        state.setPendingRequest(pending);
        assertSame(pending, state.getActiveRequest()); assertNull(state.getPlayingRequest());
        LivePlayRequest playing = LivePlayRequest.live(channel, 2);
        state.setPlayingRequest(playing, "key");
        assertSame(playing, state.getActiveRequest()); assertNull(state.getPendingRequest()); assertEquals("key", state.getPlaybackKey());
    }

    @Test public void resetClearsPlaybackAndChannelContext() {
        LivePlaybackState state = new LivePlaybackState(); Channel channel = Channel.create("C");
        channel.group(new Group("G")); state.setChannel(channel); state.setPlaybackMetadata(new MediaMetadata.Builder().setTitle("x").build());
        state.setPlayingRequest(LivePlayRequest.live(channel, 0), "k"); state.reset();
        assertNull(state.getChannel()); assertNull(state.getGroup()); assertNull(state.getActiveRequest()); assertNull(state.getPlaybackMetadata()); assertNull(state.getPlaybackKey());
    }
}

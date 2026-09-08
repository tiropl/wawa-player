package com.wawa_player.android.tv.playback.live;

import static org.junit.Assert.*;

import com.wawa_player.android.tv.bean.Channel;
import com.wawa_player.android.tv.bean.Group;
import androidx.media3.common.MediaMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
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

    @Test public void pendingTakesPriorityAndRepeatedTransitionsClearPreviousState() {
        LivePlaybackState state = new LivePlaybackState();
        Channel channel = Channel.create("C");
        LivePlayRequest first = LivePlayRequest.live(channel, 1);
        LivePlayRequest second = LivePlayRequest.live(channel, 2);

        state.setPlayingRequest(first, "first");
        state.setPendingRequest(second);
        assertSame(second, state.getActiveRequest());
        assertNull(state.getPlayingRequest());
        assertNull(state.getPlaybackKey());

        state.setPlayingRequest(first, "second");
        assertSame(first, state.getActiveRequest());
        assertNull(state.getPendingRequest());
        assertEquals("second", state.getPlaybackKey());
        state.clearPendingRequest();
        assertSame(first, state.getActiveRequest());
    }

    @Test public void channelAutomaticallyAssociatesItsGroupButNullKeepsExistingGroup() {
        LivePlaybackState state = new LivePlaybackState();
        Group group = new Group("News");
        Channel channel = Channel.create("C").group(group);
        state.setChannel(channel);
        assertSame(channel, state.getChannel());
        assertSame(group, state.getGroup());

        state.setGroup(new Group("Manual"));
        state.setChannel(null);
        assertEquals("Manual", state.getGroup().getName());
    }

    @Test public void metadataAndPlaybackKeyCanBeUpdatedAndCleared() {
        LivePlaybackState state = new LivePlaybackState();
        MediaMetadata first = new MediaMetadata.Builder().setTitle("first").build();
        MediaMetadata second = new MediaMetadata.Builder().setTitle("second").build();
        state.setPlaybackMetadata(first);
        assertSame(first, state.getPlaybackMetadata());
        state.setPlaybackMetadata(second);
        assertSame(second, state.getPlaybackMetadata());
        state.clearPlayback();
        assertNull(state.getPlaybackMetadata());
        assertNull(state.getPlaybackKey());
        assertNull(state.getActiveRequest());
    }
}

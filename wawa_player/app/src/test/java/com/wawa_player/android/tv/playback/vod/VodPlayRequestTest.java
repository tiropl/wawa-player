package com.wawa_player.android.tv.playback.vod;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.wawa_player.android.tv.bean.Episode;
import com.wawa_player.android.tv.bean.Flag;
import com.wawa_player.android.tv.bean.Result;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class VodPlayRequestTest {
    @Test
    public void matchesSameRequestAndRejectsDifferentParts() {
        Flag flag = Flag.create("source", "episode-url");
        Episode episode = flag.getEpisodes().get(0);
        VodPlayRequest request = VodPlayRequest.create("key", flag, episode);

        assertTrue(request.matches("key", flag, episode));
        assertTrue(request.matches(VodPlayRequest.create("key", flag, episode)));
        assertFalse(request.matches((VodPlayRequest) null));
        assertFalse(request.matches("other", flag, episode));
        assertFalse(request.matches("key", Flag.create("other", "episode-url"), episode));
        assertFalse(request.matches("key", flag, Episode.create("other", "other-url")));
        assertFalse(request.matches("key", null, episode));
        assertFalse(request.matches("key", flag, null));
    }

    @Test
    public void acceptsEmptyOrMatchingResultFlagOnly() {
        Flag flag = Flag.create("source", "url");
        VodPlayRequest request = VodPlayRequest.create("key", flag, flag.getEpisodes().get(0));

        assertFalse(request.accepts(null));
        assertTrue(request.accepts(Result.empty()));
        Result same = Result.empty();
        same.setFlag("source");
        assertTrue(request.accepts(same));
        Result other = Result.empty();
        other.setFlag("other");
        assertFalse(request.accepts(other));
    }
}

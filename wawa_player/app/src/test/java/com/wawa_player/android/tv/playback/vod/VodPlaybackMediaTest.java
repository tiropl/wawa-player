package com.wawa_player.android.tv.playback.vod;

import static org.junit.Assert.*;

import com.wawa_player.android.tv.bean.Episode;
import com.wawa_player.android.tv.bean.History;
import androidx.media3.common.MediaMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class VodPlaybackMediaTest {
    @Test public void metadataUsesTitleAndEpisodeName() {
        History history = new History(); history.setVodName("Series"); history.setVodPic("https://img/cover.jpg");
        MediaMetadata metadata = VodPlaybackMedia.metadata(history, Episode.create("Episode 1", "url"));
        assertEquals("Series", metadata.title); assertEquals("Episode 1", metadata.artist);
        assertEquals("Series : Episode 1", metadata.displayTitle);
    }

    @Test public void duplicateEpisodeTitleIsOmitted() {
        History history = new History(); history.setVodName("Series");
        MediaMetadata metadata = VodPlaybackMedia.metadata(history, Episode.create("Series", "url"));
        assertEquals("Series", metadata.title); assertEquals("", metadata.artist); assertEquals("Series", metadata.displayTitle);
    }

    @Test public void emptyHistoryAndEpisodeNamesProduceMetadataDefaults() {
        History history = new History();
        MediaMetadata metadata = VodPlaybackMedia.metadata(history, new Episode());
        assertEquals("", metadata.title);
        assertEquals("", metadata.artist);
        assertEquals("", metadata.displayTitle);
    }

    @Test(expected = NullPointerException.class)
    public void missingEpisodeIsRejectedByMetadataBuilder() {
        VodPlaybackMedia.metadata(new History(), null);
    }
}

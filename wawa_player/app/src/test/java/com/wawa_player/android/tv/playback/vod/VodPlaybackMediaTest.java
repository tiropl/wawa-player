package com.wawa_player.android.tv.playback.vod;

import static org.junit.Assert.*;

import com.wawa_player.android.tv.bean.Episode;
import com.wawa_player.android.tv.bean.History;
import androidx.media3.common.MediaMetadata;
import org.junit.Test;

public class VodPlaybackMediaTest {
    @Test public void metadataUsesTitleAndEpisodeName() {
        History history = new History(); history.setVodName("Series"); history.setVodPic("https://img/cover.jpg");
        MediaMetadata metadata = VodPlaybackMedia.metadata(history, Episode.create("Episode 1", "url"));
        assertEquals("Series", metadata.title); assertEquals("Episode 1", metadata.artist);
        assertEquals("Series - Episode 1", metadata.displayTitle);
    }

    @Test public void duplicateEpisodeTitleIsOmitted() {
        History history = new History(); history.setVodName("Series");
        MediaMetadata metadata = VodPlaybackMedia.metadata(history, Episode.create("Series", "url"));
        assertEquals("Series", metadata.title); assertEquals("", metadata.artist); assertEquals("Series", metadata.displayTitle);
    }
}

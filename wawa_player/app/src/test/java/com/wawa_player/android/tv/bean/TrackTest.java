package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class TrackTest {

    @Test
    public void constructorAndAccessorsPreserveTrackState() {
        Track track = new Track(2, "English", "text/vtt");
        track.setId(7);
        track.key("episode-key");
        track.setSelected(true);

        assertThat(track.getId()).isEqualTo(7);
        assertThat(track.getType()).isEqualTo(2);
        assertThat(track.getName()).isEqualTo("English");
        assertThat(track.getFormat()).isEqualTo("text/vtt");
        assertThat(track.getKey()).isEqualTo("episode-key");
        assertThat(track.isSelected()).isTrue();
        assertThat(track.toggle()).isSameInstanceAs(track);
        assertThat(track.isSelected()).isFalse();
    }
}

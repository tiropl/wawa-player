package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class EpisodeTest {

    @Test
    public void createExtractsEpisodeNumberAndDefaultsDescription() {
        Episode episode = Episode.create("Episode 12", "https://example/12");

        assertThat(episode.getName()).isEqualTo("Episode 12");
        assertThat(episode.getNumber()).isEqualTo(12);
        assertThat(episode.getDesc()).isEmpty();
        assertThat(episode.getUrl()).isEqualTo("https://example/12");
    }

    @Test
    public void scorePrefersExactNumberAndNameMatches() {
        Episode episode = Episode.create("Episode 12", "url");

        assertThat(episode.getScore("Episode 12", -1)).isEqualTo(100);
        assertThat(episode.getScore("anything", 12)).isEqualTo(80);
        Episode unnumbered = Episode.create("Pilot", "url");
        assertThat(unnumbered.getScore("Long Pilot Story", -1)).isEqualTo(60);
        assertThat(episode.getScore("Episode", -1)).isEqualTo(70);
        assertThat(episode.getScore("Other", -1)).isEqualTo(0);
    }

    @Test
    public void matchesNameIsCaseInsensitiveAndEqualityIncludesUrl() {
        Episode first = Episode.create("Episode 1", "one");
        Episode sameName = Episode.create("episode 1", "one");
        Episode differentUrl = Episode.create("Episode 1", "two");

        assertThat(first.matchesName(sameName)).isTrue();
        assertThat(first.matchesName(null)).isFalse();
        assertThat(first).isNotEqualTo(sameName);
        assertThat(first).isNotEqualTo(differentUrl);
        assertThat(first.hashCode()).isNotEqualTo(differentUrl.hashCode());
    }

    @Test
    public void deselectClearsSelectedState() {
        Episode episode = Episode.create("Episode 1", "one");
        episode.setSelected(true);

        episode.deselect();

        assertThat(episode.isSelected()).isFalse();
    }
}

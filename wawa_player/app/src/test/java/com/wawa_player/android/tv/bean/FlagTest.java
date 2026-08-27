package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class FlagTest {

    @Test
    public void splitsUrlsIntoNumberedEpisodesAndPreservesLabels() {
        Flag flag = Flag.create("source", "one$Pilot#two$Final#three");

        assertThat(flag.getEpisodes()).hasSize(3);
        assertThat(flag.getEpisodes().get(0).getName()).isEqualTo("one");
        assertThat(flag.getEpisodes().get(0).getUrl()).isEqualTo("Pilot");
        assertThat(flag.getEpisodes().get(1).getName()).isEqualTo("two");
        assertThat(flag.getEpisodes().get(1).getUrl()).isEqualTo("Final");
        assertThat(flag.getEpisodes().get(2).getName()).isEqualTo("03");
        assertThat(flag.getEpisodes().get(2).getUrl()).isEqualTo("three");
    }

    @Test
    public void duplicateEpisodesAreIgnoredAndMergeCanPrepend() {
        Flag flag = Flag.create("source", "01$url1#02$url2");
        flag.setEpisodes("01$url1");
        flag.mergeEpisodes(List.of(Episode.create("03", "url3")), false);
        flag.mergeEpisodes(List.of(Episode.create("00", "url0")), true);

        assertThat(flag.getEpisodes().get(0).getName()).isEqualTo("00");
        assertThat(flag.getEpisodes().get(1).getName()).isEqualTo("01");
        assertThat(flag.getEpisodes().get(2).getName()).isEqualTo("02");
        assertThat(flag.getEpisodes().get(3).getName()).isEqualTo("03");
    }

    @Test
    public void findUsesPositionAndStrictFallback() {
        Flag flag = Flag.create("source", "01$url1#02$url2");
        flag.setPosition(1);

        assertThat(flag.find("unrelated", true).getName()).isEqualTo("02");
        assertThat(flag.find("unrelated", false).getName()).isEqualTo("02");
        assertThat(flag.find("Episode 01", true).getName()).isEqualTo("01");
    }
}

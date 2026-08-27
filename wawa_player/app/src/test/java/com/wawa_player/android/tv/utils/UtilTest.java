package com.wawa_player.android.tv.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class UtilTest {

    @Test
    public void extractsEpisodeNumbersFromCommonNames() {
        assertThat(Util.getNumber("第12集")).isEqualTo(12);
        assertThat(Util.getNumber("S01E03")).isEqualTo(3);
        assertThat(Util.getNumber("Episode 7")).isEqualTo(7);
        assertThat(Util.getNumber("无集数标题")).isEqualTo(-1);
    }

    @Test
    public void removesYearAndQualityBeforeFallbackNumberExtraction() {
        assertThat(Util.getNumber("Movie 2024 1080p")).isEqualTo(-1);
        assertThat(Util.getNumber("Part 2 1080p")).isEqualTo(2);
        assertThat(Util.getNumber(null)).isEqualTo(-1);
    }

    @Test
    public void substringRemovesRequestedTrailingCharacters() {
        assertThat(Util.substring("abc")).isEqualTo("ab");
        assertThat(Util.substring("abc", 2)).isEqualTo("a");
        assertThat(Util.substring("a", 2)).isEqualTo("a");
        assertThat(Util.substring(null)).isNull();
    }

    @Test
    public void cleanConvertsBasicHtmlAndNormalizesLines() {
        assertThat(Util.clean("plain text")).isEqualTo("plain text");
        assertThat(Util.clean("<b>Title</b><br> Description")).isEqualTo("Title\nDescription");
    }
}

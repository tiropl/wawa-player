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
        assertThat(Util.clean("<p>A&nbsp;B</p><p>Ｃ　Ｄ</p>")).isEqualTo("A B\n\nＣ Ｄ");
    }

    @Test
    public void formatsTimesAndHandlesInvalidSubstrings() {
        assertThat(Util.timeMs(0)).isEqualTo("00:00");
        assertThat(Util.timeMs(-1)).isEqualTo("-00:00");
        assertThat(Util.timeMs(65_000)).isEqualTo("01:05");
        assertThat(Util.substring("a", 2)).isEqualTo("a");
        assertThat(Util.substring(null, 2)).isNull();
    }

    @Test
    public void recognizesSpecialEpisodeNameFormats() {
        assertThat(Util.getNumber("S02E11")).isEqualTo(11);
        assertThat(Util.getNumber("第3话")).isEqualTo(3);
        assertThat(Util.getNumber("Movie 2024 4K")).isEqualTo(-1);
        assertThat(Util.getNumber("Part 0002")).isEqualTo(2);
        assertThat(Util.getNumber("")).isEqualTo(-1);
    }
}

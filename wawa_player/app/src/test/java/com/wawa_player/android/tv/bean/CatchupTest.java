package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class CatchupTest {

    @Test
    public void decidePrefersNonEmptyMajorThenMinor() {
        Catchup major = Catchup.create();
        Catchup minor = Catchup.create();
        minor.setType("append");
        minor.setSource("?playseek=${utc}");

        assertThat(Catchup.decide(major, minor)).isSameInstanceAs(minor);

        major.setSource("?major");
        assertThat(Catchup.decide(major, minor)).isSameInstanceAs(major);
        assertThat(Catchup.decide(Catchup.create(), Catchup.create())).isNull();
    }

    @Test
    public void formatUsesUnixSecondTokensAndAppendsToUrl() {
        Catchup catchup = Catchup.create();
        catchup.setSource("?start=${utc:ignored}&end=${utcend:ignored}");
        EpgData data = data(1_700_000_000_000L, 1_700_000_060_000L);

        assertThat(catchup.format("http://example/live.m3u8", data))
                .isEqualTo("http://example/live.m3u8?start=1700000000&end=1700000060");
    }

    @Test
    public void formatChangesQuestionMarkWhenUrlAlreadyHasQuery() {
        Catchup catchup = Catchup.create();
        catchup.setSource("?start=${(b)timestamp}");
        EpgData data = data(1_700_000_000_000L, 1_700_000_060_000L);

        assertThat(catchup.format("http://example/live.m3u8?token=abc", data))
                .isEqualTo("http://example/live.m3u8?token=abc&start=1700000000");
    }

    @Test
    public void defaultTypeReturnsFormattedTemplateWithoutOriginalUrl() {
        Catchup catchup = Catchup.create();
        catchup.setType("default");
        catchup.setSource("http://timeshift.example/${utc:ignored}-${utcend:ignored}");
        EpgData data = data(1_700_000_000_000L, 1_700_000_060_000L);

        assertThat(catchup.format("http://example/live.m3u8", data))
                .isEqualTo("http://timeshift.example/1700000000-1700000060");
    }

    @Test
    public void formatAppliesReplacementBeforeAppending() {
        Catchup catchup = Catchup.create();
        catchup.setReplace("/PLTV/,/TVOD/");
        catchup.setSource("?playseek=${utc:ignored}-${utcend:ignored}");
        EpgData data = data(1_700_000_000_000L, 1_700_000_060_000L);

        assertThat(catchup.format("http://example/PLTV/live.m3u8", data))
                .isEqualTo("http://example/TVOD/live.m3u8?playseek=1700000000-1700000060");
    }

    @Test
    public void matchSupportsLiteralAndRegularExpressionRules() {
        Catchup literal = Catchup.create();
        literal.setRegex("/PLTV/");
        assertThat(literal.match("http://example/PLTV/live.m3u8")).isTrue();
        assertThat(literal.match("http://example/live.m3u8")).isFalse();

        Catchup regex = Catchup.create();
        regex.setRegex("/live\\.m3u8$");
        assertThat(regex.match("http://example/live.m3u8")).isTrue();
    }

    @Test
    public void pltvFactoryProvidesDocumentedDefaults() {
        Catchup catchup = Catchup.PLTV();

        assertThat(catchup.getDays()).isEqualTo("7");
        assertThat(catchup.getType()).isEqualTo("append");
        assertThat(catchup.getRegex()).isEqualTo("/PLTV/");
        assertThat(catchup.getReplace()).isEqualTo("/PLTV/,/TVOD/");
        assertThat(catchup.getSource()).contains("${(b)yyyyMMddHHmmss}");
    }

    private static EpgData data(long start, long end) {
        EpgData data = new EpgData();
        data.setStartTime(start);
        data.setEndTime(end);
        return data;
    }
}

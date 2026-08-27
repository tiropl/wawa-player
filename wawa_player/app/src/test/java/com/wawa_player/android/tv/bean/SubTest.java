package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import androidx.media3.common.C;
import androidx.media3.common.MimeTypes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class SubTest {

    @Test
    public void createsSubtitleFromPathAndInfersNameAndFormat() {
        Sub sub = Sub.from("https://example/subtitle.vtt?token=abc");

        assertThat(sub.getUrl()).isEqualTo("https://example/subtitle.vtt?token=abc");
        assertThat(sub.getName()).isEqualTo("subtitle.vtt");
        assertThat(sub.getFormat()).isEqualTo(MimeTypes.TEXT_VTT);
        assertThat(sub.getFlag()).isEqualTo(C.SELECTION_FLAG_FORCED);
        assertThat(sub.isForced()).isTrue();
        assertThat(sub.getUri().toString()).isEqualTo("https://example/subtitle.vtt?token=abc");
    }

    @Test
    public void defaultFlagIsReturnedForUnforcedSubtitle() {
        Sub sub = Sub.from("English", "https://example/subtitle.srt", "en", MimeTypes.APPLICATION_SUBRIP);

        assertThat(sub.getName()).isEqualTo("English");
        assertThat(sub.getLang()).isEqualTo("en");
        assertThat(sub.getFormat()).isEqualTo(MimeTypes.APPLICATION_SUBRIP);
        assertThat(sub.getFlag()).isEqualTo(C.SELECTION_FLAG_DEFAULT);
        assertThat(sub.isForced()).isFalse();
        assertThat(sub).isEqualTo(Sub.from("Other", "https://example/subtitle.srt", "zh", MimeTypes.TEXT_VTT));
    }

    @Test
    public void emptySubtitleHasNoUri() {
        Sub sub = Sub.from("", "", "", "");

        assertThat(sub.isEmpty()).isTrue();
        assertThat(sub.getUri()).isNull();
        assertThat(sub.getUrl()).isEmpty();
    }
}

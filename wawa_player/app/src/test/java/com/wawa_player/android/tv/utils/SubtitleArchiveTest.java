package com.wawa_player.android.tv.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class SubtitleArchiveTest {

    @Test
    public void recognizesZipNamesAndUrlsWithoutQueryOrFragment() {
        assertThat(SubtitleArchive.isZip("captions.zip", "")).isTrue();
        assertThat(SubtitleArchive.isZip("captions", "https://example/captions.zip?token=abc#part")).isTrue();
        assertThat(SubtitleArchive.isZip("captions.ZIP", "")).isTrue();
        assertThat(SubtitleArchive.isZip("captions.tar", "https://example/captions.tar")).isFalse();
    }

    @Test
    public void recognizesSupportedSubtitleExtensionsCaseInsensitively() {
        assertThat(SubtitleArchive.isSupported("movie.SRT", "")).isTrue();
        assertThat(SubtitleArchive.isSupported("movie", "https://example/subtitle.vtt?lang=zh")).isTrue();
        assertThat(SubtitleArchive.isSupported("movie", "https://example/subtitle.ass#track")).isTrue();
        assertThat(SubtitleArchive.isSupported("movie.mp4", "https://example/video.mp4")).isFalse();
        assertThat(SubtitleArchive.isSupported("", "")).isFalse();
    }
}

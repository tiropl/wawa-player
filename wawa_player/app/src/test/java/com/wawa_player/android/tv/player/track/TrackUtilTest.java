package com.wawa_player.android.tv.player.track;

import static com.google.common.truth.Truth.assertThat;

import androidx.media3.common.Format;
import androidx.media3.common.MimeTypes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class TrackUtilTest {

    @Test
    public void describesOnlySetFormatFieldsInStableOrder() {
        Format format = new Format.Builder()
                .setId("id")
                .setLabel("English")
                .setCodecs("avc1")
                .setLanguage("en")
                .setSampleMimeType(MimeTypes.VIDEO_H264)
                .setContainerMimeType(MimeTypes.VIDEO_MP4)
                .setWidth(1920)
                .setHeight(1080)
                .setSampleRate(48000)
                .setChannelCount(2)
                .setAverageBitrate(500000)
                .build();

        assertThat(TrackUtil.describeFormat(format)).isEqualTo(
                "id,English,avc1,en,video/avc,video/mp4,1920,1080,48000,2,500000");
    }

    @Test
    public void mapsSubtitleExtensionsAndUnknowns() {
        assertThat(TrackUtil.getSubtitleMimeType(null)).isEmpty();
        assertThat(TrackUtil.getSubtitleMimeType("caption.VTT")).isEqualTo(MimeTypes.TEXT_VTT);
        assertThat(TrackUtil.getSubtitleMimeType("caption.ass")).isEqualTo(MimeTypes.TEXT_SSA);
        assertThat(TrackUtil.getSubtitleMimeType("caption.ttml")).isEqualTo(MimeTypes.APPLICATION_TTML);
        assertThat(TrackUtil.getSubtitleMimeType("caption.srt")).isEqualTo(MimeTypes.APPLICATION_SUBRIP);
    }
}

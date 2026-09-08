package com.wawa_player.android.tv.player.media;

import static com.google.common.truth.Truth.assertThat;

import android.text.TextUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class MediaItemFactoryTest {

    @Test
    public void formatDisplayTitle_bothEmpty_returnsEmpty() {
        assertThat(MediaItemFactory.formatDisplayTitle(null, null)).isEqualTo("");
        assertThat(MediaItemFactory.formatDisplayTitle("", "")).isEqualTo("");
        assertThat(MediaItemFactory.formatDisplayTitle(null, "")).isEqualTo("");
        assertThat(MediaItemFactory.formatDisplayTitle("", null)).isEqualTo("");
    }

    @Test
    public void formatDisplayTitle_onlyTitle_returnsTitle() {
        assertThat(MediaItemFactory.formatDisplayTitle("Movie", null)).isEqualTo("Movie");
        assertThat(MediaItemFactory.formatDisplayTitle("Movie", "")).isEqualTo("Movie");
    }

    @Test
    public void formatDisplayTitle_onlyName_returnsName() {
        assertThat(MediaItemFactory.formatDisplayTitle(null, "Episode 1")).isEqualTo("Episode 1");
        assertThat(MediaItemFactory.formatDisplayTitle("", "Episode 1")).isEqualTo("Episode 1");
    }

    @Test
    public void formatDisplayTitle_sameTitleAndName_returnsTitle() {
        assertThat(MediaItemFactory.formatDisplayTitle("Same", "Same")).isEqualTo("Same");
    }

    @Test
    public void formatDisplayTitle_differentTitleAndName_returnsFormattedString() {
        // When both are non-empty and different, it calls ResUtil.getString(R.string.detail_title, title, name)
        // With Robolectric, the string resource should be available
        String result = MediaItemFactory.formatDisplayTitle("Movie", "Episode 1");
        // The result depends on the string resource format; it should contain both values
        assertThat(result).isNotEmpty();
        assertThat(result).contains("Movie");
        assertThat(result).contains("Episode 1");
    }
}

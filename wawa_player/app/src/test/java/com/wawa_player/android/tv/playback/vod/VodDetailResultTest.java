package com.wawa_player.android.tv.playback.vod;

import static com.google.common.truth.Truth.assertThat;

import com.wawa_player.android.tv.bean.Result;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class VodDetailResultTest {

    @Test
    public void recordAccessorsAndMatchesAreNullSafe() {
        Result result = Result.empty();
        VodDetailResult detail = new VodDetailResult("key", "id", result);

        assertThat(detail.key()).isEqualTo("key");
        assertThat(detail.id()).isEqualTo("id");
        assertThat(detail.result()).isSameInstanceAs(result);
        assertThat(detail.matches("key", "id")).isTrue();
        assertThat(detail.matches("other", "id")).isFalse();
        assertThat(new VodDetailResult(null, null, null).matches(null, null)).isTrue();
    }
}

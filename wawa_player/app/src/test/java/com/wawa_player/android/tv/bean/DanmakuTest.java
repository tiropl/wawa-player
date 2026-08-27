package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class DanmakuTest {

    @Test
    public void parsesSingleAndArrayForms() {
        Danmaku single = Danmaku.from("https://example/danmaku.xml");
        assertThat(single.getUrl()).isEqualTo("https://example/danmaku.xml");
        assertThat(single.getName()).isEqualTo("https://example/danmaku.xml");
        assertThat(single.isEmpty()).isFalse();

        assertThat(Danmaku.arrayFrom("[{\"name\":\"A\",\"url\":\"https://example/a\"}]")).hasSize(1);
    }

    @Test
    public void emptyDanmakuUsesSafeDefaultsAndComparesByUrl() {
        Danmaku empty = Danmaku.from("");
        assertThat(empty.isEmpty()).isTrue();
        assertThat(empty.getUrl()).isEmpty();
        assertThat(empty.getName()).isEmpty();
        assertThat(Danmaku.from("same")).isEqualTo(Danmaku.from("same"));
    }
}

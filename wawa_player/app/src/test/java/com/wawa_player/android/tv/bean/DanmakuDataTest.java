package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class DanmakuDataTest {

    @Test
    public void parsesTimingTypeSizeColorAndEntities() throws Exception {
        Matcher matcher = Pattern.compile("\\[(.*?)](.*)").matcher("[12.5,1,30,16711680]hello &amp; &quot;world&quot;");
        assertThat(matcher.matches()).isTrue();

        DanmakuData data = new DanmakuData(matcher, 2f);

        assertThat(data.getTime()).isEqualTo(12500L);
        assertThat(data.getType()).isEqualTo(1);
        assertThat(data.getSize()).isEqualTo(42f);
        assertThat(data.getColor()).isEqualTo(0xFFFF0000);
        assertThat(data.getShadow()).isEqualTo(android.graphics.Color.BLACK);
        assertThat(data.getText()).isEqualTo("hello & \"world\"");
    }

    @Test(expected = Exception.class)
    public void rejectsIncompleteParameters() throws Exception {
        Matcher matcher = Pattern.compile("\\[(.*?)](.*)").matcher("[1,2,3]text");
        assertThat(matcher.matches()).isTrue();
        new DanmakuData(matcher, 1f);
    }
}

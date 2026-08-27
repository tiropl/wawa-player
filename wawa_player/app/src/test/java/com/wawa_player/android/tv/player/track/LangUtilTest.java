package com.wawa_player.android.tv.player.track;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Test;

public class LangUtilTest {

    @Test
    public void prefersExactAndPrimaryLanguageForEnglish() {
        Locale old = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);

            assertThat(Arrays.asList(LangUtil.getPreferredTextLanguages()))
                    .containsExactly("en-US", "en").inOrder();
            assertThat(LangUtil.getPreferredTextLanguageScore("en-US")).isEqualTo(400);
            assertThat(LangUtil.getPreferredTextLanguageScore("en-GB")).isEqualTo(200);
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-CN")).isEqualTo(0);
        } finally {
            Locale.setDefault(old);
        }
    }

    @Test
    public void prefersTraditionalChineseScriptForTaiwan() {
        Locale old = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("zh-TW"));

            assertThat(Arrays.asList(LangUtil.getPreferredTextLanguages()))
                    .containsExactly("zh-TW", "zh-Hant", "zh").inOrder();
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-TW")).isEqualTo(400);
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-Hant")).isEqualTo(300);
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-Hans")).isEqualTo(100);
            assertThat(LangUtil.getPreferredTextLanguageScore("en")).isEqualTo(0);
        } finally {
            Locale.setDefault(old);
        }
    }

    @Test
    public void prefersSimplifiedChineseScriptForChina() {
        Locale old = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("zh-CN"));

            assertThat(Arrays.asList(LangUtil.getPreferredTextLanguages()))
                    .containsExactly("zh-CN", "zh-Hans", "zh").inOrder();
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-CN")).isEqualTo(400);
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-Hans")).isEqualTo(300);
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-Hant")).isEqualTo(100);
        } finally {
            Locale.setDefault(old);
        }
    }
}

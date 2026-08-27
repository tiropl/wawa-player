package com.wawa_player.android.tv.player.track;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Test;

public class LangUtilTest {
    @Test
    public void scoresLanguageTagsAndNormalizesUnderscores() {
        Locale old = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            assertThat(Arrays.asList(LangUtil.getPreferredTextLanguages())).containsExactly("en-US", "en").inOrder();
            assertThat(LangUtil.getPreferredTextLanguageScore("en-US")).isEqualTo(400);
            assertThat(LangUtil.getPreferredTextLanguageScore("en_US")).isEqualTo(400);
            assertThat(LangUtil.getPreferredTextLanguageScore("en-GB")).isEqualTo(200);
            assertThat(LangUtil.getPreferredTextLanguageScore("fr")).isEqualTo(0);
            assertThat(LangUtil.getPreferredTextLanguageScore(null)).isEqualTo(0);
            assertThat(LangUtil.getPreferredTextLanguageScore("")).isEqualTo(0);
        } finally {
            Locale.setDefault(old);
        }
    }

    @Test
    public void distinguishesTraditionalAndSimplifiedChinese() {
        Locale old = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("zh-TW"));
            assertThat(Arrays.asList(LangUtil.getPreferredTextLanguages())).containsExactly("zh-TW", "zh-Hant", "zh").inOrder();
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-Hant")).isEqualTo(300);
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-Hans")).isEqualTo(100);
            Locale.setDefault(Locale.forLanguageTag("zh-CN"));
            assertThat(Arrays.asList(LangUtil.getPreferredTextLanguages())).containsExactly("zh-CN", "zh-Hans", "zh").inOrder();
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-Hans")).isEqualTo(300);
            assertThat(LangUtil.getPreferredTextLanguageScore("zh-Hant")).isEqualTo(100);
        } finally {
            Locale.setDefault(old);
        }
    }
}

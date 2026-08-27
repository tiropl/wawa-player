package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class WordTest {

    @Test
    public void parsesTitleAndAlternateNameFields() {
        Word word = Word.objectFrom("{\"data\":[{\"title\":\"First\"},{\"name\":\"Second\"}]}");

        assertThat(word.getData()).hasSize(2);
        assertThat(word.getData().get(0).getTitle()).isEqualTo("First");
        assertThat(word.getData().get(1).getTitle()).isEqualTo("Second");
    }

    @Test
    public void malformedJsonReturnsEmptyWord() {
        assertThat(Word.objectFrom("{bad json").getData()).isEmpty();
        assertThat(Word.objectFrom("null").getData()).isEmpty();
    }
}

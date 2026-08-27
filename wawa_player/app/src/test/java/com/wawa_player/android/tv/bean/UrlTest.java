package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class UrlTest {

    @Test
    public void addsValuesAndSwitchesBetweenNamedLines() {
        Url url = Url.create().add("Primary", "https://one.example/live").add("Backup", "https://two.example/live");

        assertThat(url.isMulti()).isTrue();
        assertThat(url.v()).isEqualTo("https://one.example/live");
        assertThat(url.n(0)).isEqualTo("Primary");
        assertThat(url.v(1)).isEqualTo("https://two.example/live");

        url.set(1);
        assertThat(url.getPosition()).isEqualTo(1);
        assertThat(url.v()).isEqualTo("https://two.example/live");
        assertThat(url.isEmpty()).isFalse();
    }

    @Test
    public void replaceUpdatesCurrentValueAndOutOfRangeReadsAreEmpty() {
        Url url = Url.create().add("https://one.example/live").add("https://two.example/live");
        url.set(1).replace("https://replacement.example/live");

        assertThat(url.v()).isEqualTo("https://replacement.example/live");
        assertThat(url.v(99)).isEmpty();
        assertThat(url.n(99)).isEmpty();
    }

    @Test
    public void emptyUrlHasNoValues() {
        Url url = Url.create();

        assertThat(url.getValues()).isEmpty();
        assertThat(url.isEmpty()).isTrue();
        assertThat(url.isMulti()).isFalse();
    }
}

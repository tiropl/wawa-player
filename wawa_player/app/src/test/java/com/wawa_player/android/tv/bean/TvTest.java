package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.simpleframework.xml.core.Persister;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class TvTest {

    @Test
    public void xmlMapsChannelsProgrammesAndTitles() throws Exception {
        String xml = "<tv date=\"2024-01-01\">"
                + "<channel id=\"news\"><display-name>News</display-name><icon src=\"logo.png\"/></channel>"
                + "<programme start=\"20240101T080000 +0000\" stop=\"20240101T090000 +0000\" channel=\"news\"><title></title><title>Morning</title></programme>"
                + "</tv>";

        Tv tv = new Persister().read(Tv.class, xml);

        assertThat(tv.getDate()).isEqualTo("2024-01-01");
        assertThat(tv.getChannel()).hasSize(1);
        assertThat(tv.getChannel().get(0).getId()).isEqualTo("news");
        assertThat(tv.getChannel().get(0).getSrc()).isEqualTo("logo.png");
        assertThat(tv.getChannel().get(0).hasSrc()).isTrue();
        assertThat(tv.getChannel().get(0).getDisplayName().get(0).getText()).isEqualTo("News");
        assertThat(tv.getProgramme()).hasSize(1);
        assertThat(tv.getProgramme().get(0).getStart()).isEqualTo("20240101T080000 +0000");
        assertThat(tv.getProgramme().get(0).getStop()).isEqualTo("20240101T090000 +0000");
        assertThat(tv.getProgramme().get(0).getChannel()).isEqualTo("news");
        assertThat(tv.getProgramme().get(0).getTitle()).isEqualTo("Morning");
    }

    @Test
    public void missingFieldsUseEmptyCollectionsAndStrings() {
        Tv tv = new Tv();
        assertThat(tv.getDate()).isEmpty();
        assertThat(tv.getChannel()).isEmpty();
        assertThat(tv.getProgramme()).isEmpty();
        assertThat(new Tv.Channel().hasSrc()).isFalse();
        assertThat(new Tv.Programme().getTitle()).isEmpty();
        assertThat(new Tv.Title().getText()).isEmpty();
    }
}

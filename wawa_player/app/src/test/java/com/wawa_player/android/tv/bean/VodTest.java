package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class VodTest {

    @Test
    public void objectFromMapsFieldsAndSetFlagsSplitsSourcesAndEpisodes() {
        Vod vod = Vod.objectFrom("{\"vod_id\":\" id \",\"vod_name\":\" <b>Movie</b> \",\"vod_play_from\":\"A$$$B\",\"vod_play_url\":\"One$url1#Two$url2$$$Only$url3\"}");

        assertThat(vod.getId()).isEqualTo("id");
        assertThat(vod.getName()).isEqualTo("Movie");
        assertThat(vod.getFlags()).hasSize(2);
        assertThat(vod.getFlags().get(0).getName()).isEqualTo("A");
        assertThat(vod.getFlags().get(0).getEpisodes()).hasSize(2);
        assertThat(vod.getFlags().get(1).getName()).isEqualTo("B");
    }

    @Test
    public void visibilityAndFolderStateReflectMetadata() {
        Vod vod = Vod.objectFrom("{\"vod_name\":\"Movie\",\"vod_year\":\"2024\",\"vod_tag\":\"folder\"}");

        assertThat(vod.isFolder()).isTrue();
        assertThat(vod.isAction()).isFalse();
        assertThat(vod.getNameVisible()).isEqualTo(android.view.View.VISIBLE);
        assertThat(vod.getYearVisible()).isEqualTo(android.view.View.VISIBLE);
        assertThat(vod.getRemarkVisible()).isEqualTo(android.view.View.GONE);
    }

    @Test
    public void equalityUsesIdWhenAvailableAndNameOtherwise() {
        Vod first = Vod.objectFrom("{\"vod_id\":\"1\",\"vod_name\":\"One\"}");
        Vod sameId = Vod.objectFrom("{\"vod_id\":\"1\",\"vod_name\":\"Different\"}");
        Vod sameName = Vod.objectFrom("{\"vod_name\":\"One\"}");
        Vod otherName = Vod.objectFrom("{\"vod_name\":\"Other\"}");

        assertThat(first).isEqualTo(sameId);
        assertThat(first.isSameItem(sameId)).isTrue();
        assertThat(first).isEqualTo(sameName);
        assertThat(first).isNotEqualTo(otherName);
        assertThat(new Vod().getId()).isEmpty();
    }
}

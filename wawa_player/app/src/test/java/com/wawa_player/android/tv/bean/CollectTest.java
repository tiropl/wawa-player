package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class CollectTest {

    @Test
    public void createUsesFirstVodSiteAndPageIsAtLeastOne() {
        Site site = Site.objectFrom(JsonParser.parseString("{\"key\":\"site\",\"name\":\"Site\"}"), "");
        Vod vod = Vod.objectFrom("{\"vod_id\":\"id\",\"vod_name\":\"Movie\"}");
        vod.setSite(site);
        Collect collect = Collect.create(new ArrayList<>(List.of(vod)));
        collect.setPage(0);

        assertThat(collect.getSite()).isSameInstanceAs(site);
        assertThat(collect.getList()).containsExactly(vod);
        assertThat(collect.getPage()).isEqualTo(1);
        assertThat(collect.isSelected()).isFalse();
    }

    @Test
    public void nullFieldsReturnSafeValues() {
        Collect collect = new Collect(null, null);
        assertThat(collect.getSite()).isNotNull();
        assertThat(collect.getList()).isEmpty();
        collect.setSelected(true);
        assertThat(collect.isSelected()).isTrue();
    }
}

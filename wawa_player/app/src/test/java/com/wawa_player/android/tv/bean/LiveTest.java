package com.wawa_player.android.tv.bean;

import com.google.common.truth.Truth;
import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.Constant;
import com.google.gson.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.time.ZoneId;

@RunWith(RobolectricTestRunner.class)
@Config(application = App.class, sdk = 36)
public class LiveTest {

    /** 通过 Gson 反序列化设置 @Ignore 字段 */
    private Live fromJson(String json) {
        return App.gson().fromJson(json, Live.class);
    }

    @Test
    public void constructor_empty() {
        Live live = new Live();
        Truth.assertThat(live.getName()).isEmpty();
        Truth.assertThat(live.getUrl()).isEmpty();
        Truth.assertThat(live.getApi()).isEmpty();
        Truth.assertThat(live.getExt()).isEmpty();
        Truth.assertThat(live.getJar()).isEmpty();
        Truth.assertThat(live.getEpg()).isEmpty();
        Truth.assertThat(live.getUa()).isEmpty();
        Truth.assertThat(live.getOrigin()).isEmpty();
        Truth.assertThat(live.getReferer()).isEmpty();
        Truth.assertThat(live.getTimeZone()).isEmpty();
    }

    @Test
    public void constructor_with_name_url() {
        Live live = new Live("TestLive", "http://live.url");
        Truth.assertThat(live.getName()).isEqualTo("TestLive");
        Truth.assertThat(live.getUrl()).isEqualTo("http://live.url");
    }

    @Test
    public void timeout_default() {
        Truth.assertThat(new Live().getTimeout()).isEqualTo(Constant.TIMEOUT_PLAY);
    }

    @Test
    public void getZoneId_empty_uses_system() {
        Truth.assertThat(new Live().getZoneId()).isEqualTo(ZoneId.systemDefault());
    }

    @Test
    public void getZoneId_valid() {
        Live live = fromJson("{\"timeZone\":\"Asia/Tokyo\"}");
        Truth.assertThat(live.getZoneId()).isEqualTo(ZoneId.of("Asia/Tokyo"));
    }

    @Test
    public void getZoneId_invalid_falls_back() {
        Live live = fromJson("{\"timeZone\":\"Invalid/Zone\"}");
        Truth.assertThat(live.getZoneId()).isEqualTo(ZoneId.systemDefault());
    }

    @Test
    public void isEmpty() {
        Truth.assertThat(new Live().isEmpty()).isTrue();
        Truth.assertThat(new Live("name", "url").isEmpty()).isFalse();
    }

    @Test
    public void equals_by_name() {
        Live a = new Live("Channel1", "url1");
        Live b = new Live("Channel1", "url2");
        Truth.assertThat(a).isEqualTo(b);

        Live c = new Live("Channel2", "url1");
        Truth.assertThat(a).isNotEqualTo(c);
    }

    @Test
    public void equals_same_instance() {
        Live live = new Live("x", "y");
        Truth.assertThat(live).isEqualTo(live);
    }

    @Test
    public void equals_non_live() {
        Truth.assertThat(new Live("x", "y").equals("not live")).isFalse();
    }

    @Test
    public void boot_pass_chaining() {
        Live live = new Live("x", "y");
        Live returned = live.boot(true);
        Truth.assertThat(returned).isSameInstanceAs(live);
        Truth.assertThat(live.isBoot()).isTrue();

        live.pass(true);
        Truth.assertThat(live.isPass()).isTrue();
        Truth.assertThat(live.getGroups()).isEmpty();
    }

    @Test
    public void selected_boolean() {
        Live live = new Live("x", "y");
        Truth.assertThat(live.isSelected()).isFalse();
        live.setSelected(true);
        Truth.assertThat(live.isSelected()).isTrue();
    }

    @Test
    public void selected_live_item() {
        Live a = new Live("Channel1", "url1");
        Live b = new Live("Channel1", "url2");
        Live c = new Live("Channel2", "url3");

        a.setSelected(b);
        Truth.assertThat(a.isSelected()).isTrue();

        a.setSelected(c);
        Truth.assertThat(a.isSelected()).isFalse();
    }

    @Test
    public void getHeader_default_empty() {
        Truth.assertThat(new Live().getHeader()).isEmpty();
    }

    @Test
    public void getHeaders_merges_ua_origin_referer() {
        Live live = fromJson("{\"ua\":\"CustomUA\",\"origin\":\"http://origin\",\"referer\":\"http://referer\"}");
        Truth.assertThat(live.getHeaders().get("User-Agent")).isEqualTo("CustomUA");
        Truth.assertThat(live.getHeaders().get("Origin")).isEqualTo("http://origin");
        Truth.assertThat(live.getHeaders().get("Referer")).isEqualTo("http://referer");
    }

    @Test
    public void getHeaders_empty_ua_not_added() {
        Truth.assertThat(new Live().getHeaders()).doesNotContainKey("User-Agent");
    }

    @Test
    public void getCatchup_getCore_getGroups_default() {
        Live live = new Live();
        Truth.assertThat(live.getCatchup()).isNotNull();
        Truth.assertThat(live.getCore()).isNotNull();
        Truth.assertThat(live.getGroups()).isEmpty();
    }

    @Test
    public void getGroups_mutable() {
        Live live = new Live();
        live.getGroups().add(new Group("Sports"));
        Truth.assertThat(live.getGroups()).hasSize(1);
    }

    @Test
    public void getEpgApi_single() {
        Live live = new Live();
        live.setEpg("http://epg.com/xml");
        Truth.assertThat(live.getEpgApi()).isEqualTo("http://epg.com/xml");
    }

    @Test
    public void getEpgApi_with_template() {
        Live live = new Live();
        live.setEpg("http://epg.com/xml,http://epg2.com/api?id={name}");
        Truth.assertThat(live.getEpgApi()).isEqualTo("http://epg2.com/api?id={name}");
    }

    @Test
    public void getEpgXml_filters() {
        Live live = new Live();
        live.setEpg("http://epg.com/xml,http://other.com/api,http://epg.com/xml.gz");
        Truth.assertThat(live.getEpgXml()).hasSize(2);
    }

    @Test
    public void getEpgXml_no_match() {
        Live live = new Live();
        live.setEpg("http://epg.com/api");
        Truth.assertThat(live.getEpgXml()).isEmpty();
    }

    @Test
    public void sync_from_null() {
        Live live = new Live("x", "y");
        Live result = live.sync((Live) null);
        Truth.assertThat(result).isSameInstanceAs(live);
    }

    @Test
    public void sync_from_live() {
        Live live = new Live("x", "y");
        Live other = new Live("x", "other");
        other.setBoot(true);
        other.setPass(true);
        other.setKeep("group#channel#url");
        live.sync(other);
        Truth.assertThat(live.isBoot()).isTrue();
        Truth.assertThat(live.isPass()).isTrue();
        Truth.assertThat(live.getKeep()).isEqualTo("group#channel#url");
    }

    @Test
    public void width() {
        Live live = new Live();
        Truth.assertThat(live.getWidth()).isEqualTo(0);
        live.setWidth(1920);
        Truth.assertThat(live.getWidth()).isEqualTo(1920);
    }

    @Test
    public void setExt_trims() {
        Live live = new Live();
        live.setExt("  http://example.com  ");
        Truth.assertThat(live.getExt()).isEqualTo("http://example.com");
    }

    @Test
    public void find_group_existing() {
        Live live = new Live("x", "y");
        Group g = new Group("Sports");
        live.getGroups().add(g);

        Group lookup = new Group("Sports");
        Group found = live.find(lookup);
        Truth.assertThat(found).isSameInstanceAs(g);
    }

    @Test
    public void find_group_new() {
        Live live = new Live("x", "y");
        Group lookup = new Group("Movies");
        Group found = live.find(lookup);
        Truth.assertThat(found).isSameInstanceAs(lookup);
        Truth.assertThat(live.getGroups()).contains(lookup);
    }
}

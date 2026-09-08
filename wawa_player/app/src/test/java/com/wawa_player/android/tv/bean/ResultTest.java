package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class ResultTest {

    @Test
    public void parsesJsonAndProvidesDefaultValues() {
        Result result = Result.fromJson("{\"code\":0,\"msg\":\"ok\",\"pagecount\":3,\"list\":[]}");

        assertThat(result.getCode()).isEqualTo(0);
        assertThat(result.getMsg()).isEqualTo("ok");
        assertThat(result.getPageCount()).isEqualTo(3);
        assertThat(result.getList()).isEmpty();
        assertThat(result.getTypes()).isEmpty();
        assertThat(result.getHeader()).isEmpty();
    }

    @Test
    public void suppressesMessageWhenResultCodeIsError() {
        Result result = Result.fromJson("{\"code\":1,\"msg\":\"failed\"}");

        assertThat(result.getCode()).isEqualTo(1);
        assertThat(result.getMsg()).isEmpty();
        assertThat(result.hasMsg()).isFalse();
    }

    @Test
    public void malformedJsonAndXmlReturnEmptyResults() {
        assertThat(Result.fromJson("{bad json").getList()).isEmpty();
        assertThat(Result.fromXml("<rss><list>").getList()).isEmpty();
        assertThat(Result.fromType(0, "{bad json").getList()).isEmpty();
    }

    @Test
    public void errorAndListFactoriesExposeExpectedContent() {
        Result error = Result.error("failed");
        assertThat(error.getParse()).isEqualTo(0);
        assertThat(error.getMsg()).isEqualTo("failed");

        Vod vod = new Vod();
        Result list = Result.vod(vod);
        assertThat(list.getList()).containsExactly(vod);
        assertThat(list.getVod()).isSameInstanceAs(vod);
    }

    @Test
    public void settersKeepFirstNonEmptyHeaderAndSubs() {
        Result result = Result.empty();
        Map<String, String> first = new HashMap<>();
        first.put("Token", "one");
        Map<String, String> second = new HashMap<>();
        second.put("Token", "two");

        result.setHeader(first);
        result.setHeader(second);
        assertThat(result.getHeader()).containsEntry("Token", "one");
    }

    @Test
    public void clearRemovesListAndKeepsResultObjectUsable() {
        Result result = Result.list(new java.util.ArrayList<>(java.util.List.of(new Vod())));
        assertThat(result.getList()).hasSize(1);

        assertThat(result.clear()).isSameInstanceAs(result);
        assertThat(result.getList()).isEmpty();
        assertThat(result.getVod()).isNotNull();
    }
}

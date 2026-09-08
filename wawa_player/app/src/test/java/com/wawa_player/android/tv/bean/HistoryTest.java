package com.wawa_player.android.tv.bean;

import androidx.media3.common.C;

import com.google.common.truth.Truth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class HistoryTest {

    @Test
    public void constructor_defaults() {
        History h = new History();
        Truth.assertThat(h.getSpeed()).isWithin(0.001f).of(1f);
        Truth.assertThat(h.getScale()).isEqualTo(-1);
        Truth.assertThat(h.getOpening()).isEqualTo(C.TIME_UNSET);
        Truth.assertThat(h.getEnding()).isEqualTo(C.TIME_UNSET);
        Truth.assertThat(h.getPosition()).isEqualTo(C.TIME_UNSET);
        Truth.assertThat(h.getDuration()).isEqualTo(C.TIME_UNSET);
        Truth.assertThat(h.getCid()).isEqualTo(0);
        Truth.assertThat(h.isRevSort()).isFalse();
        Truth.assertThat(h.isRevPlay()).isFalse();
    }

    @Test
    public void getters_null_returns_empty() {
        History h = new History();
        Truth.assertThat(h.getVodRemarks()).isEmpty();
        Truth.assertThat(h.getEpisodeUrl()).isEmpty();
    }

    @Test
    public void getters_nonNull_returns_value() {
        History h = new History();
        h.setVodRemarks("EP01");
        h.setEpisodeUrl("http://example.com/ep1");
        Truth.assertThat(h.getVodRemarks()).isEqualTo("EP01");
        Truth.assertThat(h.getEpisodeUrl()).isEqualTo("http://example.com/ep1");
    }

    @Test
    public void canSave() {
        History h = new History();
        Truth.assertThat(h.canSave()).isFalse();

        h.setPosition(0);
        h.setDuration(60000);
        Truth.assertThat(h.canSave()).isTrue();

        h.setDuration(0);
        Truth.assertThat(h.canSave()).isFalse();

        h.setPosition(-1);
        h.setDuration(60000);
        Truth.assertThat(h.canSave()).isFalse();
    }

    @Test
    public void equals_by_key() {
        History a = new History();
        a.setKey("site#123#1");
        History b = new History();
        b.setKey("site#123#1");
        Truth.assertThat(a).isEqualTo(b);

        History c = new History();
        c.setKey("site#456#1");
        Truth.assertThat(a).isNotEqualTo(c);
    }

    @Test
    public void equals_non_history() {
        History h = new History();
        h.setKey("key");
        Truth.assertThat(h.equals("not a history")).isFalse();
    }

    @Test
    public void hashCode_by_key() {
        History a = new History();
        a.setKey("site#123#1");
        History b = new History();
        b.setKey("site#123#1");
        Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void copy_copies_all_fields() {
        History h = new History();
        h.setKey("site#123#1");
        h.setVodName("Test");
        h.setVodPic("pic.jpg");
        h.setVodFlag("flag");
        h.setVodRemarks("EP01");
        h.setEpisodeUrl("http://ep1");
        h.setRevSort(true);
        h.setRevPlay(true);
        h.setCreateTime(1000L);
        h.setOpening(5000L);
        h.setEnding(10000L);
        h.setPosition(30000L);
        h.setDuration(60000L);
        h.setSpeed(1.5f);
        h.setScale(2);
        h.setCid(7);

        History copy = h.copy();
        Truth.assertThat(copy.getKey()).isEqualTo("site#123#1");
        Truth.assertThat(copy.getVodName()).isEqualTo("Test");
        Truth.assertThat(copy.getVodPic()).isEqualTo("pic.jpg");
        Truth.assertThat(copy.getVodFlag()).isEqualTo("flag");
        Truth.assertThat(copy.getVodRemarks()).isEqualTo("EP01");
        Truth.assertThat(copy.getEpisodeUrl()).isEqualTo("http://ep1");
        Truth.assertThat(copy.isRevSort()).isTrue();
        Truth.assertThat(copy.isRevPlay()).isTrue();
        Truth.assertThat(copy.getCreateTime()).isEqualTo(1000L);
        Truth.assertThat(copy.getOpening()).isEqualTo(5000L);
        Truth.assertThat(copy.getEnding()).isEqualTo(10000L);
        Truth.assertThat(copy.getPosition()).isEqualTo(30000L);
        Truth.assertThat(copy.getDuration()).isEqualTo(60000L);
        Truth.assertThat(copy.getSpeed()).isWithin(0.001f).of(1.5f);
        Truth.assertThat(copy.getScale()).isEqualTo(2);
        Truth.assertThat(copy.getCid()).isEqualTo(7);
    }

    @Test
    public void copy_is_independent() {
        History h = new History();
        h.setKey("key");
        h.setVodName("original");
        History copy = h.copy();
        h.setVodName("modified");
        Truth.assertThat(copy.getVodName()).isEqualTo("original");
    }

    @Test
    public void isSameItem_by_key() {
        History a = new History();
        a.setKey("k1");
        History b = new History();
        b.setKey("k1");
        Truth.assertThat(a.isSameItem(b)).isTrue();

        History c = new History();
        c.setKey("k2");
        Truth.assertThat(a.isSameItem(c)).isFalse();
    }

    @Test
    public void isSameContent() {
        History a = new History();
        a.setVodName("Movie");
        a.setVodPic("pic.jpg");
        a.setCreateTime(100L);
        History b = new History();
        b.setVodName("Movie");
        b.setVodPic("pic.jpg");
        b.setCreateTime(100L);
        Truth.assertThat(a.isSameContent(b)).isTrue();

        b.setVodName("Other");
        Truth.assertThat(a.isSameContent(b)).isFalse();
    }

    @Test
    public void cid_chaining() {
        History h = new History();
        h.setKey("site@@@vod@@@0");
        h.cid(5);
        Truth.assertThat(h.getCid()).isEqualTo(5);
        Truth.assertThat(h.getKey()).contains("5");
    }

    @Test
    public void getSiteKey_and_getVodId() {
        History h = new History();
        h.setKey("mysite@@@12345@@@3");
        Truth.assertThat(h.getSiteKey()).isEqualTo("mysite");
        Truth.assertThat(h.getVodId()).isEqualTo("12345");
    }

    @Test
    public void toString_json() {
        History h = new History();
        h.setKey("k");
        String json = h.toString();
        Truth.assertThat(json).contains("\"key\"");
        Truth.assertThat(json).contains("\"k\"");
    }

    @Test
    public void markSaveScheduled_canScheduleSave() {
        History h = new History();
        Truth.assertThat(h.canScheduleSave()).isTrue();
        h.markSaveScheduled();
        Truth.assertThat(h.canScheduleSave()).isFalse();
    }
}

package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class KeepTest {

    @Test
    public void arrayFromMapsFieldsAndKeyParts() {
        Keep keep = Keep.arrayFrom("[{\"key\":\"site@@@id\",\"siteName\":\"Site\",\"vodName\":\"Movie\",\"vodPic\":\"pic\",\"createTime\":12,\"type\":1,\"cid\":2}]").get(0);

        assertThat(keep.getKey()).isEqualTo("site@@@id");
        assertThat(keep.getSiteKey()).isEqualTo("site");
        assertThat(keep.getVodId()).isEqualTo("id");
        assertThat(keep.getSiteName()).isEqualTo("Site");
        assertThat(keep.getVodName()).isEqualTo("Movie");
        assertThat(keep.getCreateTime()).isEqualTo(12L);
        assertThat(keep.getType()).isEqualTo(1);
        assertThat(keep.getCid()).isEqualTo(2);
    }

    @Test
    public void equalityAndContentUseDifferentFields() {
        Keep first = Keep.arrayFrom("[{\"key\":\"site@@@id\",\"vodName\":\"Movie\",\"vodPic\":\"pic\",\"createTime\":12}]").get(0);
        Keep sameKey = Keep.arrayFrom("[{\"key\":\"site@@@id\",\"vodName\":\"Other\"}]").get(0);
        Keep otherKey = Keep.arrayFrom("[{\"key\":\"site@@@other\",\"vodName\":\"Movie\",\"vodPic\":\"pic\",\"createTime\":12}]").get(0);

        assertThat(first).isEqualTo(sameKey);
        assertThat(first.isSameItem(sameKey)).isTrue();
        assertThat(first).isNotEqualTo(otherKey);
        assertThat(first.isSameContent(otherKey)).isTrue();
    }
}

package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class CateTest {

    @Test
    public void nullFieldsUseZeroDefaults() {
        Parcel parcel = Parcel.obtain();
        parcel.writeValue(null);
        parcel.writeValue(null);
        parcel.writeValue(null);
        parcel.setDataPosition(0);
        Cate cate = Cate.CREATOR.createFromParcel(parcel);
        parcel.recycle();

        assertThat(cate.getLand()).isEqualTo(0);
        assertThat(cate.getCircle()).isEqualTo(0);
        assertThat(cate.getRatio()).isEqualTo(0f);
    }
}

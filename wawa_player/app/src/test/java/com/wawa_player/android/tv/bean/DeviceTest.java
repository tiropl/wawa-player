package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class DeviceTest {

    @Test
    public void accessorsPredicatesHostAndIdentityUseDeviceFields() {
        Device device = Device.objectFrom("{\"uuid\":\"u\",\"name\":\"Living Room\",\"ip\":\"http://192.168.1.2:9978\",\"type\":1}");

        assertThat(device.getUuid()).isEqualTo("u");
        assertThat(device.getName()).isEqualTo("Living Room");
        assertThat(device.getIp()).isEqualTo("http://192.168.1.2:9978");
        assertThat(device.getHost()).isEqualTo("192.168.1.2");
        assertThat(device.isMobile()).isTrue();
        assertThat(device.isApp()).isTrue();
        assertThat(device.isLeanback()).isFalse();
        assertThat(device.isDLNA()).isFalse();

        Device same = Device.objectFrom("{\"uuid\":\"u\",\"name\":\"Other\",\"type\":0}");
        assertThat(device).isEqualTo(same);
        assertThat(device.isSameItem(same)).isTrue();
        assertThat(device.isSameContent(same)).isFalse();
    }

    @Test
    public void defaultsAreEmptyAndTypeCanBeChanged() {
        Device device = new Device();
        assertThat(device.getUuid()).isEmpty();
        assertThat(device.getName()).isEmpty();
        assertThat(device.getIp()).isEmpty();
        device.setType(2);
        device.setUuid("dlna-id");
        assertThat(device.isDLNA()).isTrue();
        assertThat(device.isApp()).isFalse();
        assertThat(device.getHost()).isEqualTo("dlna-id");
    }
}

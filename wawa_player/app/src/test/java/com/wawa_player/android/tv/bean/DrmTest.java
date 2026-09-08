package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import androidx.media3.common.C;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class DrmTest {

    @Test
    public void mapsSupportedDrmTypesToMedia3Uuids() {
        assertThat(Drm.create("key", "widevine", Map.of(), false).getUUID()).isEqualTo(C.WIDEVINE_UUID);
        assertThat(Drm.create("key", "playready", Map.of(), false).getUUID()).isEqualTo(C.PLAYREADY_UUID);
        assertThat(Drm.create("key", "clearkey", Map.of(), true).getUUID()).isEqualTo(C.CLEARKEY_UUID);
    }

    @Test
    public void unknownTypeUsesNilUuidAndPreservesFields() {
        Drm drm = Drm.create("license", "unknown", Map.of("Authorization", "Bearer token"), true);

        assertThat(drm.getUUID()).isEqualTo(C.UUID_NIL);
        assertThat(drm.getKey()).isEqualTo("license");
        assertThat(drm.getType()).isEqualTo("unknown");
        assertThat(drm.isForceKey()).isTrue();
        assertThat(drm.getHeader()).containsEntry("Authorization", "Bearer token");
    }
}

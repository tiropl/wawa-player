package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.media3.common.DolbyVisionOutputPolicy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class DecodeSettingTest {

    @Test
    public void audioPassThroughDefaultIsTrue() {
        assertTrue(DecodeSetting.isAudioPassThrough());
        DecodeSetting.putAudioPassThrough(false);
        assertFalse(DecodeSetting.isAudioPassThrough());
    }

    @Test
    public void audioPreferDefaultIsFalse() {
        assertFalse(DecodeSetting.isAudioPrefer());
        DecodeSetting.putAudioPrefer(true);
        assertTrue(DecodeSetting.isAudioPrefer());
    }

    @Test
    public void videoPreferDefaultIsFalse() {
        assertFalse(DecodeSetting.isVideoPrefer());
        DecodeSetting.putVideoPrefer(true);
        assertTrue(DecodeSetting.isVideoPrefer());
    }

    @Test
    public void preferAACDefaultIsFalse() {
        assertFalse(DecodeSetting.isPreferAAC());
        DecodeSetting.putPreferAAC(true);
        assertTrue(DecodeSetting.isPreferAAC());
    }

    @Test
    public void dolbyVisionOutputPolicyDefaultsToAuto() {
        assertEquals(DolbyVisionOutputPolicy.AUTO, DecodeSetting.getDolbyVisionOutputPolicy());
    }

    @Test
    public void dolbyVisionOutputPolicyClampsInvalidValues() {
        DecodeSetting.putDolbyVisionOutputPolicy(-1);
        assertEquals(DolbyVisionOutputPolicy.AUTO, DecodeSetting.getDolbyVisionOutputPolicy());

        DecodeSetting.putDolbyVisionOutputPolicy(DolbyVisionOutputPolicy.ASSUME_UNSUPPORTED);
        assertEquals(DolbyVisionOutputPolicy.ASSUME_UNSUPPORTED, DecodeSetting.getDolbyVisionOutputPolicy());
    }

    @Test
    public void tunnelDefaultIsFalse() {
        assertFalse(DecodeSetting.isTunnel());
        DecodeSetting.putTunnel(true);
        assertTrue(DecodeSetting.isTunnel());
    }

    @Test
    public void tunnelingEnabledRequiresTunnelAndSurfaceRender() {
        DecodeSetting.putTunnel(false);
        assertFalse(DecodeSetting.isTunnelingEnabled());

        DecodeSetting.putTunnel(true);
        // putTunnel sets render to SURFACE when engine is exo
        assertTrue(DecodeSetting.isTunnelingEnabled());

        PlayerSetting.putRender(PlayerSetting.RENDER_TEXTURE);
        assertFalse(DecodeSetting.isTunnelingEnabled());
    }
}

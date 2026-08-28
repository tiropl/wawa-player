package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.wawa_player.android.tv.player.effect.video.VideoEffectPreset;
import com.wawa_player.android.tv.player.effect.video.VideoEffectProfile;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class VideoSettingTest {

    @Test
    public void saturationIsClamped() {
        VideoSetting.putSaturation(0.0f);
        assertEquals(VideoSetting.MIN_SATURATION, VideoSetting.getSaturation(), 0);
        VideoSetting.putSaturation(99.0f);
        assertEquals(VideoSetting.MAX_SATURATION, VideoSetting.getSaturation(), 0);
        VideoSetting.putSaturation(1.5f);
        assertEquals(1.5f, VideoSetting.getSaturation(), 0);
    }

    @Test
    public void contrastIsClamped() {
        VideoSetting.putContrast(0.0f);
        assertEquals(VideoSetting.MIN_CONTRAST, VideoSetting.getContrast(), 0);
        VideoSetting.putContrast(99.0f);
        assertEquals(VideoSetting.MAX_CONTRAST, VideoSetting.getContrast(), 0);
    }

    @Test
    public void brightnessIsClamped() {
        VideoSetting.putBrightness(-1.0f);
        assertEquals(VideoSetting.MIN_BRIGHTNESS, VideoSetting.getBrightness(), 0);
        VideoSetting.putBrightness(1.0f);
        assertEquals(VideoSetting.MAX_BRIGHTNESS, VideoSetting.getBrightness(), 0);
    }

    @Test
    public void sharpnessIsClamped() {
        VideoSetting.putSharpness(-1.0f);
        assertEquals(VideoSetting.MIN_SHARPNESS, VideoSetting.getSharpness(), 0);
        VideoSetting.putSharpness(1.0f);
        assertEquals(VideoSetting.MAX_SHARPNESS, VideoSetting.getSharpness(), 0);
    }

    @Test
    public void shadowIsClamped() {
        VideoSetting.putShadow(-1.0f);
        assertEquals(VideoSetting.MIN_SHADOW, VideoSetting.getShadow(), 0);
        VideoSetting.putShadow(1.0f);
        assertEquals(VideoSetting.MAX_SHADOW, VideoSetting.getShadow(), 0);
    }

    @Test
    public void gammaIsClamped() {
        VideoSetting.putGamma(0.0f);
        assertEquals(VideoSetting.MIN_GAMMA, VideoSetting.getGamma(), 0);
        VideoSetting.putGamma(99.0f);
        assertEquals(VideoSetting.MAX_GAMMA, VideoSetting.getGamma(), 0);
    }

    @Test
    public void hueIsClamped() {
        VideoSetting.putHue(-999.0f);
        assertEquals(VideoSetting.MIN_HUE, VideoSetting.getHue(), 0);
        VideoSetting.putHue(999.0f);
        assertEquals(VideoSetting.MAX_HUE, VideoSetting.getHue(), 0);
    }

    @Test
    public void temperatureIsClamped() {
        VideoSetting.putTemperature(-999.0f);
        assertEquals(VideoSetting.MIN_TEMPERATURE, VideoSetting.getTemperature(), 0);
        VideoSetting.putTemperature(999.0f);
        assertEquals(VideoSetting.MAX_TEMPERATURE, VideoSetting.getTemperature(), 0);
    }

    @Test
    public void putPresetSetsEnabledFlag() {
        VideoSetting.putPreset(VideoEffectPreset.OFF);
        assertFalse(VideoSetting.isEnabled());

        VideoSetting.putPreset(VideoEffectPreset.NATURAL);
        assertTrue(VideoSetting.isEnabled());
        assertEquals(VideoEffectPreset.NATURAL, VideoSetting.getPreset());
    }

    @Test
    public void putPresetClampsValue() {
        // putPreset clamps to OFF but doesn't store the preset when OFF,
        // so getPreset returns the default (NATURAL). The enabled flag is the observable effect.
        VideoSetting.putPreset(-1);
        assertFalse(VideoSetting.isEnabled());
    }

    @Test
    public void appliedProfileReturnsOffWhenDisabled() {
        VideoSetting.putPreset(VideoEffectPreset.OFF);
        VideoEffectProfile profile = VideoSetting.getAppliedProfile();
        VideoEffectProfile off = VideoEffectProfile.off();
        assertEquals(off.getSaturation(), profile.getSaturation(), 0);
        assertEquals(off.getContrast(), profile.getContrast(), 0);
    }

    @Test
    public void appliedProfileReturnsPresetWhenEnabled() {
        VideoSetting.putPreset(VideoEffectPreset.NATURAL);
        VideoEffectProfile profile = VideoSetting.getAppliedProfile();
        VideoEffectProfile expected = VideoEffectProfile.of(VideoEffectPreset.NATURAL);
        assertEquals(expected.getSaturation(), profile.getSaturation(), 0);
        assertEquals(expected.getContrast(), profile.getContrast(), 0);
    }

    @Test
    public void customProfileReadsStoredValues() {
        VideoSetting.putPreset(VideoEffectPreset.CUSTOM);
        VideoSetting.putSaturation(1.5f);
        VideoSetting.putContrast(1.2f);
        VideoSetting.putBrightness(0.1f);
        VideoSetting.putSharpness(0.3f);
        VideoSetting.putShadow(0.2f);
        VideoSetting.putGamma(1.3f);
        VideoSetting.putHue(10.0f);
        VideoSetting.putTemperature(20.0f);

        VideoEffectProfile profile = VideoSetting.getCustomProfile();
        assertEquals(1.5f, profile.getSaturation(), 0);
        assertEquals(1.2f, profile.getContrast(), 0);
        assertEquals(0.1f, profile.getBrightness(), 0);
        assertEquals(0.3f, profile.getSharpness(), 0);
        assertEquals(0.2f, profile.getShadowLift(), 0);
        assertEquals(1.3f, profile.getGamma(), 0);
        assertEquals(10.0f, profile.getHue(), 0);
        assertEquals(20.0f, profile.getTemperature(), 0);
    }

    @Test
    public void putCustomProfileStoresAllValues() {
        VideoEffectProfile profile = VideoEffectProfile.custom(1.5f, 1.2f, 0.1f, 0.3f, 0.2f, 1.3f, 10.0f, 20.0f);
        VideoSetting.putCustomProfile(profile);

        assertEquals(1.5f, VideoSetting.getSaturation(), 0);
        assertEquals(1.2f, VideoSetting.getContrast(), 0);
        assertEquals(0.1f, VideoSetting.getBrightness(), 0);
        assertEquals(0.3f, VideoSetting.getSharpness(), 0);
        assertEquals(0.2f, VideoSetting.getShadow(), 0);
        assertEquals(1.3f, VideoSetting.getGamma(), 0);
        assertEquals(10.0f, VideoSetting.getHue(), 0);
        assertEquals(20.0f, VideoSetting.getTemperature(), 0);
    }

    @Test
    public void resetClearsState() {
        VideoSetting.putPreset(VideoEffectPreset.VIVID);
        VideoSetting.putSaturation(2.0f);
        VideoSetting.reset();

        assertFalse(VideoSetting.isEnabled());
        assertEquals(VideoEffectPreset.NATURAL, VideoSetting.getPreset());
    }
}

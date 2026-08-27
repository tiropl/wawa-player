package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class ModePolicyTest {
    @Test public void regularModeShowsOptionalFeatures() {
        Setting.putMode(Setting.MODE_DEFAULT);
        assertTrue(ModePolicy.showPush());
        assertTrue(ModePolicy.showSiteSwitch());
        assertTrue(ModePolicy.showTrackSetting());
    }

    @Test public void elderModeHidesOptionalFeatures() {
        Setting.putMode(Setting.MODE_ELDER);
        assertFalse(ModePolicy.showPush());
        assertFalse(ModePolicy.showSiteSwitch());
        assertFalse(ModePolicy.showTrackSetting());
    }
}

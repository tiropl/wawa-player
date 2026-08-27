package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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

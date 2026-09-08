package com.wawa_player.android.tv.setting;

public class ModePolicy {

    public static boolean showPush() {
        return !Setting.isElderMode();
    }

    public static boolean showSiteSwitch() {
        return !Setting.isElderMode();
    }

}

package com.wawa_player.android.tv.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import com.wawa_player.android.tv.setting.Setting;

public class FontUtil {

    public static Context scaled(Context context) {
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.fontScale *= Setting.getFontScale();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            config.fontWeightAdjustment = Setting.getFontWeightAdjustment();
        }
        return context.createConfigurationContext(config);
    }
}

package com.fongmi.android.tv.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.setting.Setting;

public class LoadingSound {

    private static MediaPlayer sPlayer;

    public static void start(Context context) {
        if (sPlayer != null) return;
        if (!Setting.isSound()) return;
        try {
            sPlayer = MediaPlayer.create(context, R.raw.loading);
            if (sPlayer == null) return;
            sPlayer.setLooping(true);
            sPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (sPlayer == null) return;
        try {
            if (sPlayer.isPlaying()) sPlayer.stop();
            sPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sPlayer = null;
    }
}

package com.wawa_player.android.tv.playback;

import com.wawa_player.android.tv.bean.Result;

public record PlaybackResult<T>(T request, Result result) {
}

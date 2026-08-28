package com.wawa_player.android.tv.playback;

import static com.google.common.truth.Truth.assertThat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class PlaybackIntentTest {

    @Test
    public void onExternalResult_nullData_doesNothing() {
        AtomicReference<Boolean> onNextCalled = new AtomicReference<>(false);
        AtomicLong seekToValue = new AtomicLong(-1);
        PlaybackIntent.onExternalResult(null, () -> onNextCalled.set(true), pos -> seekToValue.set(pos));
        assertThat(onNextCalled.get()).isFalse();
        assertThat(seekToValue.get()).isEqualTo(-1);
    }

    @Test
    public void onExternalResult_nullExtras_doesNothing() {
        AtomicReference<Boolean> onNextCalled = new AtomicReference<>(false);
        AtomicLong seekToValue = new AtomicLong(-1);
        Intent data = new Intent();
        PlaybackIntent.onExternalResult(data, () -> onNextCalled.set(true), pos -> seekToValue.set(pos));
        assertThat(onNextCalled.get()).isFalse();
        assertThat(seekToValue.get()).isEqualTo(-1);
    }

    @Test
    public void onExternalResult_playbackCompletion_callsOnNext() {
        AtomicReference<Boolean> onNextCalled = new AtomicReference<>(false);
        AtomicLong seekToValue = new AtomicLong(-1);
        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putString("end_by", "playback_completion");
        extras.putInt("position", 5000);
        data.putExtras(extras);
        PlaybackIntent.onExternalResult(data, () -> onNextCalled.set(true), pos -> seekToValue.set(pos));
        ShadowLooper.idleMainLooper();
        assertThat(onNextCalled.get()).isTrue();
    }

    @Test
    public void onExternalResult_userEndBy_callsSeekTo() {
        AtomicReference<Boolean> onNextCalled = new AtomicReference<>(false);
        AtomicLong seekToValue = new AtomicLong(-1);
        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putString("end_by", "user");
        extras.putInt("position", 12345);
        data.putExtras(extras);
        PlaybackIntent.onExternalResult(data, () -> onNextCalled.set(true), pos -> seekToValue.set(pos));
        assertThat(seekToValue.get()).isEqualTo(12345);
    }

    @Test
    public void onExternalResult_otherEndBy_doesNothing() {
        AtomicReference<Boolean> onNextCalled = new AtomicReference<>(false);
        AtomicLong seekToValue = new AtomicLong(-1);
        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putString("end_by", "unknown");
        extras.putInt("position", 5000);
        data.putExtras(extras);
        PlaybackIntent.onExternalResult(data, () -> onNextCalled.set(true), pos -> seekToValue.set(pos));
        assertThat(onNextCalled.get()).isFalse();
        assertThat(seekToValue.get()).isEqualTo(-1);
    }

    @Test
    public void onExternalResult_emptyEndBy_doesNothing() {
        AtomicReference<Boolean> onNextCalled = new AtomicReference<>(false);
        AtomicLong seekToValue = new AtomicLong(-1);
        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putString("end_by", "");
        data.putExtras(extras);
        PlaybackIntent.onExternalResult(data, () -> onNextCalled.set(true), pos -> seekToValue.set(pos));
        assertThat(onNextCalled.get()).isFalse();
        assertThat(seekToValue.get()).isEqualTo(-1);
    }

    @Test
    public void onExternalResult_missingEndBy_doesNothing() {
        AtomicReference<Boolean> onNextCalled = new AtomicReference<>(false);
        AtomicLong seekToValue = new AtomicLong(-1);
        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putInt("position", 5000);
        data.putExtras(extras);
        PlaybackIntent.onExternalResult(data, () -> onNextCalled.set(true), pos -> seekToValue.set(pos));
        assertThat(onNextCalled.get()).isFalse();
        assertThat(seekToValue.get()).isEqualTo(-1);
    }

    @Test
    public void onExternalResult_missingPosition_usesZero() {
        AtomicLong seekToValue = new AtomicLong(-1);
        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putString("end_by", "user");
        data.putExtras(extras);
        PlaybackIntent.onExternalResult(data, () -> {}, pos -> seekToValue.set(pos));
        assertThat(seekToValue.get()).isEqualTo(0);
    }
}

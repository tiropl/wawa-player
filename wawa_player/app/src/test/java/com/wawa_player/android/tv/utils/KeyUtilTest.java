package com.wawa_player.android.tv.utils;

import static com.google.common.truth.Truth.assertThat;

import android.view.KeyEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class KeyUtilTest {

    @Test
    public void detectsActionAndNavigationKeys() {
        KeyEvent down = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP);
        KeyEvent up = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP);

        assertThat(KeyUtil.isActionDown(down)).isTrue();
        assertThat(KeyUtil.isActionUp(up)).isTrue();
        assertThat(KeyUtil.isUpKey(down)).isTrue();
        assertThat(KeyUtil.isDownKey(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PAGE_DOWN))).isTrue();
        assertThat(KeyUtil.isLeftKey(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT))).isTrue();
        assertThat(KeyUtil.isRightKey(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT))).isTrue();
        assertThat(KeyUtil.isBackKey(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))).isTrue();
    }

    @Test
    public void detectsEnterDigitsMenuAndMediaKeys() {
        assertThat(KeyUtil.isEnterKey(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE))).isTrue();
        assertThat(KeyUtil.isDigitKey(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_7))).isTrue();
        assertThat(KeyUtil.isDigitKey(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_NUMPAD_3))).isTrue();
        assertThat(KeyUtil.isMenuKey(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MENU))).isTrue();
        assertThat(KeyUtil.isMenuKey(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MENU))).isFalse();
        assertThat(KeyUtil.isMediaPlayPause(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY))).isTrue();
        assertThat(KeyUtil.isMediaStop(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_STOP))).isTrue();
        assertThat(KeyUtil.isMediaRewind(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_REWIND))).isTrue();
        assertThat(KeyUtil.isMediaFastForward(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_FAST_FORWARD))).isTrue();
    }

    @Test
    public void rejectsUnrelatedOrWrongActionKeys() {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A);

        assertThat(KeyUtil.isEnterKey(event)).isFalse();
        assertThat(KeyUtil.isDigitKey(event)).isFalse();
        assertThat(KeyUtil.isMediaPlayPause(event)).isFalse();
        assertThat(KeyUtil.isMediaStop(event)).isFalse();
    }
}

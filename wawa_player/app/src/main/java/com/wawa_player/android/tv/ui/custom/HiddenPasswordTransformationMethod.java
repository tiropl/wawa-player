package com.wawa_player.android.tv.ui.custom;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

// 全程圆点遮挡的密码显示方式（默认 PasswordTransformationMethod 会短暂显示刚输入的字符）
public class HiddenPasswordTransformationMethod extends PasswordTransformationMethod {

    private static HiddenPasswordTransformationMethod sInstance;

    public static HiddenPasswordTransformationMethod getInstance() {
        if (sInstance == null) sInstance = new HiddenPasswordTransformationMethod();
        return sInstance;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new HiddenCharSequence(source);
    }

    private static class HiddenCharSequence implements CharSequence {

        private final CharSequence source;

        HiddenCharSequence(CharSequence source) {
            this.source = source;
        }

        @Override
        public char charAt(int index) {
            return '\u2022';
        }

        @Override
        public int length() {
            return source.length();
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new HiddenCharSequence(source.subSequence(start, end));
        }

        @Override
        public String toString() {
            return source.toString();
        }
    }
}

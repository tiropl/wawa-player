package com.wawa_player.android.tv.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ImeUtil {

    public static boolean isVisible(View view) {
        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(view);
        return insets != null && insets.isVisible(WindowInsetsCompat.Type.ime());
    }

    public static void hide(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

package com.wawa_player.android.tv.ui.dialog;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.wawa_player.android.tv.utils.ImeUtil;
import com.wawa_player.android.tv.utils.ResUtil;
import com.wawa_player.android.tv.utils.ViewUtil;

public abstract class BaseLockDialog extends BaseAlertDialog {

    protected abstract View getContentView();

    @Override
    public void onStart() {
        super.onStart();
        setWidth(0.45f);
        if (getDialog() == null || getDialog().getWindow() == null) return;
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ViewUtil.constrainToWindow(getContentView(), ResUtil.dp2px(16));
        getDialog().setOnKeyListener((dialog, keyCode, event) -> onKey(keyCode, event));
    }

    private boolean onKey(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK || event.getAction() != KeyEvent.ACTION_DOWN) return false;
        View view = getContentView();
        if (view == null || !ImeUtil.isVisible(view)) return false;
        ImeUtil.hide(view);
        return true;
    }

    protected void showError(TextView view, int resId) {
        view.setText(resId);
        view.setVisibility(View.VISIBLE);
        ViewUtil.scrollToReveal(view);
    }
}

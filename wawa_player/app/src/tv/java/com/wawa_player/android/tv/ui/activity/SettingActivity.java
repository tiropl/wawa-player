package com.wawa_player.android.tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.ActivitySettingBinding;
import com.wawa_player.android.tv.event.RefreshEvent;
import com.wawa_player.android.tv.impl.LockListener;
import com.wawa_player.android.tv.setting.PasswordLock;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.adapter.LockActionAdapter;
import com.wawa_player.android.tv.ui.base.BaseActivity;
import com.wawa_player.android.tv.ui.dialog.LockActionDialog;
import com.wawa_player.android.tv.ui.dialog.LockChangeDialog;
import com.wawa_player.android.tv.ui.dialog.LockSetDialog;
import com.wawa_player.android.tv.ui.dialog.LockVerifyDialog;
import com.wawa_player.android.tv.ui.dialog.ModeDialog;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.ResUtil;

public class SettingActivity extends BaseActivity implements ModeDialog.Listener, LockActionDialog.Listener, LockSetDialog.Listener {

    private ActivitySettingBinding mBinding;
    private final String[] modes = new String[3];

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingActivity.class));
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivitySettingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding.lineConfig.requestFocus();
        setOtherText();
        setLockText();
    }

    private void setOtherText() {
        modes[Setting.MODE_DEFAULT] = ResUtil.getString(R.string.setting_mode_default);
        modes[Setting.MODE_ELDER] = ResUtil.getString(R.string.setting_mode_elder);
        modes[Setting.MODE_CHILD] = ResUtil.getString(R.string.setting_mode_child);
        mBinding.modeText.setText(modes[Setting.getMode()]);
    }

    @Override
    protected void initEvent() {
        mBinding.lineConfig.setOnClickListener(this::onLineConfig);
        mBinding.mode.setOnClickListener(this::setMode);
        mBinding.player.setOnClickListener(this::onPlayer);
        mBinding.personal.setOnClickListener(this::onPersonal);
        mBinding.lock.setOnClickListener(this::onLock);
        mBinding.more.setOnClickListener(this::onMore);
    }

    private void onLineConfig(View view) {
        SettingLineActivity.start(this);
    }

    private void onPersonal(View view) {
        SettingPersonalActivity.start(this);
    }

    private void onMore(View view) {
        SettingMoreActivity.start(this);
    }

    private void onPlayer(View view) {
        SettingPlayerActivity.start(this);
    }

    private void setMode(View view) {
        ModeDialog.create().index(Setting.getMode()).show(this);
    }

    @Override
    public void setMode(int mode) {
        if (mode == Setting.MODE_CHILD) {
            Notify.show(R.string.setting_mode_coming_soon);
            return;
        }
        Setting.putMode(mode);
        mBinding.modeText.setText(modes[mode]);
        Notify.show(getString(R.string.setting_mode_changed, modes[mode]));
        RefreshEvent.mode();
    }

    private void setLockText() {
        mBinding.lockText.setText(Setting.getSwitch(PasswordLock.isSet()));
    }

    private void onLock(View view) {
        if (PasswordLock.isSet()) LockActionDialog.create().show(this);
        else LockSetDialog.create().listener(this).show(this);
    }

    @Override
    public void onLockAction(int action) {
        if (action == LockActionAdapter.ACTION_CHANGE) LockChangeDialog.create().show(this);
        else LockVerifyDialog.create().listener(clearLockListener).show(this);
    }

    @Override
    public void onLockSet() {
        setLockText();
    }

    private final LockListener clearLockListener = new LockListener() {
        @Override
        public void onLockVerified() {
            PasswordLock.clear();
            Notify.show(R.string.lock_clear_success);
            setLockText();
        }

        @Override
        public void onLockCancelled() {
        }
    };

}

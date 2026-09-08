package com.wawa_player.android.tv.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.FragmentSettingBinding;
import com.wawa_player.android.tv.event.RefreshEvent;
import com.wawa_player.android.tv.impl.LockListener;
import com.wawa_player.android.tv.setting.PasswordLock;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.activity.HomeActivity;
import com.wawa_player.android.tv.ui.base.BaseFragment;
import com.wawa_player.android.tv.ui.dialog.LockChangeDialog;
import com.wawa_player.android.tv.ui.dialog.LockSetDialog;
import com.wawa_player.android.tv.ui.dialog.LockVerifyDialog;
import com.wawa_player.android.tv.utils.Notify;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingFragment extends BaseFragment implements LockSetDialog.Listener, LockListener {

    private FragmentSettingBinding mBinding;
    private final String[] modes = new String[3];

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    private HomeActivity getRoot() {
        return (HomeActivity) requireActivity();
    }

    @Override
    protected ViewBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return mBinding = FragmentSettingBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        setOtherText();
        setLockText();
    }

    private void setOtherText() {
        modes[Setting.MODE_DEFAULT] = getString(R.string.setting_mode_default);
        modes[Setting.MODE_ELDER] = getString(R.string.setting_mode_elder);
        modes[Setting.MODE_CHILD] = getString(R.string.setting_mode_child);
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
        getRoot().change(7);
    }

    private void onPersonal(View view) {
        getRoot().change(3);
    }

    private void onMore(View view) {
        getRoot().change(6);
    }

    private void onPlayer(View view) {
        getRoot().change(2);
    }

    private void setMode(View view) {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.setting_mode).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(modes, Setting.getMode(), (dialog, which) -> {
            if (which == Setting.MODE_CHILD) {
                Notify.show(R.string.setting_mode_coming_soon);
                return;
            }
            Setting.putMode(which);
            mBinding.modeText.setText(modes[which]);
            Notify.show(getString(R.string.setting_mode_changed, modes[which]));
            RefreshEvent.mode();
            dialog.dismiss();
        }).show();
    }

    private void setLockText() {
        mBinding.lockText.setText(Setting.getSwitch(PasswordLock.isSet()));
    }

    private void onLock(View view) {
        if (PasswordLock.isSet()) showLockActions();
        else LockSetDialog.create().listener(this).show(this);
    }

    private void showLockActions() {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.setting_lock).setItems(new String[]{getString(R.string.lock_action_change), getString(R.string.lock_action_clear)}, (dialog, which) -> {
            if (which == 0) LockChangeDialog.create().show(this);
            else LockVerifyDialog.create().listener(this).show(this);
        }).setNegativeButton(R.string.dialog_negative, null).show();
    }

    @Override
    public void onLockSet() {
        setLockText();
    }

    @Override
    public void onLockVerified() {
        PasswordLock.clear();
        Notify.show(R.string.lock_clear_success);
        setLockText();
    }

    @Override
    public void onLockCancelled() {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) return;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

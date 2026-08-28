package com.wawa_player.android.tv.ui.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.FragmentSettingBinding;
import com.wawa_player.android.tv.event.RefreshEvent;
import com.wawa_player.android.tv.setting.PlayerSetting;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.activity.HomeActivity;
import com.wawa_player.android.tv.ui.base.BaseFragment;
import com.wawa_player.android.tv.ui.dialog.FontDialog;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingFragment extends BaseFragment implements FontDialog.Listener {

    private FragmentSettingBinding mBinding;
    private String[] size;
    private String[] fonts;
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
    }

    private void setOtherText() {
        modes[Setting.MODE_DEFAULT] = getString(R.string.setting_mode_default);
        modes[Setting.MODE_ELDER] = getString(R.string.setting_mode_elder);
        modes[Setting.MODE_CHILD] = getString(R.string.setting_mode_child);
        mBinding.modeText.setText(modes[Setting.getMode()]);
        mBinding.soundText.setText(Setting.getSwitch(Setting.isSound()));
        mBinding.sizeText.setText((size = ResUtil.getStringArray(R.array.select_size))[PlayerSetting.getSize()]);
        mBinding.fontText.setText((fonts = ResUtil.getStringArray(R.array.select_font))[Setting.getFont()]);
    }

    @Override
    protected void initEvent() {
        mBinding.lineConfig.setOnClickListener(this::onLineConfig);
        mBinding.size.setOnClickListener(this::setSize);
        mBinding.font.setOnClickListener(this::setFont);
        mBinding.player.setOnClickListener(this::onPlayer);
        mBinding.mode.setOnClickListener(this::setMode);
        mBinding.sound.setOnClickListener(this::setSound);
        mBinding.more.setOnClickListener(this::onMore);
    }

    private void onLineConfig(View view) {
        getRoot().change(7);
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

    private void setSound(View view) {
        Setting.putSound(!Setting.isSound());
        mBinding.soundText.setText(Setting.getSwitch(Setting.isSound()));
        Notify.show(ResUtil.getString(R.string.setting_sound_state, Setting.getSwitch(Setting.isSound())));
    }

    private void setSize(View view) {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.setting_size).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(size, PlayerSetting.getSize(), (dialog, which) -> {
            mBinding.sizeText.setText(size[which]);
            PlayerSetting.putSize(which);
            RefreshEvent.size();
            Notify.show(ResUtil.getString(R.string.setting_size_changed, size[which]));
            dialog.dismiss();
        }).show();
    }

    private void setFont(View view) {
        FontDialog.create().show(this);
    }

    @Override
    public void onFontChanged() {
        mBinding.fontText.setText(fonts[Setting.getFont()]);
        showRestartDialog();
    }

    private void showRestartDialog() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.restart_app_title).setMessage(R.string.restart_app_content).setPositiveButton(R.string.restart_now, (d, w) -> App.restart()).setNegativeButton(R.string.restart_later, null).show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).requestFocus();
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
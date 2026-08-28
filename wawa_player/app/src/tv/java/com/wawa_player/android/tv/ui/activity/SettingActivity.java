package com.wawa_player.android.tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.ActivitySettingBinding;
import com.wawa_player.android.tv.event.RefreshEvent;
import com.wawa_player.android.tv.setting.PlayerSetting;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.base.BaseActivity;
import com.wawa_player.android.tv.ui.dialog.FontDialog;
import com.wawa_player.android.tv.ui.dialog.ModeDialog;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingActivity extends BaseActivity implements FontDialog.Listener, ModeDialog.Listener {

    private ActivitySettingBinding mBinding;
    private String[] size;
    private String[] fonts;
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
    }

    private void setOtherText() {
        modes[Setting.MODE_DEFAULT] = ResUtil.getString(R.string.setting_mode_default);
        modes[Setting.MODE_ELDER] = ResUtil.getString(R.string.setting_mode_elder);
        modes[Setting.MODE_CHILD] = ResUtil.getString(R.string.setting_mode_child);
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
        mBinding.mode.setOnClickListener(this::setMode);
        mBinding.player.setOnClickListener(this::onPlayer);
        mBinding.sound.setOnClickListener(this::setSound);
        mBinding.more.setOnClickListener(this::onMore);
    }

    private void onLineConfig(View view) {
        SettingLineActivity.start(this);
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

    private void setSound(View view) {
        Setting.putSound(!Setting.isSound());
        mBinding.soundText.setText(Setting.getSwitch(Setting.isSound()));
        Notify.show(ResUtil.getString(R.string.setting_sound_state, Setting.getSwitch(Setting.isSound())));
    }

    private void setSize(View view) {
        new MaterialAlertDialogBuilder(this).setTitle(R.string.setting_size).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(size, PlayerSetting.getSize(), (dialog, which) -> {
            mBinding.sizeText.setText(size[which]);
            PlayerSetting.putSize(which);
            RefreshEvent.size();
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
        AlertDialog dialog = new MaterialAlertDialogBuilder(this).setTitle(R.string.restart_app_title).setMessage(R.string.restart_app_content).setPositiveButton(R.string.restart_now, (d, w) -> App.restart()).setNegativeButton(R.string.restart_later, null).show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).requestFocus();
    }

}

package com.wawa_player.android.tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.api.config.WallConfig;
import com.wawa_player.android.tv.databinding.ActivitySettingPersonalBinding;
import com.wawa_player.android.tv.event.ConfigEvent;
import com.wawa_player.android.tv.event.RefreshEvent;
import com.wawa_player.android.tv.impl.Callback;
import com.wawa_player.android.tv.setting.PlayerSetting;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.base.BaseActivity;
import com.wawa_player.android.tv.ui.dialog.FontDialog;
import com.wawa_player.android.tv.ui.dialog.HistoryDialog;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingPersonalActivity extends BaseActivity implements FontDialog.Listener {

    private ActivitySettingPersonalBinding mBinding;
    private String[] size;
    private String[] fonts;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingPersonalActivity.class));
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivitySettingPersonalBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding.sound.requestFocus();
        mBinding.soundText.setText(Setting.getSwitch(Setting.isSound()));
        mBinding.sizeText.setText((size = ResUtil.getStringArray(R.array.select_size))[PlayerSetting.getSize()]);
        mBinding.fontText.setText((fonts = ResUtil.getStringArray(R.array.select_font))[Setting.getFont()]);
    }

    @Override
    protected void initEvent() {
        mBinding.sound.setOnClickListener(this::setSound);
        mBinding.font.setOnClickListener(this::setFont);
        mBinding.size.setOnClickListener(this::setSize);
        mBinding.wallDefault.setOnClickListener(this::setWallDefault);
        mBinding.wallRefresh.setOnClickListener(this::setWallRefresh);
        mBinding.wallRefresh.setOnLongClickListener(this::onWallHistory);
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

    private void setWallDefault(View view) {
        Setting.putWall(Setting.getWall() == 4 ? 1 : Setting.getWall() + 1);
        Setting.putWallType(0);
        ConfigEvent.wall();
        Notify.show(R.string.setting_wall_changed);
    }

    private void setWallRefresh(View view) {
        if (TextUtils.isEmpty(WallConfig.getUrl())) return;
        Setting.putWall(0);
        WallConfig.get().load(getCallback());
    }

    private boolean onWallHistory(View view) {
        HistoryDialog.create().wall().show(this);
        return true;
    }

    private Callback getCallback() {
        return new Callback() {
            @Override
            public void start() {
                Notify.progress(getActivity());
            }

            @Override
            public void success() {
                Notify.dismiss();
            }

            @Override
            public void error(String msg) {
                Notify.dismiss();
                Notify.show(msg);
            }
        };
    }
}

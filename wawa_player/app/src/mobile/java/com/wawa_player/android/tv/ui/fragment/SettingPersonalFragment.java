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
import com.wawa_player.android.tv.api.config.WallConfig;
import com.wawa_player.android.tv.databinding.FragmentSettingPersonalBinding;
import com.wawa_player.android.tv.event.ConfigEvent;
import com.wawa_player.android.tv.event.RefreshEvent;
import com.wawa_player.android.tv.impl.Callback;
import com.wawa_player.android.tv.setting.PlayerSetting;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.base.BaseFragment;
import com.wawa_player.android.tv.ui.dialog.FontDialog;
import com.wawa_player.android.tv.ui.dialog.HistoryDialog;
import com.wawa_player.android.tv.ui.dialog.ThemeDialog;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingPersonalFragment extends BaseFragment implements FontDialog.Listener, ThemeDialog.Listener {

    private FragmentSettingPersonalBinding mBinding;
    private String[] size;
    private String[] fonts;

    public static SettingPersonalFragment newInstance() {
        return new SettingPersonalFragment();
    }

    @Override
    protected ViewBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return mBinding = FragmentSettingPersonalBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        mBinding.soundText.setText(Setting.getSwitch(Setting.isSound()));
        mBinding.sizeText.setText((size = ResUtil.getStringArray(R.array.select_size))[PlayerSetting.getSize()]);
        mBinding.fontText.setText((fonts = ResUtil.getStringArray(R.array.select_font))[Setting.getFont()]);
        mBinding.themeColorText.setText(getThemeText());
    }

    private String getThemeText() {
        int color = Setting.getThemeColor();
        if (color == -1) return getString(R.string.setting_off);
        return getString(color == 0 ? R.string.setting_auto : R.string.setting_custom);
    }

    @Override
    protected void initEvent() {
        mBinding.sound.setOnClickListener(this::setSound);
        mBinding.font.setOnClickListener(this::setFont);
        mBinding.size.setOnClickListener(this::setSize);
        mBinding.themeColor.setOnClickListener(this::onThemeColor);
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

    private void onThemeColor(View view) {
        ThemeDialog.show(this);
    }

    @Override
    public void setTheme(int color) {
        Setting.putThemeColor(color);
        RefreshEvent.theme();
        Notify.show(ResUtil.getString(R.string.setting_theme_changed, getThemeText()));
    }

    private void setWallDefault(View view) {
        if (TextUtils.isEmpty(WallConfig.getUrl())) return;
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
                Notify.progress(requireActivity());
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

package com.wawa_player.android.tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.BuildConfig;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.api.config.VodConfig;
import com.wawa_player.android.tv.databinding.ActivitySettingMoreBinding;
import com.wawa_player.android.tv.db.AppDatabase;
import com.wawa_player.android.tv.impl.Callback;
import com.wawa_player.android.tv.impl.ConfigListener;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.base.BaseActivity;
import com.wawa_player.android.tv.ui.dialog.DohDialog;
import com.wawa_player.android.tv.ui.dialog.RestoreDialog;
import com.wawa_player.android.tv.utils.FileUtil;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.PermissionUtil;
import com.wawa_player.android.tv.utils.ResUtil;
import com.github.catvod.bean.Doh;
import com.github.catvod.net.OkHttp;

import java.util.ArrayList;
import java.util.List;

import com.wawa_player.android.tv.Updater;

public class SettingMoreActivity extends BaseActivity implements DohDialog.Listener {

    private ActivitySettingMoreBinding mBinding;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingMoreActivity.class));
    }

    private int getDohIndex() {
        return Math.max(0, VodConfig.get().getDoh().indexOf(Doh.objectFrom(Setting.getDoh())));
    }

    private String[] getDohList() {
        List<String> list = new ArrayList<>();
        for (Doh item : VodConfig.get().getDoh()) list.add(item.getName());
        return list.toArray(new String[0]);
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivitySettingMoreBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding.incognito.requestFocus();
        mBinding.versionText.setText(BuildConfig.VERSION_NAME);
        mBinding.dohText.setText(getDohList()[getDohIndex()]);
        mBinding.incognitoText.setText(Setting.getSwitch(Setting.isIncognito()));
        setCacheText();
    }

    private void setCacheText() {
        FileUtil.getCacheSize(new Callback() {
            @Override
            public void success(String result) {
                mBinding.cacheText.setText(result);
            }
        });
    }

    @Override
    protected void initEvent() {
        mBinding.incognito.setOnClickListener(this::setIncognito);
        mBinding.doh.setOnClickListener(this::setDoh);
        mBinding.backup.setOnClickListener(this::onBackup);
        mBinding.restore.setOnClickListener(this::onRestore);
        mBinding.cache.setOnClickListener(this::onCache);
        mBinding.version.setOnClickListener(this::onVersion);
    }

    private void onVersion(View view) {
        Updater.create().force().start(this);
    }

    private void setIncognito(View view) {
        Setting.putIncognito(!Setting.isIncognito());
        mBinding.incognitoText.setText(Setting.getSwitch(Setting.isIncognito()));
        Notify.show(ResUtil.getString(R.string.setting_incognito_state, Setting.getSwitch(Setting.isIncognito())));
    }

    private void setDoh(View view) {
        DohDialog.create().index(getDohIndex()).show(this);
    }

    @Override
    public void setDoh(Doh doh) {
        OkHttp.dns().setDoh(doh);
        Setting.putDoh(doh.toString());
        mBinding.dohText.setText(doh.getName());
        Notify.show(ResUtil.getString(R.string.setting_doh_changed, doh.getName()));
    }

    private void onCache(View view) {
        FileUtil.clearCache(new Callback() {
            @Override
            public void success() {
                setCacheText();
                Notify.show(R.string.setting_cache_cleared);
            }
        });
    }

    private void onBackup(View view) {
        PermissionUtil.requestFile(this, allGranted -> AppDatabase.backup(new Callback() {
            @Override
            public void success() {
                Notify.show(R.string.backup_success);
            }

            @Override
            public void error() {
                Notify.show(R.string.backup_fail);
            }
        }));
    }

    private void onRestore(View view) {
        PermissionUtil.requestFile(this, allGranted -> RestoreDialog.create().callback(new Callback() {
            @Override
            public void success() {
                Notify.show(R.string.restore_success);
                mBinding.versionText.setText(BuildConfig.VERSION_NAME);
                mBinding.dohText.setText(getDohList()[getDohIndex()]);
                mBinding.incognitoText.setText(Setting.getSwitch(Setting.isIncognito()));
                setCacheText();
            }

            @Override
            public void error() {
                Notify.show(R.string.restore_fail);
            }
        }).show(this));
    }
}

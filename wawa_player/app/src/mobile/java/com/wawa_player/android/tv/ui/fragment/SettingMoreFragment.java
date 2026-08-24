package com.wawa_player.android.tv.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.BuildConfig;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.Updater;
import com.wawa_player.android.tv.api.config.VodConfig;
import com.wawa_player.android.tv.databinding.FragmentSettingMoreBinding;
import com.wawa_player.android.tv.db.AppDatabase;
import com.wawa_player.android.tv.event.RefreshEvent;
import com.wawa_player.android.tv.impl.Callback;
import com.wawa_player.android.tv.impl.LockListener;
import com.wawa_player.android.tv.setting.PasswordLock;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.base.BaseFragment;
import com.wawa_player.android.tv.ui.dialog.LockChangeDialog;
import com.wawa_player.android.tv.ui.dialog.LockSetDialog;
import com.wawa_player.android.tv.ui.dialog.LockVerifyDialog;
import com.wawa_player.android.tv.ui.dialog.RestoreDialog;
import com.wawa_player.android.tv.utils.FileUtil;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.PermissionUtil;
import com.wawa_player.android.tv.utils.ResUtil;
import com.github.catvod.bean.Doh;
import com.github.catvod.net.OkHttp;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SettingMoreFragment extends BaseFragment implements LockSetDialog.Listener, LockListener {

    private FragmentSettingMoreBinding mBinding;
    private final String[] modes = new String[3];

    public static SettingMoreFragment newInstance() {
        return new SettingMoreFragment();
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
    protected ViewBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return mBinding = FragmentSettingMoreBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        mBinding.versionText.setText(BuildConfig.VERSION_NAME);
        mBinding.dohText.setText(getDohList()[getDohIndex()]);
        mBinding.incognitoText.setText(Setting.getSwitch(Setting.isIncognito()));
        modes[Setting.MODE_DEFAULT] = getString(R.string.setting_mode_default);
        modes[Setting.MODE_ELDER] = getString(R.string.setting_mode_elder);
        modes[Setting.MODE_CHILD] = getString(R.string.setting_mode_child);
        mBinding.modeText.setText(modes[Setting.getMode()]);
        setLockText();
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
        mBinding.mode.setOnClickListener(this::setMode);
        mBinding.lock.setOnClickListener(this::onLock);
        mBinding.incognito.setOnClickListener(this::setIncognito);
        mBinding.doh.setOnClickListener(this::setDoh);
        mBinding.cache.setOnClickListener(this::onCache);
        mBinding.backup.setOnClickListener(this::onBackup);
        mBinding.restore.setOnClickListener(this::onRestore);
        mBinding.version.setOnClickListener(this::onVersion);
    }

    private void onVersion(View view) {
        Updater.create().force().start(requireActivity());
    }

    private void setIncognito(View view) {
        Setting.putIncognito(!Setting.isIncognito());
        mBinding.incognitoText.setText(Setting.getSwitch(Setting.isIncognito()));
        Notify.show(ResUtil.getString(R.string.setting_incognito_state, Setting.getSwitch(Setting.isIncognito())));
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

    private void setDoh(View view) {
        new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.setting_doh).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(getDohList(), getDohIndex(), (dialog, which) -> {
            Doh doh = VodConfig.get().getDoh().get(which);
            OkHttp.dns().setDoh(doh);
            Setting.putDoh(doh.toString());
            mBinding.dohText.setText(doh.getName());
            Notify.show(ResUtil.getString(R.string.setting_doh_changed, doh.getName()));
            dialog.dismiss();
        }).show();
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
        PermissionUtil.requestFile(requireActivity(), allGranted -> AppDatabase.backup(new Callback() {
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
        PermissionUtil.requestFile(requireActivity(), allGranted -> RestoreDialog.create().show(requireActivity(), new Callback() {
            @Override
            public void success() {
                Notify.show(R.string.restore_success);
                mBinding.versionText.setText(BuildConfig.VERSION_NAME);
                mBinding.dohText.setText(getDohList()[getDohIndex()]);
                mBinding.incognitoText.setText(Setting.getSwitch(Setting.isIncognito()));
                mBinding.modeText.setText(modes[Setting.getMode()]);
                setCacheText();
            }

            @Override
            public void error() {
                Notify.show(R.string.restore_fail);
            }
        }));
    }
}

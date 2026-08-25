package com.wawa_player.android.tv.ui.activity;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.Lifecycle;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.Updater;
import com.wawa_player.android.tv.api.config.LineConfig;
import com.wawa_player.android.tv.api.config.LiveConfig;
import com.wawa_player.android.tv.api.config.VodConfig;
import com.wawa_player.android.tv.api.config.WallConfig;
import com.wawa_player.android.tv.bean.Config;
import com.wawa_player.android.tv.databinding.ActivityHomeBinding;
import com.wawa_player.android.tv.db.AppDatabase;
import com.wawa_player.android.tv.event.ConfigEvent;
import com.wawa_player.android.tv.event.RefreshEvent;
import com.wawa_player.android.tv.event.ServerEvent;
import com.wawa_player.android.tv.event.StateEvent;
import com.wawa_player.android.tv.impl.Callback;
import com.wawa_player.android.tv.impl.ConfigListener;
import com.wawa_player.android.tv.impl.LockListener;
import com.wawa_player.android.tv.player.extractor.Source;
import com.wawa_player.android.tv.receiver.ShortcutReceiver;
import com.wawa_player.android.tv.server.Server;
import com.wawa_player.android.tv.setting.PasswordLock;
import com.wawa_player.android.tv.ui.base.BaseActivity;
import com.wawa_player.android.tv.ui.custom.FragmentStateManager;
import com.wawa_player.android.tv.ui.dialog.ConfigDialog;
import com.wawa_player.android.tv.ui.dialog.LockVerifyDialog;
import com.wawa_player.android.tv.ui.fragment.SettingDanmakuFragment;
import com.wawa_player.android.tv.ui.fragment.SettingDecodeFragment;
import com.wawa_player.android.tv.ui.fragment.SettingFragment;
import com.wawa_player.android.tv.ui.fragment.SettingMoreFragment;
import com.wawa_player.android.tv.ui.fragment.SettingPlayerFragment;
import com.wawa_player.android.tv.ui.fragment.SettingPreloadFragment;
import com.wawa_player.android.tv.ui.fragment.VodFragment;
import com.wawa_player.android.tv.utils.FileChooser;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.PermissionUtil;
import com.wawa_player.android.tv.utils.UrlUtil;
import com.wawa_player.android.tv.utils.Util;
import com.github.catvod.net.OkHttp;
import com.google.android.material.navigation.NavigationBarView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeActivity extends BaseActivity implements NavigationBarView.OnItemSelectedListener, ConfigListener {

    private FragmentStateManager mManager;
    private ActivityHomeBinding mBinding;
    private int orientation;
    private int mPreviousPosition = -1;
    private boolean lockVerified;

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivityHomeBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkAction(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        orientation = getResources().getConfiguration().orientation;
        mBinding.navigation.setOnItemSelectedListener(this);
        PermissionUtil.requestNotify(this);
        setNavigation();
        initFragment(savedInstanceState);
        Updater.create().start(this);
        initConfig(savedInstanceState);
    }

    @Override
    protected void initEvent() {
        mBinding.navigation.findViewById(R.id.live).setOnLongClickListener(this::addShortcut);
    }

    private void checkAction(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            VideoActivity.push(this, intent.getStringExtra(Intent.EXTRA_TEXT));
        } else if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
            PermissionUtil.requestFile(this, allGranted -> checkType(intent));
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String keyword = intent.getStringExtra(SearchManager.QUERY);
            if (!TextUtils.isEmpty(keyword)) SearchActivity.start(this, keyword);
        }
    }

    private void checkType(Intent intent) {
        if ("text/plain".equals(intent.getType()) || UrlUtil.path(intent.getData()).endsWith(".m3u")) {
            loadLive("file:/" + FileChooser.getPathFromUri(intent.getData()));
        } else {
            VideoActivity.push(this, intent.getData().toString());
        }
    }

    private void initFragment(Bundle savedInstanceState) {
        mManager = new FragmentStateManager(mBinding.container, getSupportFragmentManager(), position -> switch (position) {
            case 0 -> VodFragment.newInstance();
            case 1 -> SettingFragment.newInstance();
            case 2 -> SettingPlayerFragment.newInstance();
            case 3 -> SettingDanmakuFragment.newInstance();
            case 4 -> SettingPreloadFragment.newInstance();
            case 5 -> SettingDecodeFragment.newInstance();
            case 6 -> SettingMoreFragment.newInstance();
            default -> null;
        });
        if (savedInstanceState == null) change(0);
    }

    private void initConfig(Bundle savedInstanceState) {
        if (TextUtils.isEmpty(VodConfig.getUrl())) {
            // 未配置线路，冷启动时弹出配置窗口
            if (savedInstanceState == null) ConfigDialog.create().vod().show(this);
            return;
        }
        if (VodConfig.get().loaded()) {
            setNavigation();
            // 重建时配置已加载，避免重复拉取线路
            if (savedInstanceState != null) return;
            WallConfig.get().init();
            LineConfig.refresh(0);
            LineConfig.refresh(1);
            checkAction(getIntent());
        } else {
            // Use async init to avoid Room DB queries on main thread
            // Run VodConfig and LiveConfig in parallel for faster loading
            mBinding.progressLayout.showProgress();
            VodConfig.get().initAsync(vodConfig -> vodConfig.load(getCallback()));
            LiveConfig.get().initAsync(liveConfig -> liveConfig.load());
            WallConfig.get().initAsync(wallConfig -> {});
        }
    }

    @Override
    public void setConfig(Config config) {
        if (config.getUrl().startsWith("file")) {
            PermissionUtil.requestFile(this, allGranted -> loadVodConfig(config));
        } else {
            loadVodConfig(config);
        }
    }

    private void loadVodConfig(Config config) {
        VodConfig.load(config, new Callback() {
            @Override
            public void start() {
                mBinding.progressLayout.showProgress();
            }

            @Override
            public void success() {
                mBinding.progressLayout.showContent();
                setNavigation();
                LineConfig.refresh(0);
                LineConfig.refresh(1);
                checkAction(getIntent());
            }

            @Override
            public void error(String msg) {
                mBinding.progressLayout.showContent();
                Notify.show(msg);
            }
        }, true);
    }

    private Callback getCallback() {
        return new Callback() {
            @Override
            public void success() {
                LineConfig.refresh(0);
                LineConfig.refresh(1);
                checkAction(getIntent());
            }

            @Override
            public void error(String msg) {
                checkAction(getIntent());
                mBinding.progressLayout.showContent();
                StateEvent.empty();
                Notify.show(msg);
            }
        };
    }

    private void loadLive(String url) {
        LiveConfig.load(Config.find(url, 1), new Callback() {
            @Override
            public void success() {
                openLive();
            }
        });
    }

    private void setNavigation() {
        mBinding.navigation.getMenu().findItem(R.id.vod).setVisible(true);
        mBinding.navigation.getMenu().findItem(R.id.setting).setVisible(true);
        mBinding.navigation.getMenu().findItem(R.id.live).setVisible(LiveConfig.hasUrl());
    }

    private boolean openLive() {
        LiveActivity.start(this);
        return false;
    }

    private boolean addShortcut(View view) {
        ShortcutInfoCompat info = new ShortcutInfoCompat.Builder(this, getString(R.string.nav_live)).setIcon(IconCompat.createWithResource(this, R.mipmap.ic_launcher)).setIntent(new Intent(Intent.ACTION_VIEW, null, this, LiveActivity.class)).setShortLabel(getString(R.string.nav_live)).build();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, ShortcutReceiver.class).setAction(ShortcutReceiver.ACTION), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        ShortcutManagerCompat.requestPinShortcut(this, info, pendingIntent.getIntentSender());
        return true;
    }

    public void change(int position) {
        if (position < 2) {
            mBinding.navigation.setSelectedItemId(position == 0 ? R.id.vod : R.id.setting);
        } else {
            if (position == 2 || position == 3) {
                if (mManager.isVisible(6)) mPreviousPosition = 6;
                else if (mManager.isVisible(1)) mPreviousPosition = 1;
            }
            mManager.change(position);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfigEvent(ConfigEvent event) {
        switch (event.type()) {
            case VOD:
                RefreshEvent.home();
                break;
            case COMMON:
                setNavigation();
                break;
            case BOOT:
                LiveActivity.start(this);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        if (event.getType() == RefreshEvent.Type.THEME) recreate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerEvent(ServerEvent event) {
        if (event.type() == ServerEvent.Type.PUSH) VideoActivity.push(this, event.text());
        if (event.type() == ServerEvent.Type.SEARCH) SearchActivity.start(this, event.text());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.setting) return openSetting();
        if (item.getItemId() == R.id.vod) return mManager.change(0);
        if (item.getItemId() == R.id.live) return openLive();
        return false;
    }

    private boolean openSetting() {
        boolean inSettingFlow = lockVerified || mManager.isVisible(1) || mManager.isVisible(2) || mManager.isVisible(3) || mManager.isVisible(4) || mManager.isVisible(5) || mManager.isVisible(6);
        lockVerified = false;
        if (inSettingFlow || !PasswordLock.isSet()) return mManager.change(1);
        LockVerifyDialog.create().listener(new LockListener() {
            @Override
            public void onLockVerified() {
                lockVerified = true;
                mBinding.navigation.setSelectedItemId(R.id.setting);
            }

            @Override
            public void onLockCancelled() {
            }
        }).show(this);
        return false;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        App.post(() -> checkOrientation(newConfig), 100);
    }

    private void checkOrientation(Configuration newConfig) {
        if (orientation != newConfig.orientation) {
            orientation = newConfig.orientation;
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) RefreshEvent.home();
        }
    }

    @Override
    protected void onBackInvoked() {
        if (!mBinding.navigation.getMenu().findItem(R.id.vod).isVisible()) {
            setNavigation();
        } else if (mManager.isVisible(4) || mManager.isVisible(5)) {
            change(2);
        } else if (mManager.isVisible(3) || mManager.isVisible(2)) {
            if (mPreviousPosition == 6) {
                mPreviousPosition = -1;
                change(6);
            } else {
                change(1);
            }
        } else if (mManager.isVisible(6)) {
            mPreviousPosition = -1;
            change(1);
        } else if (mManager.isVisible(1)) {
            change(0);
        } else if (mManager.canBack(0)) {
            Util.moveToBackground(this);
        }
    }

    @Override
    protected void onDestroy() {
        LiveConfig.get().clear();
        VodConfig.get().clear();
        AppDatabase.backup();
        OkHttp.get().clear();
        Source.get().exit();
        Server.get().stop();
        super.onDestroy();
    }
}

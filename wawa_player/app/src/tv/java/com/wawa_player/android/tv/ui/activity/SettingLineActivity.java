package com.wawa_player.android.tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.api.config.LiveConfig;
import com.wawa_player.android.tv.api.config.VodConfig;
import com.wawa_player.android.tv.api.config.WallConfig;
import com.wawa_player.android.tv.bean.Config;
import com.wawa_player.android.tv.bean.Live;
import com.wawa_player.android.tv.bean.Site;
import com.wawa_player.android.tv.databinding.ActivitySettingLineBinding;
import com.wawa_player.android.tv.event.ConfigEvent;
import com.wawa_player.android.tv.impl.Callback;
import com.wawa_player.android.tv.impl.ConfigListener;
import com.wawa_player.android.tv.impl.DanmakuListener;
import com.wawa_player.android.tv.impl.LiveListener;
import com.wawa_player.android.tv.impl.SiteListener;
import com.wawa_player.android.tv.setting.DanmakuSetting;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.base.BaseActivity;
import com.wawa_player.android.tv.ui.dialog.ConfigDialog;
import com.wawa_player.android.tv.ui.dialog.DanmakuApiDialog;
import com.wawa_player.android.tv.ui.dialog.HistoryDialog;
import com.wawa_player.android.tv.ui.dialog.LiveDialog;
import com.wawa_player.android.tv.ui.dialog.SiteDialog;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.PermissionUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SettingLineActivity extends BaseActivity implements ConfigListener, SiteListener, LiveListener, DanmakuListener {

    private ActivitySettingLineBinding mBinding;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingLineActivity.class));
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivitySettingLineBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding.vod.requestFocus();
        mBinding.vodUrl.setText(VodConfig.getDesc());
        mBinding.liveUrl.setText(LiveConfig.getDesc());
        mBinding.wallUrl.setText(WallConfig.getDesc());
        mBinding.danmakuText.setText(getDanmakuStatus());
    }

    private String getDanmakuStatus() {
        return getString(TextUtils.isEmpty(DanmakuSetting.getEffectiveApiUrl()) ? R.string.none : R.string.yes);
    }

    @Override
    protected void initEvent() {
        mBinding.vod.setOnClickListener(this::onVod);
        mBinding.live.setOnClickListener(this::onLive);
        mBinding.wall.setOnClickListener(this::onWall);
        mBinding.vod.setOnLongClickListener(this::onVodEdit);
        mBinding.vodHome.setOnClickListener(this::onVodHome);
        mBinding.live.setOnLongClickListener(this::onLiveEdit);
        mBinding.liveHome.setOnClickListener(this::onLiveHome);
        mBinding.wall.setOnLongClickListener(this::onWallEdit);
        mBinding.vodHistory.setOnClickListener(this::onVodHistory);
        mBinding.liveHistory.setOnClickListener(this::onLiveHistory);
        mBinding.wallDefault.setOnClickListener(this::setWallDefault);
        mBinding.wallRefresh.setOnClickListener(this::setWallRefresh);
        mBinding.wallRefresh.setOnLongClickListener(this::onWallHistory);
        mBinding.danmaku.setOnClickListener(this::onDanmaku);
    }

    @Override
    public void setConfig(Config config) {
        if (config.getUrl().startsWith("file")) {
            PermissionUtil.requestFile(this, allGranted -> load(config));
        } else {
            load(config);
        }
    }

    private void load(Config config) {
        switch (config.getType()) {
            case 0:
                VodConfig.load(config, getCallback(), true);
                break;
            case 1:
                LiveConfig.load(config, getCallback(), true);
                break;
            case 2:
                Setting.putWall(0);
                WallConfig.load(config, getCallback());
                break;
        }
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

    @Override
    public void setSite(Site item) {
        VodConfig.get().setHome(item);
    }

    @Override
    public void setLive(Live item) {
        LiveConfig.get().setHome(item);
    }

    private void onVod(View view) {
        ConfigDialog.create().vod().show(this);
    }

    private void onLive(View view) {
        ConfigDialog.create().live().show(this);
    }

    private void onWall(View view) {
        ConfigDialog.create().wall().show(this);
    }

    private boolean onVodEdit(View view) {
        ConfigDialog.create().vod().edit().show(this);
        return true;
    }

    private boolean onLiveEdit(View view) {
        ConfigDialog.create().live().edit().show(this);
        return true;
    }

    private boolean onWallEdit(View view) {
        ConfigDialog.create().wall().edit().show(this);
        return true;
    }

    private void onVodHome(View view) {
        SiteDialog.create().action().show(this);
    }

    private void onLiveHome(View view) {
        LiveDialog.create().action().show(this);
    }

    private void onVodHistory(View view) {
        HistoryDialog.create().vod().show(this);
    }

    private void onLiveHistory(View view) {
        HistoryDialog.create().live().show(this);
    }

    private void setWallDefault(View view) {
        if (TextUtils.isEmpty(WallConfig.getUrl())) return;
        Setting.putWall(Setting.getWall() == 4 ? 1 : Setting.getWall() + 1);
        Setting.putWallType(0);
        ConfigEvent.wall();
        Notify.show(R.string.setting_wall_changed);
    }

    private void setWallRefresh(View view) {
        Setting.putWall(0);
        WallConfig.get().load(getCallback());
    }

    private boolean onWallHistory(View view) {
        HistoryDialog.create().wall().show(this);
        return true;
    }

    private void onDanmaku(View view) {
        DanmakuApiDialog.show(this);
    }

    @Override
    public void setDanmakuApi(String url) {
        DanmakuSetting.putApiUrl(url);
        mBinding.danmakuText.setText(getDanmakuStatus());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfigEvent(ConfigEvent event) {
        if (event.type() != ConfigEvent.Type.COMMON) return;
        mBinding.vodUrl.setText(VodConfig.getDesc());
        mBinding.liveUrl.setText(LiveConfig.getDesc());
        mBinding.wallUrl.setText(WallConfig.getDesc());
    }
}
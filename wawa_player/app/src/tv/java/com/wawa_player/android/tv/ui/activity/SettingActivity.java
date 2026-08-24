package com.wawa_player.android.tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.viewbinding.ViewBinding;

import com.github.catvod.bean.Doh;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.api.config.LiveConfig;
import com.wawa_player.android.tv.api.config.VodConfig;
import com.wawa_player.android.tv.api.config.WallConfig;
import com.wawa_player.android.tv.bean.Config;
import com.wawa_player.android.tv.bean.Live;
import com.wawa_player.android.tv.bean.Site;
import com.wawa_player.android.tv.databinding.ActivitySettingBinding;
import com.wawa_player.android.tv.event.ConfigEvent;
import com.wawa_player.android.tv.event.RefreshEvent;
import com.wawa_player.android.tv.impl.Callback;
import com.wawa_player.android.tv.impl.ConfigListener;
import com.wawa_player.android.tv.impl.LiveListener;
import com.wawa_player.android.tv.impl.LockListener;
import com.wawa_player.android.tv.impl.SiteListener;
import com.wawa_player.android.tv.setting.PasswordLock;
import com.wawa_player.android.tv.setting.PlayerSetting;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.adapter.LockActionAdapter;
import com.wawa_player.android.tv.ui.base.BaseActivity;
import com.wawa_player.android.tv.ui.dialog.ConfigDialog;
import com.wawa_player.android.tv.ui.dialog.HistoryDialog;
import com.wawa_player.android.tv.ui.dialog.LiveDialog;
import com.wawa_player.android.tv.ui.dialog.LockActionDialog;
import com.wawa_player.android.tv.ui.dialog.LockChangeDialog;
import com.wawa_player.android.tv.ui.dialog.LockSetDialog;
import com.wawa_player.android.tv.ui.dialog.LockVerifyDialog;
import com.wawa_player.android.tv.ui.dialog.SiteDialog;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.PermissionUtil;
import com.wawa_player.android.tv.utils.ResUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SettingActivity extends BaseActivity implements ConfigListener, SiteListener, LiveListener, LockActionDialog.Listener, LockSetDialog.Listener {

    private ActivitySettingBinding mBinding;
    private String[] size;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingActivity.class));
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
        return mBinding = ActivitySettingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding.vod.requestFocus();
        mBinding.vodUrl.setText(VodConfig.getDesc());
        mBinding.liveUrl.setText(LiveConfig.getDesc());
        mBinding.wallUrl.setText(WallConfig.getDesc());
        setOtherText();
        setLockText();
    }

    private void setOtherText() {
        mBinding.soundText.setText(Setting.getSwitch(Setting.isSound()));
        mBinding.sizeText.setText((size = ResUtil.getStringArray(R.array.select_size))[PlayerSetting.getSize()]);
    }

    @Override
    protected void initEvent() {
        mBinding.vod.setOnClickListener(this::onVod);
        mBinding.live.setOnClickListener(this::onLive);
        mBinding.wall.setOnClickListener(this::onWall);
        mBinding.size.setOnClickListener(this::setSize);
        mBinding.vod.setOnLongClickListener(this::onVodEdit);
        mBinding.vodHome.setOnClickListener(this::onVodHome);
        mBinding.live.setOnLongClickListener(this::onLiveEdit);
        mBinding.liveHome.setOnClickListener(this::onLiveHome);
        mBinding.wall.setOnLongClickListener(this::onWallEdit);
        mBinding.player.setOnClickListener(this::onPlayer);
        mBinding.danmaku.setOnClickListener(this::onDanmaku);
        mBinding.sound.setOnClickListener(this::setSound);
        mBinding.more.setOnClickListener(this::onMore);
        mBinding.lock.setOnClickListener(this::onLock);
        mBinding.vodHistory.setOnClickListener(this::onVodHistory);
        mBinding.liveHistory.setOnClickListener(this::onLiveHistory);
        mBinding.wallDefault.setOnClickListener(this::setWallDefault);
        mBinding.wallRefresh.setOnClickListener(this::setWallRefresh);
        mBinding.wallRefresh.setOnLongClickListener(this::onWallHistory);
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

    private void onMore(View view) {
        SettingMoreActivity.start(this);
    }

    private void onDanmaku(View view) {
        SettingDanmakuActivity.start(this);
    }

    private void onPlayer(View view) {
        SettingPlayerActivity.start(this);
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

    private void setWallDefault(View view) {
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

    private void setSound(View view) {
        Setting.putSound(!Setting.isSound());
        mBinding.soundText.setText(Setting.getSwitch(Setting.isSound()));
        Notify.show(ResUtil.getString(R.string.setting_sound_state, Setting.getSwitch(Setting.isSound())));
    }

    private void setSize(View view) {
        int index = (PlayerSetting.getSize() + 1) % size.length;
        mBinding.sizeText.setText(size[index]);
        PlayerSetting.putSize(index);
        RefreshEvent.size();
        Notify.show(ResUtil.getString(R.string.setting_size_changed, size[index]));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfigEvent(ConfigEvent event) {
        if (event.type() != ConfigEvent.Type.COMMON) return;
        mBinding.vodUrl.setText(VodConfig.getDesc());
        mBinding.liveUrl.setText(LiveConfig.getDesc());
        mBinding.wallUrl.setText(WallConfig.getDesc());
    }

}

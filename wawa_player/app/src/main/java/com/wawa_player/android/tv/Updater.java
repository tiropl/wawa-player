package com.wawa_player.android.tv;

import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.wawa_player.android.tv.impl.UpdateListener;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.ui.dialog.UpdateDialog;
import com.wawa_player.android.tv.utils.Download;
import com.wawa_player.android.tv.utils.FileUtil;
import com.wawa_player.android.tv.utils.Github;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.ResUtil;
import com.wawa_player.android.tv.utils.Task;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Path;

import org.json.JSONObject;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Updater implements Download.Callback, UpdateListener {

    private static final Pattern VERSION = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

    private Download download;
    private UpdateDialog dialog;
    private boolean force;

    private Updater() {
    }

    public static Updater create() {
        return new Updater();
    }

    private File getFile() {
        return Path.cache("update.apk");
    }

    public Updater force() {
        Notify.show(R.string.update_check);
        Setting.putUpdate(true);
        force = true;
        return this;
    }

    public void start(FragmentActivity activity) {
        if (!Setting.getUpdate()) return;
        Task.execute(() -> doInBackground(activity));
    }

    private void doInBackground(FragmentActivity activity) {
        try {
            JSONObject object = new JSONObject(OkHttp.string(Github.getRelease()));
            String version = getVersion(object.optString("tag_name"));
            String apk = Github.findApk(object, BuildConfig.FLAVOR_mode, BuildConfig.FLAVOR_abi);
            if (version.isEmpty() || apk.isEmpty() || !isNewer(version)) {
                if (force) App.post(() -> Notify.show(R.string.update_latest));
                return;
            }
            download = Download.create(apk, getFile());
            App.post(() -> show(activity, version, object.optString("body")));
        } catch (Exception e) {
            e.printStackTrace();
            if (force) App.post(() -> Notify.show(R.string.update_fail));
        }
    }

    private String getVersion(String tag) {
        Matcher matcher = VERSION.matcher(tag);
        return matcher.find() ? matcher.group() : "";
    }

    private boolean isNewer(String remote) {
        String[] rs = remote.split("\\.");
        String[] ls = BuildConfig.VERSION_NAME.split("\\.");
        for (int i = 0; i < 3; i++) {
            int r = i < rs.length ? Integer.parseInt(rs[i]) : 0;
            int l = i < ls.length ? Integer.parseInt(ls[i]) : 0;
            if (r != l) return r > l;
        }
        return false;
    }

    private void show(FragmentActivity activity, String version, String desc) {
        dismiss();
        dialog = UpdateDialog.create().title(ResUtil.getString(R.string.update_version, version)).desc(desc).listener(this).show(activity);
    }

    @Override
    public void onConfirm(View view) {
        view.setEnabled(false);
        download.start(this);
    }

    @Override
    public void onCancel(View view) {
        Setting.putUpdate(false);
        if (download != null) download.cancel();
        dismiss();
    }

    private void dismiss() {
        try {
            if (dialog != null) dialog.dismiss();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void progress(int progress) {
        if (dialog != null) dialog.setProgress(progress);
    }

    @Override
    public void error(String msg) {
        Notify.show(msg);
        dismiss();
    }

    @Override
    public void success(File file) {
        FileUtil.openFile(file);
        dismiss();
    }
}

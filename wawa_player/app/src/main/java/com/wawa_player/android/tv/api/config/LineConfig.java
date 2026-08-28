package com.wawa_player.android.tv.api.config;

import android.text.TextUtils;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.api.Decoder;
import com.wawa_player.android.tv.bean.Config;
import com.wawa_player.android.tv.bean.Depot;
import com.wawa_player.android.tv.server.Server;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.Task;
import com.wawa_player.android.tv.utils.UrlUtil;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Prefers;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;

public class LineConfig {

    private static String sourceKey(int type) {
        return "line_source_" + type;
    }

    private static String urlsKey(int type) {
        return "line_urls_" + type;
    }

    public static String getSource(int type) {
        return Prefers.getString(sourceKey(type));
    }

    public static boolean isMulti(int type) {
        return !TextUtils.isEmpty(getSource(type));
    }

    public static List<Depot> getLines(int type) {
        List<Depot> items = Depot.arrayFrom(Prefers.getString(urlsKey(type)));
        return items == null ? Collections.emptyList() : items;
    }

    public static void save(int type, String source, List<Depot> items) {
        Prefers.put(sourceKey(type), source);
        Prefers.put(urlsKey(type), App.gson().toJson(items));
    }

    public static void clear(int type) {
        Prefers.remove(sourceKey(type));
        Prefers.remove(urlsKey(type));
        for (Config c : Config.getAll(type)) {
            Config.delete(c.getUrl(), type);
        }
    }

    public static void refreshSync(int type) {
        String source = getSource(type);
        if (TextUtils.isEmpty(source)) return;
        try {
            clear(type);
            Server.get().start();
            String json = Decoder.getJson(UrlUtil.convert(source), "LineConfig", 15000);
            JsonObject object = Json.parse(json).getAsJsonObject();
            if (!object.has("urls")) throw new Exception("Line urls is empty");
            List<Depot> items = Depot.arrayFrom(object.getAsJsonArray("urls").toString());
            if (items.isEmpty()) throw new Exception("Line urls is empty");
            save(type, source, items);
        } catch (Throwable e) {
            e.printStackTrace();
            App.post(() -> Notify.show(R.string.line_update_fail));
        }
    }

    public static void refresh(int type) {
        Task.submit(() -> refreshSync(type));
    }
}

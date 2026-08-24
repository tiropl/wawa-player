package com.wawa_player.android.tv.server;

import com.wawa_player.android.tv.api.config.LiveConfig;
import com.wawa_player.android.tv.bean.Device;
import com.wawa_player.android.tv.server.impl.Process;
import com.wawa_player.android.tv.server.process.Action;
import com.wawa_player.android.tv.server.process.Cache;
import com.wawa_player.android.tv.server.process.Local;
import com.wawa_player.android.tv.server.process.Media;
import com.wawa_player.android.tv.server.process.Parse;
import com.wawa_player.android.tv.server.process.Proxy;
import com.github.catvod.utils.Asset;
import com.wawa_player.android.tv.setting.Setting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class Nano extends NanoHTTPD {

    private static final String INDEX = "index.html";

    private List<Process> process;

    public Nano(int port) {
        super(port);
        addProcess();
    }

    private void addProcess() {
        process = new ArrayList<>();
        process.add(new Action());
        process.add(new Cache());
        process.add(new Local());
        process.add(new Media());
        process.add(new Parse());
        process.add(new Proxy());
    }

    public static Response ok() {
        return ok("OK");
    }

    public static Response ok(String text) {
        return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, text);
    }

    public static Response error(String text) {
        return error(Response.Status.INTERNAL_ERROR, text);
    }

    public static Response error(Response.Status status, String text) {
        return newFixedLengthResponse(status, MIME_PLAINTEXT, text);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String url = session.getUri().trim();
        Map<String, String> files = new HashMap<>();
        if (session.getMethod() == Method.POST) parse(session, files);
        if (url.startsWith("/tvbus")) return ok(LiveConfig.getResp());
        if (url.startsWith("/device")) return ok(Device.get().toString());
        for (Process process : process) if (process.isRequest(session, url)) return process.doResponse(session, url, files);
        return getAssets(url.substring(1));
    }

    private void parse(IHTTPSession session, Map<String, String> files) {
        try {
            String ct = session.getHeaders().get("content-type");
            if (ct != null) session.getHeaders().put("content-type", ct.replace("multipart/form-data", "multipart/form-data; charset=utf-8"));
            session.parseBody(files);
        } catch (Exception ignored) {
        }
    }

    private static final String SCRIPT_JS = "js/script.js";

    private Response getAssets(String path) {
        try {
            if (path.isEmpty()) path = INDEX;
            if (path.equals(INDEX)) {
                return newFixedLengthResponse(Response.Status.OK, MIME_HTML, Asset.read(INDEX));
            }
            if (path.equals(SCRIPT_JS)) {
                String js = Asset.read(SCRIPT_JS).replace("/*{{ELDER_MODE}}*/false", String.valueOf(Setting.isElderMode()));
                return newFixedLengthResponse(Response.Status.OK, "application/javascript", js);
            }
            InputStream is = Asset.open(path);
            return newFixedLengthResponse(Response.Status.OK, getMimeTypeForFile(path), is, -1);
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML, null, 0);
        }
    }
}

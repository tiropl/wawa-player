package com.wawa_player.android.tv.api.config;

import android.text.TextUtils;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.api.loader.BaseLoader;
import com.wawa_player.android.tv.api.Decoder;
import com.wawa_player.android.tv.bean.Config;
import com.wawa_player.android.tv.bean.Depot;
import com.wawa_player.android.tv.bean.Parse;
import com.wawa_player.android.tv.bean.Rule;
import com.wawa_player.android.tv.bean.Site;
import com.wawa_player.android.tv.event.ConfigEvent;
import com.wawa_player.android.tv.event.RefreshEvent;
import com.wawa_player.android.tv.impl.Callback;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.Task;
import com.wawa_player.android.tv.utils.UrlUtil;
import com.github.catvod.bean.Doh;
import com.github.catvod.bean.Header;
import com.github.catvod.bean.Proxy;
import com.github.catvod.utils.Json;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VodConfig extends BaseConfig {

    private static final String TAG = VodConfig.class.getSimpleName();

    private Site home;
    private String wall;
    private Parse parse;
    private List<Doh> doh;
    private List<Rule> rules;
    private List<Site> sites;
    private List<String> ads;
    private List<String> flags;
    private List<Parse> parses;
    private boolean configuring;

    public static VodConfig get() {
        return Loader.INSTANCE;
    }

    public static int getCid() {
        return get().getConfig().getId();
    }

    public static String getUrl() {
        return get().getConfig().getUrl();
    }

    public static String getDesc() {
        return get().getConfig().getDesc();
    }

    public static int getHomeIndex() {
        return get().getSites().indexOf(get().getHome());
    }

    public static boolean hasParse() {
        return !get().getParses().isEmpty();
    }

    public static void load(Config config, Callback callback) {
        load(config, callback, false);
    }

    public static void load(Config config, Callback callback, boolean configuring) {
        get().clear().config(config).configuring(configuring).load(callback);
    }

    public static void switchLine(Depot line, Callback callback) {
        Snapshot snapshot = get().new Snapshot();
        get().silent(true);
        load(Config.find(line.getUrl(), line.getName(), VOD), new Callback() {
            @Override
            public void start() {
                callback.start();
            }

            @Override
            public void success() {
                get().silent(false);
                ConfigEvent.common();
                ConfigEvent.vod();
                callback.success();
            }

            @Override
            public void error(String msg) {
                get().silent(false);
                get().restore(snapshot);
                App.post(() -> Notify.show(R.string.line_load_fail));
                callback.error(msg);
            }
        });
    }

    public VodConfig init() {
        return config(Config.vod());
    }

    public void initAsync(java.util.function.Consumer<VodConfig> callback) {
        Task.submit(() -> {
            Config config = Config.vod();
            App.post(() -> {
                this.config = config;
                callback.accept(this);
            });
        });
    }

    public boolean loaded() {
        return isLoaded();
    }

    public VodConfig config(Config config) {
        this.config = config;
        return this;
    }

    public VodConfig configuring(boolean configuring) {
        this.configuring = configuring;
        return this;
    }

    public VodConfig clear() {
        ads = null;
        doh = null;
        home = null;
        wall = null;
        parse = null;
        sites = null;
        flags = null;
        rules = null;
        parses = null;
        configuring = false;
        BaseLoader.get().clear();
        RuleConfig.get().invalidate();
        return this;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected Config defaultConfig() {
        return Config.vod();
    }

    @Override
    protected void postEvent() {
        if (silent) return;
        super.postEvent();
        ConfigEvent.vod();
    }

    @Override
    protected void loadConfig(int id, Config config, Callback callback) {
        LineConfig.refreshSync(VOD);
        super.loadConfig(id, config, callback);
    }

    @Override
    protected void load(Config config) throws Throwable {
        String json = Decoder.getJson(UrlUtil.convert(config.getUrl()), getTag(), TIMEOUT);
        checkJson(config, Json.parse(json).getAsJsonObject());
    }

    @Override
    protected boolean isLoaded() {
        return !getSites().isEmpty();
    }

    private void checkJson(Config config, JsonObject object) throws Throwable {
        if (object.has("msg")) {
            throw new Exception(object.get("msg").getAsString());
        } else if (object.has("urls")) {
            parseDepot(config, object);
        } else {
            configuring = false;
            parseConfig(config, object);
        }
    }

    private void parseDepot(Config config, JsonObject object) throws Throwable {
        List<Depot> items = Depot.arrayFrom(object.getAsJsonArray("urls").toString());
        if (items.isEmpty()) throw new Exception("Depot urls is empty");
        configuring = false;
        Set<String> activeUrls = new HashSet<>();
        for (Depot d : items) activeUrls.add(d.getUrl());
        for (Config c : Config.getAll(VOD)) {
            if (!activeUrls.contains(c.getUrl())) Config.delete(c.getUrl(), VOD);
        }
        LineConfig.save(VOD, config.getUrl(), items);
        load(this.config = Config.find(items.get(0), VOD));
        Config.delete(config.getUrl());
    }

    private void parseConfig(Config config, JsonObject object) {
        initList(object);
        initLive(config, object);
        initWall(config, object);
        initSite(config, object);
        initParse(config, object);
        config.setLogo(Json.safeString(object, "logo"));
        config.setAssrt(Json.safeString(object, "assrt"));
        config.setNotice(Json.safeString(object, "notice"));
        config.setDanmaku(Json.safeString(object, "danmaku"));
    }

    private void initList(JsonObject object) {
        setHeaders(Header.arrayFrom(fetchArray(object, "headers")));
        setProxy(Proxy.arrayFrom(fetchArray(object, "proxy")));
        setRules(Rule.arrayFrom(fetchArray(object, "rules")));
        setDoh(Doh.arrayFrom(fetchArray(object, "doh")));
        setFlags(Json.safeListString(object, "flags"));
        setHosts(Json.safeListString(object, "hosts"));
        setAds(Json.safeListString(object, "ads"));
    }

    private void initLive(Config config, JsonObject object) {
        if (Json.isEmpty(object, "lives")) return;
        Config temp = Config.find(config, LIVE).save();
        boolean sync = LiveConfig.get().needSync(config.getUrl());
        if (sync) LiveConfig.get().config(temp.update()).parse(object);
    }

    private void initWall(Config config, JsonObject object) {
        if (Json.isEmpty(object, "wallpaper")) return;
        this.wall = Json.safeString(object, "wallpaper");
        Config temp = Config.find(wall, config.getName(), WALL).save();
        boolean sync = WallConfig.get().needSync(wall);
        if (sync) WallConfig.get().config(temp.update());
    }

    private void initSite(Config config, JsonObject object) {
        String spider = Json.safeString(object, "spider");
        BaseLoader.get().parseJar(spider, true);
        setSites(Json.safeListElement(object, "sites").stream().map(e -> Site.objectFrom(e, spider)).distinct().collect(Collectors.toCollection(ArrayList::new)));
        Map<String, Site> items = Site.findAll().stream().collect(Collectors.toMap(Site::getKey, Function.identity()));
        getSites().forEach(site -> site.sync(items.get(site.getKey())));
        setHome(config, getSites().isEmpty() ? new Site() : getSites().stream().filter(item -> item.getKey().equals(config.getHome())).findFirst().orElse(getSites().get(0)), false);
    }

    private void initParse(Config config, JsonObject object) {
        setParses(Json.safeListElement(object, "parses").stream().map(Parse::objectFrom).distinct().collect(Collectors.toCollection(ArrayList::new)));
        setParse(config, getParses().isEmpty() ? new Parse() : getParses().stream().filter(item -> item.getName().equals(config.getParse())).findFirst().orElse(getParses().get(0)), false);
    }

    public List<Site> getSites() {
        return sites == null ? Collections.emptyList() : sites;
    }

    private void setSites(List<Site> sites) {
        this.sites = sites;
    }

    public List<Parse> getParses() {
        return parses == null ? Collections.emptyList() : parses;
    }

    private void setParses(List<Parse> parses) {
        if (!parses.isEmpty()) parses.add(0, Parse.god());
        this.parses = parses;
    }

    public List<Doh> getDoh() {
        List<Doh> items = Doh.get(App.get());
        if (doh == null) return items;
        items.removeAll(doh);
        items.addAll(doh);
        return items;
    }

    private void setDoh(List<Doh> doh) {
        this.doh = doh;
    }

    public List<Rule> getRules() {
        return rules == null ? Collections.emptyList() : rules;
    }

    private void setRules(List<Rule> rules) {
        this.rules = rules;
        RuleConfig.get().invalidate();
    }

    public List<Parse> getParses(int type) {
        return getParses().stream().filter(item -> item.getType() == type).toList();
    }

    public List<Parse> getParses(int type, String flag) {
        List<Parse> items = getParses(type);
        List<Parse> filter = items.stream().filter(item -> item.getExt().getFlag().contains(flag)).toList();
        return filter.isEmpty() ? items : filter;
    }

    public List<String> getFlags() {
        return flags == null ? Collections.emptyList() : flags;
    }

    private void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public List<String> getAds() {
        return ads == null ? Collections.emptyList() : ads;
    }

    private void setAds(List<String> ads) {
        this.ads = ads;
        RuleConfig.get().invalidate();
    }

    public Parse getParse() {
        return parse == null ? new Parse() : parse;
    }

    public void setParse(Parse parse) {
        setParse(getConfig(), parse, true);
    }

    public Site getHome() {
        return home == null ? new Site() : home;
    }

    public void setHome(Site site) {
        setHome(getConfig(), site, true);
        RefreshEvent.home();
    }

    public String getWall() {
        return TextUtils.isEmpty(wall) ? "" : wall;
    }

    public Parse getParse(String name) {
        return getParses().stream().filter(item -> item.getName().equals(name)).findFirst().orElse(new Parse());
    }

    public Site getSite(String key) {
        return getSites().stream().filter(item -> item.getKey().equals(key)).findFirst().orElse(new Site());
    }

    private void setParse(Config config, Parse parse, boolean save) {
        this.parse = parse;
        this.parse.setSelected(true);
        config.setParse(parse.getName());
        getParses().forEach(item -> item.setSelected(parse));
        if (save) config.save();
    }

    private void setHome(Config config, Site site, boolean save) {
        home = site;
        home.setSelected(true);
        config.setHome(home.getKey());
        if (save) config.save();
        getSites().forEach(item -> item.setSelected(home));
    }

    private void restore(Snapshot snapshot) {
        this.config = snapshot.config;
        this.home = snapshot.home;
        this.wall = snapshot.wall;
        this.parse = snapshot.parse;
        this.doh = snapshot.doh;
        this.rules = snapshot.rules;
        this.sites = snapshot.sites;
        this.ads = snapshot.ads;
        this.flags = snapshot.flags;
        this.parses = snapshot.parses;
        RuleConfig.get().invalidate();
        Task.execute(() -> BaseLoader.get().parseJar(snapshot.jar, true));
    }

    private class Snapshot {

        private final Config config;
        private final Site home;
        private final String wall;
        private final Parse parse;
        private final List<Doh> doh;
        private final List<Rule> rules;
        private final List<Site> sites;
        private final List<String> ads;
        private final List<String> flags;
        private final List<Parse> parses;
        private final String jar;

        private Snapshot() {
            this.config = getConfig();
            this.home = getHome();
            this.wall = VodConfig.this.wall;
            this.parse = getParse();
            this.doh = VodConfig.this.doh;
            this.rules = getRules();
            this.sites = getSites();
            this.ads = getAds();
            this.flags = getFlags();
            this.parses = getParses();
            this.jar = home.getJar();
        }
    }

    private static class Loader {
        static volatile VodConfig INSTANCE = new VodConfig();
    }
}

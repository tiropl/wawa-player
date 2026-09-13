package com.wawa_player.android.tv.spider;

/**
 * 主进程与 spider 独立进程之间的通信常量契约。
 * <p>
 * 集中放一份，避免两侧各写各的字符串导致对不上。
 */
public final class SpiderProtocol {

    private SpiderProtocol() {
    }

    /** 独立进程名（对应 AndroidManifest 里 service 的 android:process）。 */
    public static final String PROCESS_NAME = ":spider";

    /** Spider 方法名，对应 ISpiderService.call() 的 method 参数。 */
    public interface Method {
        String INIT = "init";
        String HOME_CONTENT = "homeContent";
        String HOME_VIDEO_CONTENT = "homeVideoContent";
        String CATEGORY_CONTENT = "categoryContent";
        String DETAIL_CONTENT = "detailContent";
        String SEARCH_CONTENT = "searchContent";
        String SEARCH_CONTENT_PG = "searchContentPg";
        String PLAYER_CONTENT = "playerContent";
        String LIVE_CONTENT = "liveContent";
        String MANUAL_VIDEO_CHECK = "manualVideoCheck";
        String IS_VIDEO_FORMAT = "isVideoFormat";
        String PROXY = "proxy";
        String ACTION = "action";
        String DESTROY = "destroy";
    }

    /** call() 的 args 键。 */
    public interface Arg {
        String EXTEND = "extend";
        String FILTER = "filter";
        String TID = "tid";
        String PG = "pg";
        String KEY = "key";
        String QUICK = "quick";
        String FLAG = "flag";
        String ID = "id";
        String URL = "url";
        String ACTION = "action";
        String IDS = "ids";          // StringArrayList
        String VIP_FLAGS = "vipFlags"; // StringArrayList
        String EXTEND_MAP = "extendMap"; // Bundle（HashMap<String,String>）
        String PARAMS = "params";      // Bundle（Map<String,String>）
    }

    /** call() 返回的 Bundle 键。 */
    public interface Result {
        /** int，见 Code。 */
        String CODE = "code";
        /** String，失败原因。 */
        String ERROR = "error";
        /** String，见 Kind。 */
        String KIND = "kind";
        /** String，Kind=STRING 时的返回值。 */
        String VALUE = "value";
        /** boolean，Kind=BOOLEAN 时的返回值。 */
        String FLAG = "flag";
        /** long，任务实际耗时(ms)。 */
        String COST_MS = "costMs";

        /* ---- Kind=PROXY 专用 ---- */
        /** int，HTTP 状态码。 */
        String STATUS = "status";
        /** String，Content-Type。 */
        String MIME = "mime";
        /** Bundle，响应头。 */
        String HEADERS = "headers";
        /** ParcelFileDescriptor，响应体管道（读端）。 */
        String PFD = "pfd";
    }

    /** 返回码。 */
    public interface Code {
        int OK = 0;
        int ERR_UNKNOWN = -1;
        int ERR_BAD_ARGS = -2;
        int ERR_NO_SPIDER = -3;
        int ERR_PAUSED = -4;
        int ERR_TIMEOUT = -5;
        int ERR_INTERRUPTED = -6;
        int ERR_BUSY = -7;
        int ERR_MEMORY = -8;
        int ERR_BLOCKED_HOST = -9;
        int ERR_DEAD = -10;
    }

    /** 返回值类型。 */
    public interface Kind {
        String VOID = "void";
        String STRING = "string";
        String BOOLEAN = "boolean";
        String PROXY = "proxy";
    }

    /** 运行状态，对应 ISpiderCallback.onStateChanged 的 state。 */
    public interface State {
        int IDLE = 1;
        int RUNNING = 2;
        int PAUSED = 3;
        int STOPPED = 4;
        int TIMEOUT = 5;
        int CRASHED = 6;
        int OOM = 7;
    }

    /** 资源与行为限制策略键（setPolicy 的 Bundle）。 */
    public interface Policy {
        /** long，单任务超时(ms)，默认 20000。 */
        String TIMEOUT_MS = "timeoutMs";
        /** int，进程内存水位上限(MB)，超过则拒绝新任务并清理，默认 256。 */
        String MAX_MEMORY_MB = "maxMemoryMb";
        /** int，proxy 响应体上限(byte)，超过则截断，默认 32MB。 */
        String MAX_BODY_BYTES = "maxBodyBytes";
        /** StringArrayList，主机白名单（为空表示不限制）。 */
        String ALLOW_HOSTS = "allowHosts";
        /** StringArrayList，主机黑名单。 */
        String DENY_HOSTS = "denyHosts";
        /** int，并发任务上限，默认 8。 */
        String MAX_TASKS = "maxTasks";
    }

    /** status() 返回的 Bundle 键。 */
    public interface Status {
        String PID = "pid";
        String ALIVE_TASKS = "aliveTasks";
        String SPIDERS = "spiders";      // String[]
        String MEMORY_MB = "memoryMb";
        String POLICY = "policy";        // Bundle
        String UPTIME_MS = "uptimeMs";
        String TOTAL_TASKS = "totalTasks";
        String FAILED_TASKS = "failedTasks";
    }

    /** 策略默认值。 */
    public static final long DEFAULT_TIMEOUT_MS = 20_000L;
    public static final int DEFAULT_MAX_MEMORY_MB = 256;
    public static final int DEFAULT_MAX_BODY_BYTES = 32 * 1024 * 1024;
    public static final int DEFAULT_MAX_TASKS = 8;

    public static String stateName(int state) {
        switch (state) {
            case State.RUNNING: return "RUNNING";
            case State.PAUSED: return "PAUSED";
            case State.STOPPED: return "STOPPED";
            case State.TIMEOUT: return "TIMEOUT";
            case State.CRASHED: return "CRASHED";
            case State.OOM: return "OOM";
            case State.IDLE:
            default: return "IDLE";
        }
    }
}

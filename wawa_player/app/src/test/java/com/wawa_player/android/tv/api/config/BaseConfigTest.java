package com.wawa_player.android.tv.api.config;

import com.google.common.truth.Truth;
import com.wawa_player.android.tv.bean.Config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class BaseConfigTest {

    private BaseConfig baseConfig;

    private static class TestConfig extends BaseConfig {
        @Override protected String getTag() { return "Test"; }
        @Override protected Config defaultConfig() { return new Config(); }
        @Override protected void load(Config config) {}
        @Override protected boolean isLoaded() { return true; }
    }

    @Before
    public void setUp() {
        baseConfig = new TestConfig();
    }

    @Test
    public void isCanceled_canceled_message() {
        Truth.assertThat(baseConfig.isCanceled(new RuntimeException("Canceled"))).isTrue();
    }

    @Test
    public void isCanceled_interrupted_exception() {
        Truth.assertThat(baseConfig.isCanceled(new InterruptedException())).isTrue();
    }

    @Test
    public void isCanceled_interrupted_io_exception() {
        Truth.assertThat(baseConfig.isCanceled(new InterruptedIOException())).isTrue();
    }

    @Test
    public void isCanceled_cause_interrupted_io() {
        Truth.assertThat(baseConfig.isCanceled(new RuntimeException(new InterruptedIOException()))).isTrue();
    }

    @Test
    public void isCanceled_timeout_returns_false() {
        Truth.assertThat(baseConfig.isCanceled(new SocketTimeoutException())).isFalse();
    }

    @Test
    public void isCanceled_timeout_message_returns_false() {
        Truth.assertThat(baseConfig.isCanceled(new RuntimeException("timeout"))).isFalse();
    }

    @Test
    public void isCanceled_cause_timeout_returns_false() {
        Truth.assertThat(baseConfig.isCanceled(new RuntimeException(new SocketTimeoutException()))).isFalse();
    }

    @Test
    public void isCanceled_normal_exception() {
        Truth.assertThat(baseConfig.isCanceled(new RuntimeException("normal"))).isFalse();
    }

    @Test
    public void needSync_when_sync_flag_set() {
        baseConfig.sync = true;
        Truth.assertThat(baseConfig.needSync("http://any")).isTrue();
    }

    @Test
    public void needSync_when_config_null() {
        baseConfig.config = null;
        Truth.assertThat(baseConfig.needSync("http://any")).isTrue();
    }

    @Test
    public void needSync_when_url_empty() {
        baseConfig.config = new Config();
        baseConfig.config.setUrl("");
        Truth.assertThat(baseConfig.needSync("http://any")).isTrue();
    }

    @Test
    public void needSync_same_url() {
        baseConfig.config = new Config();
        baseConfig.config.setUrl("http://same");
        Truth.assertThat(baseConfig.needSync("http://same")).isTrue();
    }

    @Test
    public void needSync_different_url() {
        baseConfig.config = new Config();
        baseConfig.config.setUrl("http://old");
        Truth.assertThat(baseConfig.needSync("http://new")).isFalse();
    }

    @Test
    public void getConfig_returns_default_when_null() {
        baseConfig.config = null;
        Truth.assertThat(baseConfig.getConfig()).isNotNull();
    }

    @Test
    public void getConfig_returns_actual() {
        Config c = new Config();
        c.setUrl("http://test");
        baseConfig.config = c;
        Truth.assertThat(baseConfig.getConfig().getUrl()).isEqualTo("http://test");
    }

    @Test
    public void silent_setter() {
        baseConfig.silent(true);
        Truth.assertThat(baseConfig.silent).isTrue();
        baseConfig.silent(false);
        Truth.assertThat(baseConfig.silent).isFalse();
    }
}

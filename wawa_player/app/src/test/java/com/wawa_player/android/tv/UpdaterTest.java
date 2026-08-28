package com.wawa_player.android.tv;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class UpdaterTest {

    @Test
    public void getVersionCodeWeighted() {
        assertThat(Updater.getVersionCode("0.6.7")).isEqualTo(607);
        assertThat(Updater.getVersionCode("1.0.0")).isEqualTo(10000);
        assertThat(Updater.getVersionCode("1.0.10")).isEqualTo(10010);
        assertThat(Updater.getVersionCode("2.0.0")).isEqualTo(20000);
        assertThat(Updater.getVersionCode("1.2.3")).isEqualTo(10203);
    }

    @Test
    public void getVersionCodeMajorDominates() {
        assertThat(Updater.getVersionCode("2.0.0")).isGreaterThan(Updater.getVersionCode("1.99.99"));
        assertThat(Updater.getVersionCode("1.1.0")).isGreaterThan(Updater.getVersionCode("1.0.99"));
    }

    @Test
    public void getVersionCodeDefaultsMissingParts() {
        assertThat(Updater.getVersionCode("1")).isEqualTo(10000);
        assertThat(Updater.getVersionCode("1.2")).isEqualTo(10200);
        assertThat(Updater.getVersionCode("0.0.1")).isEqualTo(1);
    }

    @Test
    public void getVersionCodeEqualVersions() {
        assertThat(Updater.getVersionCode("0.6.7")).isEqualTo(Updater.getVersionCode("0.6.7"));
        assertThat(Updater.getVersionCode("2.0.0")).isEqualTo(Updater.getVersionCode("2.0.0"));
    }

    @Test
    public void getVersionExtractsSemverFromTag() {
        assertThat(Updater.getVersion("v0.6.7")).isEqualTo("0.6.7");
        assertThat(Updater.getVersion("v2.0.0-beta")).isEqualTo("2.0.0");
        assertThat(Updater.getVersion("release-1.0.10")).isEqualTo("1.0.10");
    }

    @Test
    public void getVersionReturnsEmptyForNoMatch() {
        assertThat(Updater.getVersion("latest")).isEmpty();
        assertThat(Updater.getVersion("")).isEmpty();
    }
}

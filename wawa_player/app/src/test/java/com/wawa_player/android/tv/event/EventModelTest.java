package com.wawa_player.android.tv.event;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class EventModelTest {

    @Test
    public void configEventRecordsExposeTypePredicates() {
        ConfigEvent vod = new ConfigEvent(ConfigEvent.Type.VOD);
        ConfigEvent live = new ConfigEvent(ConfigEvent.Type.LIVE);
        ConfigEvent common = new ConfigEvent(ConfigEvent.Type.COMMON);

        assertThat(vod.isVod()).isTrue();
        assertThat(vod.isLive()).isFalse();
        assertThat(live.isLive()).isTrue();
        assertThat(live.isVod()).isFalse();
        assertThat(common.type()).isEqualTo(ConfigEvent.Type.COMMON);
        assertThat(new ConfigEvent(ConfigEvent.Type.VOD)).isEqualTo(vod);
    }

    @Test
    public void stateEventRecordExposesAllStateTypes() {
        assertThat(new StateEvent(StateEvent.Type.EMPTY).type()).isEqualTo(StateEvent.Type.EMPTY);
        assertThat(new StateEvent(StateEvent.Type.PROGRESS).type()).isEqualTo(StateEvent.Type.PROGRESS);
        assertThat(new StateEvent(StateEvent.Type.CONTENT).type()).isEqualTo(StateEvent.Type.CONTENT);
        assertThat(new StateEvent(StateEvent.Type.EMPTY)).isNotEqualTo(new StateEvent(StateEvent.Type.CONTENT));
    }
}

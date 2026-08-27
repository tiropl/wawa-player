package com.wawa_player.android.tv.playback.vod;

import static org.junit.Assert.assertEquals;

import androidx.media3.common.C;

import com.wawa_player.android.tv.bean.History;

import org.junit.Test;

public class VodHistoryPolicyTest {
    @Test
    public void startPositionUsesLargestOpeningOrPosition() {
        VodHistoryPolicy policy = new VodHistoryPolicy();
        assertEquals(C.TIME_UNSET, policy.startPositionMs(null));

        History history = new History();
        assertEquals(C.TIME_UNSET, policy.startPositionMs(history));
        history.setOpening(100);
        history.setPosition(250);
        assertEquals(250, policy.startPositionMs(history));
        history.setOpening(400);
        assertEquals(400, policy.startPositionMs(history));
    }

    @Test
    public void updateProgressIgnoresInvalidValuesAndUpdatesValidValues() {
        VodHistoryPolicy policy = new VodHistoryPolicy();
        History history = new History();
        long initialTime = history.getCreateTime();
        long initialPosition = history.getPosition();
        long initialDuration = history.getDuration();

        policy.updateProgress(history, 10, -1, 100);
        assertEquals(initialTime, history.getCreateTime());
        assertEquals(initialPosition, history.getPosition());
        assertEquals(initialDuration, history.getDuration());
        policy.updateProgress(history, 10, 20, 0);
        assertEquals(initialPosition, history.getPosition());

        policy.updateProgress(history, 30, 20, 100);
        assertEquals(30, history.getCreateTime());
        assertEquals(20, history.getPosition());
        assertEquals(100, history.getDuration());
    }
}

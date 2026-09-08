package com.wawa_player.android.tv.player.media;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import androidx.media3.common.C;
import androidx.media3.common.MediaMetadata;

import com.wawa_player.android.tv.bean.Danmaku;
import com.wawa_player.android.tv.bean.Result;
import com.wawa_player.android.tv.bean.Sub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class PlaySpecTest {
    @Test
    public void fromInitializesDanmakuSelectionAndUnmodifiableView() {
        Danmaku first = Danmaku.from("https://one");
        Danmaku second = Danmaku.from("https://two");
        second.setSelected(true);
        Result result = Result.empty();
        result.getDanmaku().add(first);
        result.getDanmaku().add(second);
        PlaySpec spec = PlaySpec.from(result, "key", null);
        assertEquals(2, spec.getDanmakus().size());
        spec.addDanmaku(first);
        spec.addDanmaku(second);
        assertSame(first, spec.getSelectedDanmaku());
        assertEquals(2, spec.getDanmakus().size());
        assertThrowsUnsupported(() -> spec.getDanmakus().clear());
        spec.toggleDanmaku(first);
        assertNull(spec.getSelectedDanmaku());
        spec.toggleDanmaku(second);
        assertSame(second, spec.getSelectedDanmaku());
    }

    @Test
    public void danmakuSelectionRejectsEmptyAndDuplicateValues() {
        PlaySpec spec = PlaySpec.from("key", "https://video", new HashMap<>(), null);
        Danmaku empty = Danmaku.from("");
        Danmaku item = Danmaku.from("https://one");
        spec.addDanmaku(empty);
        spec.addDanmaku(item);
        spec.addDanmaku(Danmaku.from("https://one"));
        assertEquals(1, spec.getDanmakus().size());
        spec.selectDanmaku(Danmaku.from("https://new"));
        assertEquals("https://new", spec.getSelectedDanmaku().getUrl());
    }

    @Test
    public void setSubPlacesNewSubtitleFirstAndClearsForcedFlags() {
        Sub forced = Sub.from("forced.srt", "https://forced", "en", "text/srt");
        forced.setFlag(C.SELECTION_FLAG_FORCED);
        Sub selected = Sub.from("selected.vtt", "https://selected", "en", "text/vtt");
        PlaySpec spec = PlaySpec.from("key", "https://video", null, null);
        spec.setSub(forced);
        spec.setSub(selected);
        assertSame(selected, spec.getSubs().get(0));
        assertEquals(C.SELECTION_FLAG_AUTOSELECT, forced.getRawFlag());
    }

    @Test
    public void fromParseKeepsResultPropertiesWithoutUrl() {
        Result result = Result.empty();
        result.setFormat("application/dash+xml");
        PlaySpec spec = PlaySpec.fromParse(result, "key", new MediaMetadata.Builder().setTitle("title").build());
        assertEquals("key", spec.getKey());
        assertNull(spec.getUrl());
        assertEquals("application/dash+xml", spec.getFormat());
        assertTrue(spec.getDanmakus().isEmpty());
    }

    private static void assertThrowsUnsupported(Runnable action) {
        try {
            action.run();
            throw new AssertionError("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }
}

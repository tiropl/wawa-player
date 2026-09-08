package com.wawa_player.android.tv.player.extractor;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class ExtractorMatchTest {

    // --- YouTube.Parser.match ---

    @Test
    public void youtubeParser_matchesYoutubeComWithList() {
        assertThat(YouTube.Parser.match("https://www.youtube.com/watch?v=abc&list=PLxyz")).isTrue();
    }

    @Test
    public void youtubeParser_matchesYoutuBeWithList() {
        assertThat(YouTube.Parser.match("https://youtu.be/abc?list=PLxyz")).isTrue();
    }

    @Test
    public void youtubeParser_doesNotMatchYoutubeWithoutList() {
        assertThat(YouTube.Parser.match("https://www.youtube.com/watch?v=abc")).isFalse();
    }

    @Test
    public void youtubeParser_doesNotMatchUnrelatedUrl() {
        assertThat(YouTube.Parser.match("https://example.com/video")).isFalse();
    }

    // --- Thunder.Parser.match ---

    @Test
    public void thunderParser_matchesMagnetLink() {
        assertThat(Thunder.Parser.match("magnet:?xt=urn:btih:abc123")).isTrue();
    }

    @Test
    public void thunderParser_matchesEd2kLink() {
        assertThat(Thunder.Parser.match("ed2k://|file|example.avi|12345|abc123|/")).isTrue();
    }

    @Test
    public void thunderParser_matchesTorrentUrl() {
        assertThat(Thunder.Parser.match("https://example.com/file.torrent")).isTrue();
    }

    @Test
    public void thunderParser_matchesTorrentUrlCaseInsensitive() {
        assertThat(Thunder.Parser.match("https://example.com/file.TORRENT")).isTrue();
    }

    @Test
    public void thunderParser_doesNotMatchPlainHttp() {
        assertThat(Thunder.Parser.match("https://example.com/video.mp4")).isFalse();
    }

    @Test
    public void thunderParser_matchesThunderLink() {
        assertThat(Thunder.Parser.match("thunder://abc123")).isTrue();
    }

    // --- Thunder.Parser.isTorrent (indirect via match) ---

    @Test
    public void torrentFile_detectedByMatch() {
        // isTorrent checks: !url.startsWith("magnet") && url.split(";")[0].toLowerCase().endsWith(".torrent")
        assertThat(Thunder.Parser.match("http://example.com/video.torrent;params")).isTrue();
    }
}

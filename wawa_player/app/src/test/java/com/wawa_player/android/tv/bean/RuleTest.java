package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class RuleTest {

    @Test
    public void parsesRuleCollectionsAndNullFieldsAsEmpty() {
        java.util.List<Rule> rules = Rule.arrayFrom(JsonParser.parseString("[{\"name\":\"Video\",\"hosts\":[\"example.com\"],\"regex\":[\"m3u8\"],\"script\":[\"close()\"],\"exclude\":[\"preview\"]},{\"name\":\"Empty\"}]"));

        assertThat(rules).hasSize(2);
        assertThat(rules.get(0).getHosts()).containsExactly("example.com");
        assertThat(rules.get(0).getRegex()).containsExactly("m3u8");
        assertThat(rules.get(0).getScript()).containsExactly("close()");
        assertThat(rules.get(0).getExclude()).containsExactly("preview");
        assertThat(rules.get(1).getHosts()).isEmpty();
        assertThat(rules.get(1).getRegex()).isEmpty();
    }

    @Test
    public void rulesCompareByName() {
        assertThat(Rule.create("same")).isEqualTo(Rule.create("same"));
        assertThat(Rule.create("same")).isNotEqualTo(Rule.create("other"));
        assertThat(Rule.empty().getName()).isEmpty();
    }
}

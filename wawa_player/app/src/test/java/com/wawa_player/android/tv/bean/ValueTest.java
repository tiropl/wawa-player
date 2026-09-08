package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class ValueTest {

    @Test
    public void createsValuesWithOptionalNamesAndTrimmedValues() {
        Value named = Value.create("Primary", " https://example/live ");
        Value unnamed = Value.create(" https://example/live ");

        assertThat(named.getN()).isEqualTo("Primary");
        assertThat(named.getV()).isEqualTo("https://example/live");
        assertThat(unnamed.getN()).isEmpty();
        assertThat(unnamed.getV()).isEqualTo("https://example/live");
    }

    @Test
    public void copiesAndComparesValuesByValue() {
        Value first = Value.create("One", "url");
        Value same = Value.create("Two", "url");
        Value different = Value.create("Other", "other");

        assertThat(first).isEqualTo(same);
        assertThat(first.hashCode()).isEqualTo(same.hashCode());
        assertThat(first).isNotEqualTo(different);
        assertThat(first.copy()).isEqualTo(first);
    }

    @Test
    public void selectedStateTogglesForMatchingValue() {
        Value first = Value.create("url");
        Value same = Value.create("url");

        first.setSelected(same);
        assertThat(first.isSelected()).isTrue();
        first.setSelected(same);
        assertThat(first.isSelected()).isFalse();
    }
}

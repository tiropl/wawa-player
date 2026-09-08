package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class StyleTest {

    @Test
    public void factoryStylesProvideExpectedTypesAndRatios() {
        assertThat(Style.rect().getType()).isEqualTo("rect");
        assertThat(Style.rect().getRatio()).isEqualTo(0.75f);
        assertThat(Style.list().isList()).isTrue();
        assertThat(Style.get(1, 0, 0).getRatio()).isEqualTo(1.33f);
        assertThat(Style.get(0, 1, 0).getRatio()).isEqualTo(1.0f);
        assertThat(Style.get(0, 0, 1)).isNull();
    }

    @Test
    public void normalizesRatiosAndIdentifiesLandscapeStyles() {
        Style oval = new Style("oval", 0);
        Style oversized = new Style("rect", 10);
        Style unknown = new Style((String) null);

        assertThat(oval.getRatio()).isEqualTo(1.0f);
        assertThat(oval.isOval()).isTrue();
        assertThat(oversized.getRatio()).isEqualTo(4.0f);
        assertThat(oversized.isLand()).isTrue();
        assertThat(unknown.getType()).isEqualTo("rect");
        assertThat(unknown.getRatio()).isEqualTo(0.75f);
    }

    @Test
    public void stylesCompareByEffectiveTypeAndRatio() {
        assertThat(new Style("rect", 0)).isEqualTo(Style.rect());
        assertThat(new Style("oval", 1)).isNotEqualTo(Style.rect());
    }
}

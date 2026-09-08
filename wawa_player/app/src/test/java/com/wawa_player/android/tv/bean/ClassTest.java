package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class ClassTest {

    @Test
    public void objectFromReadsAliasesAndDefaultsOptionalValues() {
        Class item = Class.objectFrom("{\"id\":\"movie\",\"name\":\"Movies\",\"type_flag\":\"1\"}");

        assertThat(item.getTypeId()).isEqualTo("movie");
        assertThat(item.getTypeName()).isEqualTo("Movies");
        assertThat(item.getTypeFlag()).isEqualTo("1");
        assertThat(item.isFolder()).isTrue();
        assertThat(item.getFilters()).isEmpty();
        assertThat(item.getLand()).isEqualTo(0);
        assertThat(item.getCircle()).isEqualTo(0);
        assertThat(item.getRatio()).isEqualTo(0F);
    }

    @Test
    public void equalityAndContentUseCategoryIdentityAndDisplayFields() {
        Class first = Class.objectFrom("{\"type_id\":\"1\",\"type_name\":\"Movies\",\"type_flag\":\"0\"}");
        Class sameId = Class.objectFrom("{\"type_id\":\"1\",\"type_name\":\"Changed\",\"type_flag\":\"1\"}");
        Class other = Class.objectFrom("{\"type_id\":\"2\",\"type_name\":\"Movies\",\"type_flag\":\"0\"}");

        assertThat(first).isEqualTo(sameId);
        assertThat(first.isSameItem(sameId)).isTrue();
        assertThat(first.isSameContent(sameId)).isFalse();
        assertThat(first).isNotEqualTo(other);
        assertThat(Class.objectFrom("{\"type_id\":\"home\"}").isHome()).isTrue();
    }
}

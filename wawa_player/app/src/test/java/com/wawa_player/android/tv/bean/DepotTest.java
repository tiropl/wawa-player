package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class DepotTest {

    @Test
    public void parsesDepotsAndFallsBackToUrlForMissingName() {
        java.util.List<Depot> depots = Depot.arrayFrom("[{\"url\":\"https://one.example\"},{\"url\":\"https://two.example\",\"name\":\"Two\"}]");

        assertThat(depots).hasSize(2);
        assertThat(depots.get(0).getUrl()).isEqualTo("https://one.example");
        assertThat(depots.get(0).getName()).isEqualTo("https://one.example");
        assertThat(depots.get(1).getName()).isEqualTo("Two");
    }

    @Test
    public void nullArrayReturnsEmptyList() {
        assertThat(Depot.arrayFrom("null")).isEmpty();
        assertThat(Depot.arrayFrom("[]")).isEmpty();
    }
}

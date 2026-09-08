package com.wawa_player.android.tv.bean;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class GroupTest {

    @Test
    public void constructorParsesHiddenGroupNameAndInitializesPosition() {
        Group group = new Group("Sports_password");

        assertThat(group.getName()).isEqualTo("Sports");
        assertThat(group.getPass()).isEqualTo("password");
        assertThat(group.isHidden()).isTrue();
        assertThat(group.getPosition()).isEqualTo(-1);
        assertThat(group.isEmpty()).isTrue();
    }

    @Test
    public void addMergesDuplicateChannelsAndFindAddsMissingChannel() {
        Group group = new Group("News");
        group.add(Channel.create("CNN"));
        Channel duplicate = Channel.create("CNN");
        duplicate.getUrls().add("second");
        group.add(duplicate);

        assertThat(group.getChannel()).hasSize(1);
        assertThat(group.getChannel().get(0).getUrls()).containsExactly("second");
        Channel added = group.find(Channel.create("BBC"));
        assertThat(added.getName()).isEqualTo("BBC");
        assertThat(group.find("BBC")).isEqualTo(1);
    }

    @Test
    public void arrayFromLoadsChannelsAndEqualityIncludesChannelCount() {
        Group group = Group.arrayFrom("[{\"name\":\"News\",\"channel\":[{\"name\":\"CNN\"}]}]").get(0);

        assertThat(group.getName()).isEqualTo("News");
        assertThat(group.getChannel()).hasSize(1);
        assertThat(group).isNotEqualTo(new Group("News"));
        Group sameSize = new Group("News");
        sameSize.setChannel(List.of(Channel.create("Other")));
        assertThat(group).isEqualTo(sameSize);
    }
}

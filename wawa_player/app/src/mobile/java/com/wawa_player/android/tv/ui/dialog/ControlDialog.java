package com.wawa_player.android.tv.ui.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.bean.Parse;
import com.wawa_player.android.tv.databinding.ActivityVideoBinding;
import com.wawa_player.android.tv.databinding.DialogControlBinding;
import com.wawa_player.android.tv.playback.PlaybackAction;
import com.wawa_player.android.tv.player.PlayerManager;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.setting.SpeedSetting;
import com.wawa_player.android.tv.ui.adapter.ParseAdapter;
import com.wawa_player.android.tv.ui.custom.SpaceItemDecoration;
import com.wawa_player.android.tv.utils.ResUtil;
import com.wawa_player.android.tv.utils.SliderUtil;
import com.wawa_player.android.tv.utils.Timer;
import com.wawa_player.android.tv.utils.Util;

import java.util.Arrays;
import java.util.List;

public final class ControlDialog {

    private ActivityVideoBinding parent;
    private PlayerManager player;
    private boolean parse;

    public static ControlDialog create() {
        return new ControlDialog();
    }

    public ControlDialog parent(ActivityVideoBinding parent) {
        this.parent = parent;
        return this;
    }

    public ControlDialog parse(boolean parse) {
        this.parse = parse;
        return this;
    }

    public ControlDialog player(PlayerManager player) {
        this.player = player;
        return this;
    }

    public void show(FragmentActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        for (Fragment fragment : manager.getFragments()) if (fragment instanceof BottomSheet || fragment instanceof SideSheet) return;
        if (Util.isFullscreen(activity)) new SideSheet(parent, parse, player).show(manager, null);
        else new BottomSheet(parent, parse, player).show(manager, null);
    }

    public interface Listener {

        void onScale(int tag);

        void onParse(Parse item);
    }

    private static void init(DialogFragment dialog, DialogControlBinding b, ActivityVideoBinding p, PlayerManager player, boolean parse, ParseAdapter.OnClickListener listener) {
        FragmentActivity activity = dialog.requireActivity();
        String[] scale = ResUtil.getStringArray(R.array.select_scale);
        List<TextView> scales = Arrays.asList(b.scale0, b.scale1, b.scale2, b.scale3, b.scale4);
        b.decode.setText(p.control.action.decode.getText());
        b.ending.setText(p.control.action.ending.getText());
        b.opening.setText(p.control.action.opening.getText());
        b.repeat.setSelected(p.control.action.repeat.isSelected());
        b.timer.setSelected(Timer.get().isRunning());
        SpeedSetting.setup(b.speed);
        setMediaOptionVisible(b, player);
        setTrackVisible(b, p);
        setScaleText(b, p, scales, scale);
        setPlayer(b, p, player);
        setParse(b, parse, listener);
        b.info.setOnClickListener(v -> dismiss(dialog, p.control.info));
        b.timer.setOnClickListener(v -> onTimer(dialog));
        b.speed.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) SpeedSetting.putPlayback(player.setSpeed(value));
        });
        for (TextView view : scales) view.setOnClickListener(v -> setScale(v, scales, activity));
        b.text.setOnClickListener(v -> dismiss(dialog, p.control.action.text));
        b.audio.setOnClickListener(v -> dismiss(dialog, p.control.action.audio));
        b.video.setOnClickListener(v -> dismiss(dialog, p.control.action.video));
        b.player.setOnClickListener(v -> dismiss(dialog, p.control.action.player));
        b.danmaku.setOnClickListener(v -> dismiss(dialog, p.control.action.danmakuSetting));
        b.edition.setOnClickListener(v -> dismiss(dialog, p.control.action.edition));
        b.chapter.setOnClickListener(v -> dismiss(dialog, p.control.action.chapter));
        b.repeat.setOnClickListener(v -> active(b.repeat, p.control.action.repeat));
        b.decode.setOnClickListener(v -> click(b.decode, p.control.action.decode));
        b.ending.setOnClickListener(v -> click(b.ending, p.control.action.ending));
        b.opening.setOnClickListener(v -> click(b.opening, p.control.action.opening));
        b.player.setOnLongClickListener(v -> longClick(b.player, p.control.action.player));
        b.ending.setOnLongClickListener(v -> longClick(b.ending, p.control.action.ending));
        b.opening.setOnLongClickListener(v -> longClick(b.opening, p.control.action.opening));
    }

    private static void onTimer(DialogFragment dialog) {
        TimerDialog.create().show(dialog.requireActivity());
        dialog.dismiss();
    }

    private static void setScaleText(DialogControlBinding b, ActivityVideoBinding p, List<TextView> scales, String[] scale) {
        for (int i = 0; i < scales.size(); i++) {
            scales.get(i).setText(scale[i]);
            scales.get(i).setSelected(scales.get(i).getText().equals(p.control.action.scale.getText()));
        }
    }

    private static void setParse(DialogControlBinding b, boolean parse, ParseAdapter.OnClickListener listener) {
        setParseVisible(b, parse);
        b.parse.setHasFixedSize(true);
        b.parse.setItemAnimator(null);
        b.parse.addItemDecoration(new SpaceItemDecoration(8));
        b.parse.setAdapter(new ParseAdapter(listener));
    }

    private static void setScale(View view, List<TextView> scales, FragmentActivity activity) {
        for (TextView textView : scales) textView.setSelected(false);
        ((Listener) activity).onScale(Integer.parseInt(view.getTag().toString()));
        view.setSelected(true);
    }

    private static void active(TextView view, TextView target) {
        target.performClick();
        view.setSelected(target.isSelected());
    }

    private static void click(TextView view, TextView target) {
        target.performClick();
        view.setText(target.getText());
    }

    private static boolean longClick(TextView view, TextView target) {
        target.performLongClick();
        view.setText(target.getText());
        return true;
    }

    private static void dismiss(DialogFragment dialog, View view) {
        App.post(view::performClick, 200);
        dialog.dismiss();
    }

    private static void setPlayer(DialogControlBinding b, ActivityVideoBinding p, PlayerManager player) {
        SliderUtil.setValue(b.speed, player.getSpeed());
        b.player.setText(p.control.action.player.getText());
        b.decode.setVisibility(Setting.isElderMode() ? View.GONE : View.VISIBLE);
        b.danmaku.setVisibility(View.VISIBLE);
    }

    private static void setParseVisible(DialogControlBinding b, boolean visible) {
        b.parse.setVisibility(visible ? View.VISIBLE : View.GONE);
        b.parseText.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private static void setTrackVisible(DialogControlBinding b, ActivityVideoBinding p) {
        b.text.setVisibility(p.control.action.text.getVisibility());
        b.audio.setVisibility(p.control.action.audio.getVisibility());
        b.video.setVisibility(p.control.action.video.getVisibility());
        b.track.setVisibility(b.text.getVisibility() == View.GONE && b.audio.getVisibility() == View.GONE && b.video.getVisibility() == View.GONE ? View.GONE : View.VISIBLE);
    }

    private static void setMediaOptionVisible(DialogControlBinding b, PlayerManager player) {
        PlaybackAction.setMediaOptions(player, b.edition, b.chapter);
    }

    private static void onItemClick(DialogFragment dialog, DialogControlBinding b, Parse item) {
        ((Listener) dialog.requireActivity()).onParse(item);
        b.parse.getAdapter().notifyItemRangeChanged(0, b.parse.getAdapter().getItemCount());
    }

    public static final class BottomSheet extends BaseBottomSheetDialog implements ParseAdapter.OnClickListener {

        private final ActivityVideoBinding parent;
        private final PlayerManager player;
        private final boolean parse;
        private DialogControlBinding binding;

        BottomSheet(ActivityVideoBinding parent, boolean parse, PlayerManager player) {
            this.parent = parent;
            this.parse = parse;
            this.player = player;
        }

        @Override
        protected ViewBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
            return binding = DialogControlBinding.inflate(inflater, container, false);
        }

        @Override
        protected void initView() {
            ControlDialog.init(this, binding, parent, player, parse, this);
        }

        @Override
        public void onItemClick(Parse item) {
            ControlDialog.onItemClick(this, binding, item);
        }
    }

    public static final class SideSheet extends BaseSideSheetDialog implements ParseAdapter.OnClickListener {

        private final ActivityVideoBinding parent;
        private final PlayerManager player;
        private final boolean parse;
        private DialogControlBinding binding;

        SideSheet(ActivityVideoBinding parent, boolean parse, PlayerManager player) {
            this.parent = parent;
            this.parse = parse;
            this.player = player;
        }

        @Override
        protected int getWidth() {
            return Math.min(ResUtil.dp2px(420), ResUtil.getScreenWidth() / 2);
        }

        @Override
        protected ViewBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
            return binding = DialogControlBinding.inflate(inflater, container, false);
        }

        @Override
        protected void initView() {
            ControlDialog.init(this, binding, parent, player, parse, this);
        }

        @Override
        public void onItemClick(Parse item) {
            ControlDialog.onItemClick(this, binding, item);
        }
    }
}

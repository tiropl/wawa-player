package com.wawa_player.android.tv.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.media3.common.C;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.sidesheet.SideSheetDialog;
import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.ActivityVideoBinding;
import com.wawa_player.android.tv.databinding.DialogControlBinding;
import com.wawa_player.android.tv.player.PlayerManager;
import com.wawa_player.android.tv.setting.DanmakuSetting;
import com.wawa_player.android.tv.utils.ResUtil;
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
        for (Fragment fragment : manager.getFragments()) if (fragment instanceof SideSheet) return;
        new SideSheet(parent, parse, player).show(manager, null);
    }

    public interface Listener {
        void onScale(int tag);
    }

    private static void init(DialogFragment dialog, DialogControlBinding b, ActivityVideoBinding p, PlayerManager player, boolean parse) {
        FragmentActivity activity = dialog.requireActivity();
        String[] scale = ResUtil.getStringArray(R.array.select_scale);
        List<TextView> scales = Arrays.asList(b.scale0, b.scale1, b.scale2, b.scale3, b.scale4);
        b.player.setText(p.control.action.player.getText());
        b.decode.setText(p.control.action.decode.getText());
        b.speed.setText(p.control.action.speed.getText());
        b.opening.setText(p.control.action.opening.getText());
        b.ending.setText(p.control.action.ending.getText());
        b.repeat.setSelected(p.control.action.repeat.isSelected());
        b.danmakuShow.setSelected(DanmakuSetting.isShow());
        b.danmakuShow.setText(DanmakuSetting.isShow() ? R.string.control_danmaku_on : R.string.control_danmaku_off);
        setTrackVisible(b, player);
        setScaleText(b, p, scales, scale);
        setParseVisible(b, parse);
        b.parse.setOnClickListener(v -> dismiss(dialog, p.control.action.parse));
        b.player.setOnClickListener(v -> dismiss(dialog, p.control.action.player));
        b.decode.setOnClickListener(v -> click(b.decode, p.control.action.decode));
        b.speed.setOnClickListener(v -> dismiss(dialog, p.control.action.speed));
        b.opening.setOnClickListener(v -> click(b.opening, p.control.action.opening));
        b.ending.setOnClickListener(v -> click(b.ending, p.control.action.ending));
        b.repeat.setOnClickListener(v -> active(b.repeat, p.control.action.repeat));
        b.replay.setOnClickListener(v -> dismiss(dialog, p.control.action.replay));
        b.reset.setOnClickListener(v -> dismiss(dialog, p.control.action.reset));
        b.text.setOnClickListener(v -> dismiss(dialog, p.control.action.text));
        b.audio.setOnClickListener(v -> dismiss(dialog, p.control.action.audio));
        b.video.setOnClickListener(v -> dismiss(dialog, p.control.action.video));
        b.danmakuShow.setOnClickListener(v -> dismiss(dialog, p.control.action.danmakuShow));
        b.danmaku.setOnClickListener(v -> dismiss(dialog, p.control.action.danmaku));
        for (TextView view : scales) view.setOnClickListener(v -> setScale(view, scales, activity));
    }

    private static void setScaleText(DialogControlBinding b, ActivityVideoBinding p, List<TextView> scales, String[] scale) {
        for (int i = 0; i < scales.size(); i++) {
            scales.get(i).setText(scale[i]);
            scales.get(i).setSelected(scales.get(i).getText().equals(p.control.action.scale.getText()));
        }
    }

    private static void setScale(View view, List<TextView> scales, FragmentActivity activity) {
        for (TextView textView : scales) textView.setSelected(false);
        ((Listener) activity).onScale(Integer.parseInt(view.getTag().toString()));
        view.setSelected(true);
    }

    private static void setParseVisible(DialogControlBinding b, boolean parse) {
        b.parseText.setVisibility(parse ? View.VISIBLE : View.GONE);
        b.parse.setVisibility(parse ? View.VISIBLE : View.GONE);
    }

    private static void setTrackVisible(DialogControlBinding b, PlayerManager player) {
        b.text.setVisibility(player.haveTrack(C.TRACK_TYPE_TEXT) || player.isVod() ? View.VISIBLE : View.GONE);
        b.audio.setVisibility(player.haveTrack(C.TRACK_TYPE_AUDIO) ? View.VISIBLE : View.GONE);
        b.video.setVisibility(player.haveTrack(C.TRACK_TYPE_VIDEO) ? View.VISIBLE : View.GONE);
        b.track.setVisibility(b.text.getVisibility() == View.GONE && b.audio.getVisibility() == View.GONE && b.video.getVisibility() == View.GONE ? View.GONE : View.VISIBLE);
    }

    private static void active(TextView view, TextView target) {
        target.performClick();
        view.setSelected(target.isSelected());
    }

    private static void click(TextView view, TextView target) {
        target.performClick();
        view.setText(target.getText());
    }

    private static void dismiss(DialogFragment dialog, View view) {
        App.post(view::performClick, 200);
        dialog.dismiss();
    }

    public static final class SideSheet extends BaseSideSheetDialog {

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

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            SideSheetDialog dialog = new SideSheetDialog(new ContextThemeWrapper(requireContext(), R.style.DialogControlTheme));
            dialog.getBehavior().setDraggable(false);
            Window window = dialog.getWindow();
            if (window == null) return dialog;
            if (Util.isFullscreen(getActivity())) window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            return dialog;
        }

        @Override
        protected ViewBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
            return binding = DialogControlBinding.inflate(inflater.cloneInContext(new ContextThemeWrapper(requireContext(), R.style.DialogControlTheme)), container, false);
        }

        @Override
        protected void initView() {
            ControlDialog.init(this, binding, parent, player, parse);
        }
    }
}

package com.wawa_player.android.tv.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class FontDialog extends DialogFragment {

    private static final int[] SIZES = {14, 16, 18, 20, 23};
    private boolean changed;
    private int selected;

    public static FontDialog create() {
        return new FontDialog();
    }

    public void show(Fragment fragment) {
        show(fragment.getChildFragmentManager(), null);
    }

    public void show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        selected = Setting.getFont();
        String[] fonts = ResUtil.getStringArray(R.array.select_font);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_single_choice, android.R.id.text1, fonts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View item = super.getView(position, convertView, parent);
                ((TextView) item.findViewById(android.R.id.text1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, SIZES[position]);
                return item;
            }
        };
        return new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.setting_font).setPositiveButton(R.string.dialog_positive, (dialog, which) -> {
            if (selected != Setting.getFont()) {
                Setting.putFont(selected);
                changed = true;
            }
            dialog.dismiss();
        }).setNegativeButton(R.string.dialog_negative, null).setSingleChoiceItems(adapter, Setting.getFont(), (dialog, which) -> {
            if (which == selected) {
                // 再次点击当前选项：若与旧字号不同，直接确认
                if (selected != Setting.getFont()) {
                    Setting.putFont(selected);
                    changed = true;
                    dialog.dismiss();
                }
            } else {
                selected = which;
            }
        }).create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!changed) return;
        if (getParentFragment() instanceof Listener) {
            ((Listener) getParentFragment()).onFontChanged();
        } else if (requireActivity() instanceof Listener) {
            ((Listener) requireActivity()).onFontChanged();
        }
    }

    public interface Listener {

        void onFontChanged();
    }
}

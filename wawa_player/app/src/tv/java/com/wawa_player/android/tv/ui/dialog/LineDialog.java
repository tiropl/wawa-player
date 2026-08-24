package com.wawa_player.android.tv.ui.dialog;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.api.config.VodConfig;
import com.wawa_player.android.tv.bean.Depot;
import com.wawa_player.android.tv.databinding.DialogLineBinding;
import com.wawa_player.android.tv.impl.LineListener;
import com.wawa_player.android.tv.ui.adapter.LineAdapter;
import com.wawa_player.android.tv.ui.custom.SpaceItemDecoration;
import com.wawa_player.android.tv.utils.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LineDialog extends BaseAlertDialog implements LineAdapter.OnClickListener {

    private DialogLineBinding binding;
    private LineAdapter adapter;

    public static LineDialog create() {
        return new LineDialog();
    }

    public void show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), null);
    }

    @Override
    protected ViewBinding getBinding() {
        return binding = DialogLineBinding.inflate(getLayoutInflater());
    }

    @Override
    protected MaterialAlertDialogBuilder getBuilder() {
        return builder().setTitle(R.string.line_title).setView(getBinding().getRoot());
    }

    @Override
    protected void initView() {
        adapter = new LineAdapter(this);
        binding.recycler.setItemAnimator(null);
        binding.recycler.setHasFixedSize(false);
        binding.recycler.addItemDecoration(new SpaceItemDecoration(1, 16));
        binding.recycler.setAdapter(adapter.addAll(0));
    }

    @Override
    public void onLineClick(Depot item) {
        Context context = requireContext();
        LineListener listener = (LineListener) requireActivity();
        dismiss();
        Task.execute(() -> {
            boolean risky = VodConfig.checkRisk(item);
            App.post(() -> {
                if (risky) {
                    new MaterialAlertDialogBuilder(context)
                        .setMessage(R.string.line_risk_confirm)
                        .setPositiveButton(R.string.dialog_positive, (d, w) -> listener.setLine(item))
                        .setNegativeButton(R.string.dialog_negative, null)
                        .show();
                } else {
                    listener.setLine(item);
                }
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setWidth(0.4f);
    }
}

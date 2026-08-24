package com.wawa_player.android.tv.ui.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.bean.Depot;
import com.wawa_player.android.tv.databinding.DialogLineBinding;
import com.wawa_player.android.tv.impl.LineListener;
import com.wawa_player.android.tv.ui.adapter.LineAdapter;
import com.wawa_player.android.tv.ui.custom.SpaceItemDecoration;
import com.wawa_player.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LineDialog extends BaseAlertDialog implements LineAdapter.OnClickListener {

    private DialogLineBinding binding;
    private LineListener listener;
    private LineAdapter adapter;

    public static LineDialog create() {
        return new LineDialog();
    }

    public void show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), null);
    }

    public void show(Fragment fragment) {
        show(fragment.getChildFragmentManager(), null);
    }

    private boolean isFull() {
        return getParentFragment() == null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = isFull() ? (LineListener) context : (LineListener) getParentFragment();
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
        if (isFull()) binding.recycler.setMaxHeight(ResUtil.dp2px(264));
        binding.recycler.addItemDecoration(new SpaceItemDecoration(1, 8));
        binding.recycler.setAdapter(adapter.addAll(0));
    }

    @Override
    public void onLineClick(Depot item) {
        listener.setLine(item);
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ResUtil.isLand(requireContext())) setWidth(0.5f);
    }
}

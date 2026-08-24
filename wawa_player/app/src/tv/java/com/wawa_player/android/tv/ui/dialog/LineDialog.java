package com.wawa_player.android.tv.ui.dialog;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.bean.Depot;
import com.wawa_player.android.tv.databinding.DialogLineBinding;
import com.wawa_player.android.tv.impl.LineListener;
import com.wawa_player.android.tv.ui.adapter.LineAdapter;
import com.wawa_player.android.tv.ui.custom.SpaceItemDecoration;
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
        ((LineListener) requireActivity()).setLine(item);
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        setWidth(0.4f);
    }
}

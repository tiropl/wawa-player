package com.fongmi.android.tv.ui.dialog;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.fongmi.android.tv.databinding.DialogDohBinding;
import com.fongmi.android.tv.ui.adapter.ModeAdapter;
import com.fongmi.android.tv.ui.custom.SpaceItemDecoration;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ModeDialog extends BaseAlertDialog implements ModeAdapter.OnClickListener {

    private DialogDohBinding binding;
    private ModeAdapter adapter;
    private int index;

    public static ModeDialog create() {
        return new ModeDialog();
    }

    public ModeDialog index(int index) {
        this.index = index;
        return this;
    }

    public void show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), null);
    }

    @Override
    protected ViewBinding getBinding() {
        return binding = DialogDohBinding.inflate(getLayoutInflater());
    }

    @Override
    protected MaterialAlertDialogBuilder getBuilder() {
        return builder().setView(getBinding().getRoot());
    }

    @Override
    protected void initView() {
        adapter = new ModeAdapter(this);
        adapter.setSelect(index);
        binding.recycler.setAdapter(adapter);
        binding.recycler.setHasFixedSize(true);
        binding.recycler.addItemDecoration(new SpaceItemDecoration(1, 16));
        binding.recycler.post(() -> binding.recycler.scrollToPosition(adapter.getSelect()));
    }

    @Override
    public void onItemClick(int mode) {
        ((Listener) requireActivity()).setMode(mode);
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        setWidth(0.4f);
    }

    public interface Listener {

        void setMode(int mode);
    }
}

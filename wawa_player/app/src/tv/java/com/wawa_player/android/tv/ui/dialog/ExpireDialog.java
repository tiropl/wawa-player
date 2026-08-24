package com.wawa_player.android.tv.ui.dialog;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.databinding.DialogDohBinding;
import com.wawa_player.android.tv.ui.adapter.ExpireAdapter;
import com.wawa_player.android.tv.ui.custom.SpaceItemDecoration;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ExpireDialog extends BaseAlertDialog implements ExpireAdapter.OnClickListener {

    private DialogDohBinding binding;
    private ExpireAdapter adapter;
    private int index;

    public static ExpireDialog create() {
        return new ExpireDialog();
    }

    public ExpireDialog index(int index) {
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
        adapter = new ExpireAdapter(this);
        adapter.setSelect(index);
        binding.recycler.setAdapter(adapter);
        binding.recycler.setHasFixedSize(true);
        binding.recycler.addItemDecoration(new SpaceItemDecoration(1, 16));
        binding.recycler.post(() -> binding.recycler.scrollToPosition(adapter.getSelect()));
    }

    @Override
    public void onItemClick(int index) {
        ((Listener) requireActivity()).setCacheExpire(index);
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        setWidth(0.4f);
    }

    public interface Listener {

        void setCacheExpire(int index);
    }
}

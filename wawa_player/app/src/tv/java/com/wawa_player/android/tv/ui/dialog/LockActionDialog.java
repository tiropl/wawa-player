package com.wawa_player.android.tv.ui.dialog;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.databinding.DialogDohBinding;
import com.wawa_player.android.tv.ui.adapter.LockActionAdapter;
import com.wawa_player.android.tv.ui.custom.SpaceItemDecoration;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LockActionDialog extends BaseAlertDialog implements LockActionAdapter.OnClickListener {

    private DialogDohBinding binding;

    public static LockActionDialog create() {
        return new LockActionDialog();
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
        binding.recycler.setAdapter(new LockActionAdapter(this));
        binding.recycler.setHasFixedSize(true);
        binding.recycler.addItemDecoration(new SpaceItemDecoration(1, 16));
    }

    @Override
    public void onItemClick(int action) {
        ((Listener) requireActivity()).onLockAction(action);
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        setWidth(0.4f);
    }

    public interface Listener {

        void onLockAction(int action);
    }
}

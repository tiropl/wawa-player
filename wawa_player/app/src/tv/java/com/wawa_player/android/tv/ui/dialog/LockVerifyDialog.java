package com.wawa_player.android.tv.ui.dialog;

import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.DialogLockVerifyBinding;
import com.wawa_player.android.tv.impl.LockListener;
import com.wawa_player.android.tv.setting.PasswordLock;
import com.wawa_player.android.tv.ui.custom.CustomTextListener;
import com.wawa_player.android.tv.ui.custom.HiddenPasswordTransformationMethod;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LockVerifyDialog extends BaseLockDialog {

    private DialogLockVerifyBinding binding;
    private LockListener listener;

    public static LockVerifyDialog create() {
        return new LockVerifyDialog();
    }

    public LockVerifyDialog listener(LockListener listener) {
        this.listener = listener;
        return this;
    }

    public void show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), null);
    }

    @Override
    protected ViewBinding getBinding() {
        return binding = DialogLockVerifyBinding.inflate(getLayoutInflater());
    }

    @Override
    protected MaterialAlertDialogBuilder getBuilder() {
        return builder().setView(getBinding().getRoot());
    }

    @Override
    protected View getContentView() {
        return binding == null ? null : binding.getRoot();
    }

    @Override
    protected void initEvent() {
        binding.pass.setTransformationMethod(HiddenPasswordTransformationMethod.getInstance());
        binding.positive.setOnClickListener(this::onPositive);
        binding.negative.setOnClickListener(this::onNegative);
        binding.pass.addTextChangedListener(new CustomTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.error.setVisibility(View.GONE);
            }
        });
        binding.pass.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) binding.positive.performClick();
            return true;
        });
    }

    private void onPositive(View view) {
        String pass = binding.pass.getText().toString().trim();
        if (PasswordLock.verify(pass)) {
            dismiss();
            if (listener != null) listener.onLockVerified();
        } else {
            showError(binding.error, R.string.lock_error_wrong);
        }
    }

    private void onNegative(View view) {
        dismiss();
        if (listener != null) listener.onLockCancelled();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (listener != null) listener.onLockCancelled();
    }
}

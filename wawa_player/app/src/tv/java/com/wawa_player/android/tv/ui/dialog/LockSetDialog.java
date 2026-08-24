package com.wawa_player.android.tv.ui.dialog;

import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.DialogLockSetBinding;
import com.wawa_player.android.tv.setting.PasswordLock;
import com.wawa_player.android.tv.ui.custom.CustomTextListener;
import com.wawa_player.android.tv.ui.custom.HiddenPasswordTransformationMethod;
import com.wawa_player.android.tv.utils.Notify;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LockSetDialog extends BaseLockDialog {

    private DialogLockSetBinding binding;
    private Listener listener;

    public static LockSetDialog create() {
        return new LockSetDialog();
    }

    public LockSetDialog listener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public void show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), null);
    }

    @Override
    protected ViewBinding getBinding() {
        return binding = DialogLockSetBinding.inflate(getLayoutInflater());
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
        binding.confirm.setTransformationMethod(HiddenPasswordTransformationMethod.getInstance());
        binding.positive.setOnClickListener(this::onPositive);
        binding.negative.setOnClickListener(this::onNegative);
        binding.pass.addTextChangedListener(new CustomTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.passError.setVisibility(View.GONE);
            }
        });
        binding.confirm.addTextChangedListener(new CustomTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.confirmError.setVisibility(View.GONE);
            }
        });
        binding.confirm.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) binding.positive.performClick();
            return true;
        });
    }

    private void onPositive(View view) {
        binding.passError.setVisibility(View.GONE);
        binding.confirmError.setVisibility(View.GONE);
        String pass = binding.pass.getText().toString().trim();
        String confirm = binding.confirm.getText().toString().trim();
        if (!PasswordLock.isValid(pass)) {
            showError(binding.passError, R.string.lock_error_length);
        } else if (!pass.equals(confirm)) {
            showError(binding.confirmError, R.string.lock_error_mismatch);
        } else {
            PasswordLock.setPassword(pass);
            Notify.show(R.string.lock_set_success);
            dismiss();
            if (listener != null) listener.onLockSet();
        }
    }

    private void onNegative(View view) {
        dismiss();
    }

    public interface Listener {

        void onLockSet();
    }
}

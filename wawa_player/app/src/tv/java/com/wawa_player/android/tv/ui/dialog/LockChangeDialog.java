package com.wawa_player.android.tv.ui.dialog;

import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.DialogLockChangeBinding;
import com.wawa_player.android.tv.setting.PasswordLock;
import com.wawa_player.android.tv.ui.custom.CustomTextListener;
import com.wawa_player.android.tv.ui.custom.HiddenPasswordTransformationMethod;
import com.wawa_player.android.tv.utils.Notify;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LockChangeDialog extends BaseLockDialog {

    private DialogLockChangeBinding binding;

    public static LockChangeDialog create() {
        return new LockChangeDialog();
    }

    public void show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), null);
    }

    @Override
    protected ViewBinding getBinding() {
        return binding = DialogLockChangeBinding.inflate(getLayoutInflater());
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
        binding.oldPass.setTransformationMethod(HiddenPasswordTransformationMethod.getInstance());
        binding.pass.setTransformationMethod(HiddenPasswordTransformationMethod.getInstance());
        binding.confirm.setTransformationMethod(HiddenPasswordTransformationMethod.getInstance());
        binding.positive.setOnClickListener(this::onPositive);
        binding.negative.setOnClickListener(this::onNegative);
        binding.oldPass.addTextChangedListener(new CustomTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.oldError.setVisibility(View.GONE);
            }
        });
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
        binding.oldError.setVisibility(View.GONE);
        binding.passError.setVisibility(View.GONE);
        binding.confirmError.setVisibility(View.GONE);
        String oldPass = binding.oldPass.getText().toString().trim();
        String pass = binding.pass.getText().toString().trim();
        String confirm = binding.confirm.getText().toString().trim();
        boolean ok = true;
        if (!PasswordLock.verify(oldPass)) {
            showError(binding.oldError, R.string.lock_error_old);
            ok = false;
        }
        if (!PasswordLock.isValid(pass)) {
            showError(binding.passError, R.string.lock_error_length);
            ok = false;
        } else if (!pass.equals(confirm)) {
            showError(binding.confirmError, R.string.lock_error_mismatch);
            ok = false;
        }
        if (!ok) return;
        PasswordLock.setPassword(pass);
        Notify.show(R.string.lock_change_success);
        dismiss();
    }

    private void onNegative(View view) {
        dismiss();
    }
}

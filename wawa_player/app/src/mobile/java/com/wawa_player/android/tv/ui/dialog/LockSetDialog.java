package com.wawa_player.android.tv.ui.dialog;

import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.DialogLockSetBinding;
import com.wawa_player.android.tv.setting.PasswordLock;
import com.wawa_player.android.tv.ui.custom.CustomTextListener;
import com.wawa_player.android.tv.ui.custom.HiddenPasswordTransformationMethod;
import com.wawa_player.android.tv.utils.Notify;
import com.wawa_player.android.tv.utils.ViewUtil;
import com.wawa_player.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LockSetDialog extends BaseAlertDialog {

    private DialogLockSetBinding binding;
    private Listener listener;

    public static LockSetDialog create() {
        return new LockSetDialog();
    }

    public LockSetDialog listener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public void show(Fragment fragment) {
        show(fragment.getChildFragmentManager(), null);
    }

    @Override
    protected ViewBinding getBinding() {
        return binding = DialogLockSetBinding.inflate(getLayoutInflater());
    }

    @Override
    protected MaterialAlertDialogBuilder getBuilder() {
        return builder().setTitle(R.string.lock_title_set).setView(getBinding().getRoot()).setPositiveButton(R.string.dialog_positive, null).setNegativeButton(R.string.dialog_negative, null);
    }

    @Override
    protected void initEvent() {
        binding.pass.setTransformationMethod(HiddenPasswordTransformationMethod.getInstance());
        binding.confirm.setTransformationMethod(HiddenPasswordTransformationMethod.getInstance());
        binding.pass.addTextChangedListener(new CustomTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideError(binding.passError);
            }
        });
        binding.confirm.addTextChangedListener(new CustomTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideError(binding.confirmError);
            }
        });
        binding.confirm.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) onPositive();
            return true;
        });
        binding.pass.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.confirm.requestFocus();
                return true;
            }
            return false;
        });
    }

    private void onPositive() {
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

    private void showError(TextView view, int resId) {
        view.setText(resId);
        view.setVisibility(View.VISIBLE);
        ViewUtil.scrollToReveal(view);
    }

    private void hideError(TextView view) {
        view.setText("");
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null || getDialog().getWindow() == null) return;
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ViewUtil.constrainToWindow(binding.getRoot(), ResUtil.dp2px(150));
        if (getDialog() instanceof AlertDialog dialog && dialog.getButton(DialogInterface.BUTTON_POSITIVE) != null) {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> onPositive());
        }
    }

    public interface Listener {

        void onLockSet();
    }
}

package com.wawa_player.android.tv.ui.dialog;

import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.DialogLockVerifyBinding;
import com.wawa_player.android.tv.impl.LockListener;
import com.wawa_player.android.tv.setting.PasswordLock;
import com.wawa_player.android.tv.ui.custom.CustomTextListener;
import com.wawa_player.android.tv.ui.custom.HiddenPasswordTransformationMethod;
import com.wawa_player.android.tv.utils.ViewUtil;
import com.wawa_player.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LockVerifyDialog extends BaseAlertDialog {

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

    public void show(Fragment fragment) {
        show(fragment.getChildFragmentManager(), null);
    }

    @Override
    protected ViewBinding getBinding() {
        return binding = DialogLockVerifyBinding.inflate(getLayoutInflater());
    }

    @Override
    protected MaterialAlertDialogBuilder getBuilder() {
        return builder().setTitle(R.string.lock_title_verify).setView(getBinding().getRoot()).setPositiveButton(R.string.dialog_positive, null).setNegativeButton(R.string.dialog_negative, null);
    }

    @Override
    protected void initEvent() {
        binding.pass.setTransformationMethod(HiddenPasswordTransformationMethod.getInstance());
        binding.pass.addTextChangedListener(new CustomTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideError(binding.passError);
            }
        });
        binding.pass.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) onPositive();
            return true;
        });
    }

    private void onPositive() {
        String pass = binding.pass.getText().toString().trim();
        if (PasswordLock.verify(pass)) {
            dismiss();
            if (listener != null) listener.onLockVerified();
        } else {
            showError(binding.passError, R.string.lock_error_wrong);
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
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (listener != null) listener.onLockCancelled();
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
}

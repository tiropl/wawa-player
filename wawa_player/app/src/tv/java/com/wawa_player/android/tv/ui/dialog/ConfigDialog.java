package com.wawa_player.android.tv.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.App;
import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.api.config.LiveConfig;
import com.wawa_player.android.tv.api.config.VodConfig;
import com.wawa_player.android.tv.api.config.WallConfig;
import com.wawa_player.android.tv.bean.Config;
import com.wawa_player.android.tv.databinding.DialogConfigBinding;
import com.wawa_player.android.tv.event.ServerEvent;
import com.wawa_player.android.tv.impl.ConfigListener;
import com.wawa_player.android.tv.server.Server;
import com.wawa_player.android.tv.ui.custom.CustomTextListener;
import com.wawa_player.android.tv.utils.FileChooser;
import com.wawa_player.android.tv.utils.QRCode;
import com.wawa_player.android.tv.utils.ResUtil;
import com.wawa_player.android.tv.utils.Task;
import com.wawa_player.android.tv.utils.ViewUtil;
import com.github.catvod.utils.Path;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ConfigDialog extends BaseAlertDialog {

    private DialogConfigBinding binding;
    private boolean append = true;
    private boolean edit;
    private String url;
    private int type;

    public static ConfigDialog create() {
        return new ConfigDialog();
    }

    public ConfigDialog vod() {
        type = 0;
        return this;
    }

    public ConfigDialog live() {
        type = 1;
        return this;
    }

    public ConfigDialog wall() {
        type = 2;
        return this;
    }

    public ConfigDialog edit() {
        edit = true;
        return this;
    }

    public void show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), null);
    }

    @Override
    protected ViewBinding getBinding() {
        return binding = DialogConfigBinding.inflate(getLayoutInflater());
    }

    @Override
    protected MaterialAlertDialogBuilder getBuilder() {
        return builder().setTitle(R.string.setting_line_config).setView(getBinding().getRoot());
    }

    @Override
    protected void initView() {
        binding.text.setText(url = getUrl());
        binding.text.setSelection(TextUtils.isEmpty(url) ? 0 : url.length());
        binding.positive.setText(edit ? R.string.dialog_edit : R.string.dialog_positive);
        binding.code.setImageBitmap(QRCode.getBitmap(Server.get().getAddress(4), 200, 0));
        binding.info.setText(ResUtil.getString(R.string.push_info, Server.get().getAddress()).replace("\uff0c", "\n"));
    }

    @Override
    protected void initEvent() {
        binding.choose.setOnClickListener(this::onChoose);
        binding.positive.setOnClickListener(this::onPositive);
        binding.negative.setOnClickListener(this::onNegative);
        binding.text.addTextChangedListener(new CustomTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                detect(s.toString());
                binding.error.setVisibility(View.GONE);
            }
        });
        binding.text.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) binding.positive.performClick();
            return true;
        });
    }

    private String getUrl() {
        return switch (type) {
            case 0 -> VodConfig.getUrl();
            case 1 -> LiveConfig.getUrl();
            case 2 -> WallConfig.getUrl();
            default -> "";
        };
    }

    private void onChoose(View view) {
        FileChooser.from(launcher).show();
    }

    private void detect(String s) {
        if (append && "h".equalsIgnoreCase(s)) {
            append = false;
            binding.text.append("ttp://");
        } else if (append && "f".equalsIgnoreCase(s)) {
            append = false;
            binding.text.append("ile://");
        } else if (append && "a".equalsIgnoreCase(s)) {
            append = false;
            binding.text.append("ssets://");
        } else if (s.length() > 1) {
            append = false;
        } else if (s.isEmpty()) {
            append = true;
        }
    }

    private void onPositive(View view) {
        String name = binding.name.getText().toString().trim();
        String text = binding.text.getText().toString().trim();
        if (text.isEmpty()) {
            showError(binding.error, R.string.dialog_config_error);
            return;
        }
        ConfigListener listener = (ConfigListener) requireActivity();
        dismiss();
        Task.submit(() -> {
            if (edit) Config.find(url, type).url(text).update();
            Config config = name.isEmpty() ? Config.find(text, type) : Config.find(text, name, type);
            App.post(() -> listener.setConfig(config));
        });
    }

    private void onNegative(View view) {
        dismiss();
    }

    private void showError(TextView view, int resId) {
        view.setText(resId);
        view.setVisibility(View.VISIBLE);
        ViewUtil.scrollToReveal(view);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerEvent(ServerEvent event) {
        if (event.type() != ServerEvent.Type.SETTING) return;
        binding.name.setText(event.name());
        binding.text.setText(event.text());
        binding.text.setSelection(binding.text.getText().length());
    }

    @Override
    public void onStart() {
        super.onStart();
        setWidth(0.55f);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null || result.getData().getData() == null) return;
        ((ConfigListener) requireActivity()).setConfig(Config.find("file:/" + FileChooser.getPathFromUri(result.getData().getData()).replace(Path.rootPath(), ""), type));
        dismiss();
    });
}
package com.wawa_player.android.tv.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.viewbinding.ViewBinding;

import com.wawa_player.android.tv.databinding.ActivityFileBinding;
import com.wawa_player.android.tv.ui.adapter.FileAdapter;
import com.wawa_player.android.tv.ui.base.BaseActivity;
import com.wawa_player.android.tv.utils.PermissionUtil;
import com.wawa_player.android.tv.utils.ResUtil;
import com.github.catvod.utils.Path;

import java.io.File;

public class FileActivity extends BaseActivity implements FileAdapter.OnClickListener {

    private ActivityFileBinding mBinding;
    private FileAdapter mAdapter;
    private File dir;

    private boolean isRoot() {
        return Path.root().equals(dir);
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivityFileBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setRecyclerView();
        checkPermission();
    }

    private void setRecyclerView() {
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setVerticalSpacing(ResUtil.dp2px(16));
        mBinding.recycler.setAdapter(mAdapter = new FileAdapter(this));
    }

    private void checkPermission() {
        PermissionUtil.requestFile(this, allGranted -> {
            if (allGranted) update(Path.root());
            else finish();
        });
    }

    private void update(File dir) {
        try {
            mBinding.recycler.setSelectedPosition(0);
            mAdapter.addAll(Path.list(this.dir = dir));
            mBinding.progressLayout.showContent(true, mAdapter.getItemCount());
        } catch (Exception ignored) {
            finish();
        }
    }

    @Override
    public void onItemClick(File file) {
        if (file.isDirectory()) {
            update(file);
        } else {
            setResult(RESULT_OK, new Intent().setData(Uri.fromFile(file)));
            finish();
        }
    }

    @Override
    protected void onBackInvoked() {
        if (dir == null || isRoot()) {
            super.onBackInvoked();
        } else {
            update(dir.getParentFile());
        }
    }
}

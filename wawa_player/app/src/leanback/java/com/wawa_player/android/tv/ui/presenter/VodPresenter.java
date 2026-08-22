package com.wawa_player.android.tv.ui.presenter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.leanback.widget.Presenter;

import com.wawa_player.android.tv.Product;
import com.wawa_player.android.tv.bean.Style;
import com.wawa_player.android.tv.bean.Vod;
import com.wawa_player.android.tv.databinding.AdapterVodListBinding;
import com.wawa_player.android.tv.databinding.AdapterVodOvalBinding;
import com.wawa_player.android.tv.databinding.AdapterVodRectBinding;
import com.wawa_player.android.tv.ui.base.BaseVodHolder;
import com.wawa_player.android.tv.ui.base.ViewType;
import com.wawa_player.android.tv.ui.holder.VodListHolder;
import com.wawa_player.android.tv.ui.holder.VodOvalHolder;
import com.wawa_player.android.tv.ui.holder.VodRectHolder;

public class VodPresenter extends Presenter {

    private final OnClickListener listener;
    private final Style style;
    private final int[] size;

    public VodPresenter(OnClickListener listener) {
        this(listener, Style.rect());
    }

    public VodPresenter(OnClickListener listener, Style style) {
        this.listener = listener;
        this.style = style;
        this.size = Product.getSpec(style);
    }

    public interface OnClickListener {

        void onItemClick(Vod item);

        boolean onLongClick(Vod item);
    }

    @NonNull
    @Override
    public Presenter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return switch (style.getViewType()) {
            case ViewType.LIST -> new VodListHolder(AdapterVodListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
            case ViewType.OVAL -> new VodOvalHolder(AdapterVodOvalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener).size(size);
            default -> new VodRectHolder(AdapterVodRectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener).size(size);
        };
    }

    @Override
    public void onBindViewHolder(@NonNull Presenter.ViewHolder viewHolder, Object object) {
        ((BaseVodHolder) viewHolder).initView((Vod) object);
    }

    @Override
    public void onUnbindViewHolder(@NonNull Presenter.ViewHolder viewHolder) {
        ((BaseVodHolder) viewHolder).unbind();
    }
}
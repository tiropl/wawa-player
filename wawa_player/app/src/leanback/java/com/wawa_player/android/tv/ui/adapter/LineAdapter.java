package com.wawa_player.android.tv.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wawa_player.android.tv.api.config.LineConfig;
import com.wawa_player.android.tv.api.config.VodConfig;
import com.wawa_player.android.tv.bean.Depot;
import com.wawa_player.android.tv.databinding.AdapterConfigBinding;

import java.util.List;

public class LineAdapter extends RecyclerView.Adapter<LineAdapter.ViewHolder> {

    private final OnClickListener listener;
    private List<Depot> mItems;

    public LineAdapter(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {

        void onLineClick(Depot item);
    }

    public LineAdapter addAll(int type) {
        mItems = LineConfig.getLines(type);
        return this;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AdapterConfigBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Depot item = mItems.get(position);
        holder.binding.text.setText(item.getName());
        holder.binding.text.setSelected(item.getUrl().equals(VodConfig.getUrl()));
        holder.binding.text.setOnClickListener(v -> listener.onLineClick(item));
        holder.binding.delete.setVisibility(View.GONE);
        holder.binding.delete.setFocusable(false);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AdapterConfigBinding binding;

        public ViewHolder(@NonNull AdapterConfigBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

package com.wawa_player.android.tv.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.AdapterDohBinding;
import com.wawa_player.android.tv.setting.Setting;
import com.wawa_player.android.tv.utils.ResUtil;

public class ModeAdapter extends RecyclerView.Adapter<ModeAdapter.ViewHolder> {

    private final OnClickListener listener;
    private final String[] mItems;
    private int select;

    public ModeAdapter(OnClickListener listener) {
        this.listener = listener;
        this.mItems = new String[]{
                ResUtil.getString(R.string.setting_mode_default),
                ResUtil.getString(R.string.setting_mode_elder),
                ResUtil.getString(R.string.setting_mode_child)
        };
    }

    public interface OnClickListener {

        void onItemClick(int mode);
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public int getSelect() {
        return select;
    }

    @Override
    public int getItemCount() {
        return mItems.length;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AdapterDohBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.text.setText(mItems[position]);
        holder.binding.text.setSelected(select == position);
        holder.binding.text.setOnClickListener(v -> listener.onItemClick(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AdapterDohBinding binding;

        public ViewHolder(@NonNull AdapterDohBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

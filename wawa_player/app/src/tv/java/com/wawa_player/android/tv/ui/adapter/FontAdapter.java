package com.wawa_player.android.tv.ui.adapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wawa_player.android.tv.R;
import com.wawa_player.android.tv.databinding.AdapterFontBinding;
import com.wawa_player.android.tv.utils.ResUtil;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.ViewHolder> {

    private static final int[] SIZES = {16, 18, 20, 22, 26};
    private final OnClickListener listener;
    private final String[] items;
    private int select;

    public FontAdapter(OnClickListener listener) {
        this.items = ResUtil.getStringArray(R.array.select_font);
        this.listener = listener;
    }

    public interface OnClickListener {

        void onItemClick(int index);
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public int getSelect() {
        return select;
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AdapterFontBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.text.setText(items[position]);
        holder.binding.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SIZES[position]);
        holder.binding.text.setSelected(select == position);
        holder.binding.text.setOnClickListener(v -> listener.onItemClick(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AdapterFontBinding binding;

        public ViewHolder(@NonNull AdapterFontBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

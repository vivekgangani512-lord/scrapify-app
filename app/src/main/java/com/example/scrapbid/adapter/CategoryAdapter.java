package com.example.scrapbid.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrapbid.R;
import com.example.scrapbid.model.ScrapCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategorySelectedListener {
        void onSelected(ScrapCategory category);
    }

    private final List<ScrapCategory> categories;
    private int selectedPosition = -1;
    private final OnCategorySelectedListener listener;

    public CategoryAdapter(List<ScrapCategory> categories, OnCategorySelectedListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScrapCategory cat = categories.get(position);
        holder.tvEmoji.setText(cat.emoji);
        holder.tvName.setText(cat.name);
        holder.tvPrice.setText("₹" + (int) cat.basePricePerKg + "/kg");

        try {
            holder.tvEmoji.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor(cat.colorHex)));
        } catch (Exception ignored) {}

        boolean selected = position == selectedPosition;
        holder.itemView.setBackground(holder.itemView.getContext().getDrawable(
                selected ? R.drawable.bg_category_selected : R.drawable.bg_category_unselected));

        holder.tvName.setTextColor(holder.itemView.getContext().getColor(
                selected ? R.color.primary_medium : R.color.text_primary));

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            if (prev != -1) notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            listener.onSelected(cat);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_category_emoji);
            tvName = itemView.findViewById(R.id.tv_category_name);
            tvPrice = itemView.findViewById(R.id.tv_category_price);
        }
    }
}

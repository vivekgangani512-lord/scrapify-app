package com.example.scrapbid.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrapbid.R;
import com.example.scrapbid.model.Deal;

import java.util.List;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewHolder> {

    public interface OnDealClickListener {
        void onClick(Deal deal);
    }

    private final List<Deal> deals;
    private final OnDealClickListener listener;
    private final boolean showDealer; // true = show dealer info, false = show owner info

    public DealAdapter(List<Deal> deals, OnDealClickListener listener, boolean showDealer) {
        this.deals = deals;
        this.listener = listener;
        this.showDealer = showDealer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deal_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Deal d = deals.get(position);

        h.tvEmoji.setText(d.categoryEmoji);
        h.tvCategory.setText(d.categoryName);
        h.tvWeight.setText(String.format("%.1f kg", d.scrapWeight));
        h.tvFinalPrice.setText("₹" + (int) d.finalPrice);
        h.tvAddress.setText(d.scrapAddress);
        h.tvDate.setText(d.createdAt != null ? d.createdAt.substring(0, 10) : "");

        if (showDealer) {
            h.tvPartnerLabel.setText("Dealer");
            h.tvPartnerName.setText(d.dealerName);
            h.tvPartnerPhone.setText(d.dealerPhone);
        } else {
            h.tvPartnerLabel.setText("Owner");
            h.tvPartnerName.setText(d.userName);
            h.tvPartnerPhone.setVisibility(View.GONE);
        }

        try {
            h.tvEmoji.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor(d.categoryColorHex)));
        } catch (Exception ignored) {}

        h.itemView.setOnClickListener(v -> listener.onClick(d));
    }

    @Override
    public int getItemCount() { return deals.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvCategory, tvWeight, tvFinalPrice, tvAddress, tvDate;
        TextView tvPartnerLabel, tvPartnerName, tvPartnerPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_emoji);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvWeight = itemView.findViewById(R.id.tv_weight);
            tvFinalPrice = itemView.findViewById(R.id.tv_final_price);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPartnerLabel = itemView.findViewById(R.id.tv_partner_label);
            tvPartnerName = itemView.findViewById(R.id.tv_partner_name);
            tvPartnerPhone = itemView.findViewById(R.id.tv_partner_phone);
        }
    }
}

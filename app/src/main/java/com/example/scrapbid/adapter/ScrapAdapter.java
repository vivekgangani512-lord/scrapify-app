package com.example.scrapbid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrapbid.R;
import com.example.scrapbid.model.Scrap;

import java.util.List;

public class ScrapAdapter extends RecyclerView.Adapter<ScrapAdapter.ViewHolder> {

    public interface OnScrapClickListener {
        void onClick(Scrap scrap);
    }

    private final List<Scrap> scraps;
    private final OnScrapClickListener listener;
    private final boolean showOwner;

    public ScrapAdapter(List<Scrap> scraps, OnScrapClickListener listener, boolean showOwner) {
        this.scraps = scraps;
        this.listener = listener;
        this.showOwner = showOwner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scrap_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Scrap s = scraps.get(position);
        Context ctx = h.itemView.getContext();

        h.tvEmoji.setText(s.categoryEmoji);
        h.tvCategory.setText(s.categoryName);
        h.tvWeight.setText(String.format("%.1f kg", s.weight));
        h.tvPrice.setText("₹" + (int) s.askingPrice);
        h.tvAddress.setText(s.address);
        h.tvBidCount.setText(s.bidCount + (s.bidCount == 1 ? " bid" : " bids"));

        if (showOwner) {
            h.tvOwner.setVisibility(View.VISIBLE);
            h.tvOwner.setText("By " + s.userName);
        } else {
            h.tvOwner.setVisibility(View.GONE);
        }

        // Status badge
        applyStatusBadge(ctx, h.tvStatus, s.status);

        // Category emoji background color
        try {
            h.tvEmoji.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor(s.categoryColorHex)));
        } catch (Exception ignored) {}

        h.itemView.setOnClickListener(v -> listener.onClick(s));
    }

    public static void applyStatusBadge(Context ctx, TextView tv, String status) {
        switch (status) {
            case Scrap.STATUS_OPEN:
                tv.setText("OPEN");
                tv.setTextColor(ctx.getColor(R.color.status_open_text));
                tv.setBackgroundResource(R.drawable.bg_badge_open);
                break;
            case Scrap.STATUS_BIDDING:
                tv.setText("BIDDING");
                tv.setTextColor(ctx.getColor(R.color.status_bidding_text));
                tv.setBackgroundResource(R.drawable.bg_badge_bidding);
                break;
            case Scrap.STATUS_DEALT:
                tv.setText("DEALT");
                tv.setTextColor(ctx.getColor(R.color.status_dealt_text));
                tv.setBackgroundResource(R.drawable.bg_badge_dealt);
                break;
            default:
                tv.setText(status);
                tv.setTextColor(ctx.getColor(R.color.status_pending_text));
                tv.setBackgroundResource(R.drawable.bg_badge_pending);
        }
    }

    @Override
    public int getItemCount() { return scraps.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvCategory, tvWeight, tvPrice, tvAddress, tvStatus, tvBidCount, tvOwner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_emoji);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvWeight = itemView.findViewById(R.id.tv_weight);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvBidCount = itemView.findViewById(R.id.tv_bid_count);
            tvOwner = itemView.findViewById(R.id.tv_owner);
        }
    }
}

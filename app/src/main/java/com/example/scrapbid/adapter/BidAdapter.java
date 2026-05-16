package com.example.scrapbid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrapbid.R;
import com.example.scrapbid.model.Bid;
import com.example.scrapbid.model.Scrap;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.ViewHolder> {

    public interface OnBidActionListener {
        void onAccept(Bid bid);
    }

    private final List<Bid> bids;
    private final OnBidActionListener listener;
    private final boolean showAcceptButton;
    private final String scrapStatus;

    public BidAdapter(List<Bid> bids, OnBidActionListener listener,
                      boolean showAcceptButton, String scrapStatus) {
        this.bids = bids;
        this.listener = listener;
        this.showAcceptButton = showAcceptButton;
        this.scrapStatus = scrapStatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bid_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Bid bid = bids.get(position);
        Context ctx = h.itemView.getContext();

        // Dealer avatar initial
        String initial = bid.dealerName != null && !bid.dealerName.isEmpty()
                ? String.valueOf(bid.dealerName.charAt(0)).toUpperCase() : "D";
        h.tvAvatar.setText(initial);

        h.tvDealerName.setText(bid.dealerName);
        h.tvDealerPhone.setText(bid.dealerPhone);
        h.tvBidAmount.setText("₹" + (int) bid.bidPrice);
        h.tvNote.setText(bid.note != null && !bid.note.isEmpty() ? bid.note : "No message");

        // Status badge
        applyBidStatus(ctx, h.tvStatus, bid.status);

        // Show accept button only for pending bids on open scraps
        boolean canAccept = showAcceptButton
                && Bid.STATUS_PENDING.equals(bid.status)
                && !Scrap.STATUS_DEALT.equals(scrapStatus);

        h.btnAccept.setVisibility(canAccept ? View.VISIBLE : View.GONE);
        h.btnAccept.setOnClickListener(v -> listener.onAccept(bid));
    }

    private void applyBidStatus(Context ctx, TextView tv, String status) {
        switch (status) {
            case Bid.STATUS_PENDING:
                tv.setText("PENDING");
                tv.setTextColor(ctx.getColor(R.color.status_pending_text));
                tv.setBackgroundResource(R.drawable.bg_badge_pending);
                break;
            case Bid.STATUS_ACCEPTED:
                tv.setText("ACCEPTED ✓");
                tv.setTextColor(ctx.getColor(R.color.status_accepted_text));
                tv.setBackgroundResource(R.drawable.bg_badge_accepted);
                break;
            case Bid.STATUS_REJECTED:
                tv.setText("REJECTED");
                tv.setTextColor(ctx.getColor(R.color.status_rejected_text));
                tv.setBackgroundResource(R.drawable.bg_badge_rejected);
                break;
        }
    }

    @Override
    public int getItemCount() { return bids.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvDealerName, tvDealerPhone, tvBidAmount, tvNote, tvStatus;
        MaterialButton btnAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvDealerName = itemView.findViewById(R.id.tv_dealer_name);
            tvDealerPhone = itemView.findViewById(R.id.tv_dealer_phone);
            tvBidAmount = itemView.findViewById(R.id.tv_bid_amount);
            tvNote = itemView.findViewById(R.id.tv_note);
            tvStatus = itemView.findViewById(R.id.tv_bid_status);
            btnAccept = itemView.findViewById(R.id.btn_accept_bid);
        }
    }
}

package com.example.scrapbid.user;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrapbid.DatabaseHelper;
import com.example.scrapbid.R;
import com.example.scrapbid.SessionManager;
import com.example.scrapbid.adapter.BidAdapter;
import com.example.scrapbid.adapter.ScrapAdapter;
import com.example.scrapbid.model.Bid;
import com.example.scrapbid.model.Scrap;

import java.util.List;

public class ScrapDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;
    private Scrap scrap;
    private int scrapId;
    private RecyclerView rvBids;
    private LinearLayout emptyBids;
    private TextView tvCategory, tvWeight, tvPrice, tvAddress, tvStatus, tvDescription, tvBidCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Scrap Details");
        }

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        scrapId = getIntent().getIntExtra("scrap_id", -1);
        if (scrapId == -1) { finish(); return; }

        tvCategory = findViewById(R.id.tv_category);
        tvWeight = findViewById(R.id.tv_weight);
        tvPrice = findViewById(R.id.tv_price);
        tvAddress = findViewById(R.id.tv_address);
        tvStatus = findViewById(R.id.tv_status);
        tvDescription = findViewById(R.id.tv_description);
        tvBidCount = findViewById(R.id.tv_bid_count);
        rvBids = findViewById(R.id.rv_bids);
        emptyBids = findViewById(R.id.empty_bids);

        rvBids.setLayoutManager(new LinearLayoutManager(this));
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        scrap = db.getScrapById(scrapId);
        if (scrap == null) { finish(); return; }

        tvCategory.setText(scrap.categoryEmoji + " " + scrap.categoryName);
        tvWeight.setText(String.format("%.1f kg", scrap.weight));
        tvPrice.setText("₹" + (int) scrap.askingPrice);
        tvAddress.setText(scrap.address);
        if (scrap.description != null && !scrap.description.isEmpty()) {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(scrap.description);
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        ScrapAdapter.applyStatusBadge(this, tvStatus, scrap.status);

        // Load bids
        List<Bid> bids = db.getBidsForScrap(scrapId);
        tvBidCount.setText(bids.size() + (bids.size() == 1 ? " bid" : " bids") + " received");

        boolean isOwner = scrap.userId == session.getUserId();
        boolean canAccept = isOwner && !Scrap.STATUS_DEALT.equals(scrap.status);

        if (bids.isEmpty()) {
            rvBids.setVisibility(View.GONE);
            emptyBids.setVisibility(View.VISIBLE);
        } else {
            rvBids.setVisibility(View.VISIBLE);
            emptyBids.setVisibility(View.GONE);
            BidAdapter adapter = new BidAdapter(bids, bid -> showAcceptDialog(bid), canAccept, scrap.status);
            rvBids.setAdapter(adapter);
        }
    }

    private void showAcceptDialog(Bid bid) {
        new AlertDialog.Builder(this)
                .setTitle("Accept this bid?")
                .setMessage("Accept bid of ₹" + (int) bid.bidPrice + " from " + bid.dealerName
                        + "?\n\nThis will reject all other bids and confirm the deal.")
                .setPositiveButton("Accept", (d, w) -> acceptBid(bid))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void acceptBid(Bid bid) {
        long dealId = db.acceptBid(bid.id, scrapId, session.getUserId(), bid.dealerId, bid.bidPrice);
        if (dealId > 0) {
            Toast.makeText(this, "Deal confirmed! 🎉", Toast.LENGTH_LONG).show();
            loadData();
        } else {
            Toast.makeText(this, "Failed to confirm deal", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.scrapbid.dealer;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scrapbid.DatabaseHelper;
import com.example.scrapbid.R;
import com.example.scrapbid.SessionManager;
import com.example.scrapbid.adapter.ScrapAdapter;
import com.example.scrapbid.model.Scrap;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class PlaceBidActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;
    private Scrap scrap;
    private TextInputEditText etBidPrice, etNote;
    private MaterialButton btnPlaceBid;
    private TextView tvAlreadyBid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_bid);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Place a Bid");
        }

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        int scrapId = getIntent().getIntExtra("scrap_id", -1);
        if (scrapId == -1) { finish(); return; }

        scrap = db.getScrapById(scrapId);
        if (scrap == null) { finish(); return; }

        etBidPrice = findViewById(R.id.et_bid_price);
        etNote = findViewById(R.id.et_note);
        btnPlaceBid = findViewById(R.id.btn_place_bid);
        tvAlreadyBid = findViewById(R.id.tv_already_bid);

        // Populate scrap info
        TextView tvEmoji = findViewById(R.id.tv_emoji);
        TextView tvCategory = findViewById(R.id.tv_category);
        TextView tvWeight = findViewById(R.id.tv_weight);
        TextView tvAskingPrice = findViewById(R.id.tv_asking_price);
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvOwner = findViewById(R.id.tv_owner);
        TextView tvStatus = findViewById(R.id.tv_status);

        tvEmoji.setText(scrap.categoryEmoji);
        tvCategory.setText(scrap.categoryName);
        tvWeight.setText(String.format("%.1f kg", scrap.weight));
        tvAskingPrice.setText("₹" + (int) scrap.askingPrice);
        tvAddress.setText(scrap.address);
        tvOwner.setText("Listed by " + scrap.userName);
        ScrapAdapter.applyStatusBadge(this, tvStatus, scrap.status);

        try {
            tvEmoji.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor(scrap.categoryColorHex)));
        } catch (Exception ignored) {}

        // Check if dealer already bid
        boolean alreadyBid = db.hasDealerBidOnScrap(session.getUserId(), scrapId);
        if (alreadyBid || Scrap.STATUS_DEALT.equals(scrap.status)) {
            etBidPrice.setEnabled(false);
            etNote.setEnabled(false);
            btnPlaceBid.setEnabled(false);
            tvAlreadyBid.setVisibility(View.VISIBLE);
            if (Scrap.STATUS_DEALT.equals(scrap.status)) {
                tvAlreadyBid.setText("This scrap has already been dealt.");
            } else {
                tvAlreadyBid.setText("You have already placed a bid on this scrap.");
            }
        } else {
            tvAlreadyBid.setVisibility(View.GONE);
            // Pre-fill with asking price as suggestion
            etBidPrice.setText(String.valueOf((int) scrap.askingPrice));
        }

        btnPlaceBid.setOnClickListener(v -> placeBid());
    }

    private void placeBid() {
        String priceStr = etBidPrice.getText() != null ? etBidPrice.getText().toString().trim() : "";
        String note = etNote.getText() != null ? etNote.getText().toString().trim() : "";

        if (TextUtils.isEmpty(priceStr)) { etBidPrice.setError("Enter bid price"); return; }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            etBidPrice.setError("Invalid price");
            return;
        }

        if (price <= 0) { etBidPrice.setError("Price must be > 0"); return; }

        long bidId = db.placeBid(scrap.id, session.getUserId(), price, note);
        if (bidId > 0) {
            Toast.makeText(this, "Bid placed successfully! 🎉", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to place bid", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}

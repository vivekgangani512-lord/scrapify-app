package com.example.scrapbid.dealer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.scrapbid.DatabaseHelper;
import com.example.scrapbid.LoginActivity;
import com.example.scrapbid.R;
import com.example.scrapbid.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DealerDashboardActivity extends AppCompatActivity {

    private SessionManager session;
    private DatabaseHelper db;
    private TextView tvBidCount, tvDealCount, tvAvailableCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_dashboard);

        session = new SessionManager(this);
        db = new DatabaseHelper(this);

        TextView tvGreeting = findViewById(R.id.tv_greeting);
        tvGreeting.setText("Hello, " + session.getUserName() + " 👋");

        tvAvailableCount = findViewById(R.id.tv_available_count);
        tvBidCount = findViewById(R.id.tv_bid_count);
        tvDealCount = findViewById(R.id.tv_deal_count);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_browse) {
                fragment = new AvailableScrapsFragment();
            } else if (id == R.id.nav_my_bids) {
                fragment = new MyBidsFragment();
            } else if (id == R.id.nav_deals) {
                fragment = new DealerDealsFragment();
            } else {
                return false;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
            return true;
        });

        bottomNav.setSelectedItemId(R.id.nav_browse);

        TextView tvLogout = findViewById(R.id.tv_logout);
        tvLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void updateStats() {
        int dealerId = session.getUserId();
        int available = db.getAvailableScraps(dealerId).size();
        tvAvailableCount.setText(String.valueOf(available));
        tvBidCount.setText(String.valueOf(db.getDealerBidCount(dealerId)));
        tvDealCount.setText(String.valueOf(db.getDealCountByDealer(dealerId)));
    }

    private void logout() {
        session.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

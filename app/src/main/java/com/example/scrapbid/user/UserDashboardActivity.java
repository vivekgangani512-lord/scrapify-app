package com.example.scrapbid.user;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserDashboardActivity extends AppCompatActivity {

    private SessionManager session;
    private DatabaseHelper db;
    private TextView tvGreeting, tvScrapCount, tvActiveCount, tvDealCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        session = new SessionManager(this);
        db = new DatabaseHelper(this);

        tvGreeting = findViewById(R.id.tv_greeting);
        tvScrapCount = findViewById(R.id.tv_scrap_count);
        tvActiveCount = findViewById(R.id.tv_active_count);
        tvDealCount = findViewById(R.id.tv_deal_count);

        tvGreeting.setText("Hello, " + session.getUserName() + " 👋");

        FloatingActionButton fab = findViewById(R.id.fab_add_scrap);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddScrapActivity.class)));

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showHomeContent(true);
                return true;
            } else if (id == R.id.nav_my_scraps) {
                fragment = new MyScrapFragment();
            } else if (id == R.id.nav_deals) {
                fragment = new UserDealsFragment();
            }
            if (fragment != null) {
                showHomeContent(false);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
            return true;
        });

        // Default: show home tab
        showHomeContent(true);
        bottomNav.setSelectedItemId(R.id.nav_home);

        TextView tvLogout = findViewById(R.id.tv_logout);
        tvLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void showHomeContent(boolean show) {
        int homeVis = show ? android.view.View.VISIBLE : android.view.View.GONE;
        int fragVis = show ? android.view.View.GONE : android.view.View.VISIBLE;
        findViewById(R.id.home_content).setVisibility(homeVis);
        findViewById(R.id.fragment_container).setVisibility(fragVis);
    }

    private void updateStats() {
        int userId = session.getUserId();
        tvScrapCount.setText(String.valueOf(db.getScrapCount(userId)));
        tvActiveCount.setText(String.valueOf(db.getActiveScrapCount(userId)));
        tvDealCount.setText(String.valueOf(db.getDealCountByUser(userId)));
    }

    private void logout() {
        session.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

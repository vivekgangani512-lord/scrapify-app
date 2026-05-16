package com.example.scrapbid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scrapbid.dealer.DealerDashboardActivity;
import com.example.scrapbid.user.UserDashboardActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            SessionManager session = new SessionManager(this);
            Intent intent;
            if (session.isLoggedIn()) {
                if (session.isDealer()) {
                    intent = new Intent(this, DealerDashboardActivity.class);
                } else {
                    intent = new Intent(this, UserDashboardActivity.class);
                }
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 1500);
    }
}

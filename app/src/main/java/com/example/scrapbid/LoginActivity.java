package com.example.scrapbid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scrapbid.dealer.DealerDashboardActivity;
import com.example.scrapbid.model.User;
import com.example.scrapbid.user.UserDashboardActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout btnUser, btnDealer;
    private TextView tvRoleUserLabel, tvRoleDealerLabel;
    private TextInputEditText etPhone, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister;
    private String selectedRole = User.ROLE_USER;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        btnUser = findViewById(R.id.btn_role_user);
        btnDealer = findViewById(R.id.btn_role_dealer);
        tvRoleUserLabel = btnUser.findViewById(R.id.tv_role_user_label);
        tvRoleDealerLabel = btnDealer.findViewById(R.id.tv_role_dealer_label);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        if (getIntent().hasExtra("role")) {
            selectedRole = getIntent().getStringExtra("role");
        }

        updateRoleUI();

        btnUser.setOnClickListener(v -> {
            selectedRole = User.ROLE_USER;
            updateRoleUI();
        });

        btnDealer.setOnClickListener(v -> {
            selectedRole = User.ROLE_DEALER;
            updateRoleUI();
        });

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("role", selectedRole);
            startActivity(intent);
        });
    }

    private void updateRoleUI() {
        int active = getColor(R.color.primary_medium);
        int inactive = getColor(R.color.text_secondary);

        if (User.ROLE_USER.equals(selectedRole)) {
            btnUser.setBackgroundResource(R.drawable.bg_category_selected);
            tvRoleUserLabel.setTextColor(active);
            btnDealer.setBackgroundResource(R.drawable.bg_category_unselected);
            tvRoleDealerLabel.setTextColor(inactive);
        } else {
            btnDealer.setBackgroundResource(R.drawable.bg_category_selected);
            tvRoleDealerLabel.setTextColor(active);
            btnUser.setBackgroundResource(R.drawable.bg_category_unselected);
            tvRoleUserLabel.setTextColor(inactive);
        }
    }

    private void attemptLogin() {
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String pass = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(phone)) { etPhone.setError("Enter phone number"); return; }
        if (TextUtils.isEmpty(pass)) { etPassword.setError("Enter password"); return; }

        User user = db.loginUser(phone, pass);
        if (user == null) {
            Toast.makeText(this, "Invalid phone or password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!user.role.equals(selectedRole)) {
            String roleName = User.ROLE_DEALER.equals(user.role) ? "Scrap Dealer" : "Home Owner";
            Toast.makeText(this, "This account is a " + roleName + ". Please select the correct role.", Toast.LENGTH_LONG).show();
            return;
        }

        session.saveSession(user.id, user.name, user.phone, user.role);
        Intent intent = user.isDealer()
                ? new Intent(this, DealerDashboardActivity.class)
                : new Intent(this, UserDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

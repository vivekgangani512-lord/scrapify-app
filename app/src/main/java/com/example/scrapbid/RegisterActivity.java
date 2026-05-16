package com.example.scrapbid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scrapbid.dealer.DealerDashboardActivity;
import com.example.scrapbid.model.User;
import com.example.scrapbid.user.UserDashboardActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etPassword, etConfirmPassword, etAddress;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private String role = User.ROLE_USER;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        role = getIntent().getStringExtra("role");
        if (role == null) role = User.ROLE_USER;

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etAddress = findViewById(R.id.et_address);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        TextView tvRoleLabel = findViewById(R.id.tv_role_label);
        tvRoleLabel.setText(User.ROLE_DEALER.equals(role) ? "Registering as Scrap Dealer" : "Registering as Home Owner");

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void attemptRegister() {
        String name = text(etName);
        String phone = text(etPhone);
        String pass = text(etPassword);
        String confirm = text(etConfirmPassword);
        String address = text(etAddress);

        if (TextUtils.isEmpty(name)) { etName.setError("Enter your name"); return; }
        if (TextUtils.isEmpty(phone) || phone.length() < 10) { etPhone.setError("Enter valid phone"); return; }
        if (TextUtils.isEmpty(pass) || pass.length() < 6) { etPassword.setError("Min 6 characters"); return; }
        if (!pass.equals(confirm)) { etConfirmPassword.setError("Passwords don't match"); return; }
        if (TextUtils.isEmpty(address)) { etAddress.setError("Enter address"); return; }

        if (db.isPhoneExists(phone)) {
            etPhone.setError("Phone already registered");
            return;
        }

        long userId = db.registerUser(name, phone, pass, role, address);
        if (userId == -1) {
            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        session.saveSession((int) userId, name, phone, role);
        Intent intent;
        if (User.ROLE_DEALER.equals(role)) {
            intent = new Intent(this, DealerDashboardActivity.class);
        } else {
            intent = new Intent(this, UserDashboardActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}

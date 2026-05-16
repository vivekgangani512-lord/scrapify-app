package com.example.scrapbid.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrapbid.DatabaseHelper;
import com.example.scrapbid.R;
import com.example.scrapbid.SessionManager;
import com.example.scrapbid.adapter.CategoryAdapter;
import com.example.scrapbid.model.ScrapCategory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AddScrapActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private TextInputEditText etWeight, etPrice, etAddress, etDescription;
    private TextView tvSuggestedPrice, tvCategoryInfo;
    private MaterialButton btnSubmit;
    private ScrapCategory selectedCategory;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scrap);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Upload Scrap");
        }

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        rvCategories = findViewById(R.id.rv_categories);
        etWeight = findViewById(R.id.et_weight);
        etPrice = findViewById(R.id.et_price);
        etAddress = findViewById(R.id.et_address);
        etDescription = findViewById(R.id.et_description);
        tvSuggestedPrice = findViewById(R.id.tv_suggested_price);
        tvCategoryInfo = findViewById(R.id.tv_category_info);
        btnSubmit = findViewById(R.id.btn_submit);

        // Load categories
        List<ScrapCategory> categories = db.getAllCategories();
        rvCategories.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        CategoryAdapter adapter = new CategoryAdapter(categories, cat -> {
            selectedCategory = cat;
            tvCategoryInfo.setText(cat.name + " – Base price ₹" + (int) cat.basePricePerKg + "/kg");
            updateSuggestedPrice();
        });
        rvCategories.setAdapter(adapter);

        // Auto-calculate suggested price when weight changes
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) { updateSuggestedPrice(); }
        });

        btnSubmit.setOnClickListener(v -> submitScrap());
    }

    private void updateSuggestedPrice() {
        if (selectedCategory == null) return;
        String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
        if (TextUtils.isEmpty(weightStr)) {
            tvSuggestedPrice.setText("");
            return;
        }
        try {
            double weight = Double.parseDouble(weightStr);
            double suggested = weight * selectedCategory.basePricePerKg;
            tvSuggestedPrice.setText("Suggested: ₹" + (int) suggested);
            if (etPrice.getText() == null || etPrice.getText().toString().trim().isEmpty()) {
                etPrice.setText(String.valueOf((int) suggested));
            }
        } catch (NumberFormatException ignored) {}
    }

    private void submitScrap() {
        if (selectedCategory == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (TextUtils.isEmpty(weightStr)) { etWeight.setError("Enter weight"); return; }
        if (TextUtils.isEmpty(priceStr)) { etPrice.setError("Enter price"); return; }
        if (TextUtils.isEmpty(address)) { etAddress.setError("Enter address"); return; }

        double weight, price;
        try {
            weight = Double.parseDouble(weightStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (weight <= 0) { etWeight.setError("Weight must be > 0"); return; }
        if (price <= 0) { etPrice.setError("Price must be > 0"); return; }

        long id = db.addScrap(session.getUserId(), selectedCategory.id,
                selectedCategory.name, weight, price, address, desc);

        if (id > 0) {
            Toast.makeText(this, "Scrap uploaded successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to upload. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}

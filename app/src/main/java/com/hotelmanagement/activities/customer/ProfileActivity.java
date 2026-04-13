package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        LinearLayout navSearch = findViewById(R.id.navSearch);
        LinearLayout navWishlist = findViewById(R.id.navWishlist);

        // Điều hướng footer bằng finish() để quay về Home với hiệu ứng Back mượt mà
        navSearch.setOnClickListener(v -> {
            finish();
        });

        navWishlist.setOnClickListener(v -> {
            startActivity(new Intent(this, WishlistActivity.class));
            finish();
        });
    }
}

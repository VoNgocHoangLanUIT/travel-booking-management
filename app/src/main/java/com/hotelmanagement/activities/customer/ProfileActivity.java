package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navWishlist = findViewById(R.id.navWishlist);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Điều hướng footer bằng finish() để quay về Home với hiệu ứng Back mượt mà
        navHome.setOnClickListener(v -> {
            finish();
        });

        navWishlist.setOnClickListener(v -> {
            startActivity(new Intent(this, WishlistActivity.class));
            finish();
        });

        // XỬ LÝ ĐĂNG XUẤT
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInActivity.class);
            // Xóa sạch lịch sử các màn hình để quay về trang Đăng nhập
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}

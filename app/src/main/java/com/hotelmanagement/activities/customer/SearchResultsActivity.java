package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hotelmanagement.R;
import com.hotelmanagement.adapters.SearchResultAdapter;
import com.hotelmanagement.models.Room;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ImageView btnBack = findViewById(R.id.btnBackResults);
        RecyclerView rvSearchResults = findViewById(R.id.rvSearchResults);
        
        LinearLayout navWishlist = findViewById(R.id.navWishlist);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navHome = findViewById(R.id.navHome);

        btnBack.setOnClickListener(v -> finish());

        // ĐIỀU HƯỚNG FOOTER
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        navWishlist.setOnClickListener(v -> {
            startActivity(new Intent(this, WishlistActivity.class));
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Thiết lập RecyclerView hiển thị theo chiều dọc (Vertical)
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));

        // Load dữ liệu giả lập cho kết quả tìm kiếm
        List<Room> searchResults = new ArrayList<>();
        searchResults.add(new Room(
                R.drawable.dalat_1,
                "Skyline Pool Villa Đà Lạt",
                "Căn hộ Studio áp mái view thành phố",
                "đ10.000.000",
                "Được khách yêu thích",
                false
        ));
        searchResults.add(new Room(
                R.drawable.dalat_2,
                "Modern Luxury Suite Đà Lạt",
                "Phòng suite hiện đại đầy đủ tiện nghi",
                "đ6.000.000",
                "Được khách yêu thích",
                false
        ));
        searchResults.add(new Room(
                R.drawable.vungtau_2,
                "Ocean View Vũng Tàu",
                "View biển cực đẹp, thoáng mát",
                "đ8.000.000",
                "",
                false
        ));

        // Sử dụng SearchResultAdapter mới tạo
        rvSearchResults.setAdapter(new SearchResultAdapter(this, searchResults));
    }
}

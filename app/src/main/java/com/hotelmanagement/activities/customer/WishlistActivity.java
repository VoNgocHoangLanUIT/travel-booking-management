package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hotelmanagement.R;
import com.hotelmanagement.adapters.WishlistAdapter;
import com.hotelmanagement.models.Room;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        RecyclerView rvWishlist = findViewById(R.id.rvWishlist);
        LinearLayout navSearch = findViewById(R.id.navSearch);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        TextView tvWishlistCount = findViewById(R.id.tvWishlistCount);

        // Điều hướng footer
        // Thay vì dùng startActivity (tạo hiệu ứng mở mới), ta dùng finish() 
        // để quay về Home với hiệu ứng "Back" (trượt sang phải)
        navSearch.setOnClickListener(v -> {
            finish();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });

        // Thiết lập RecyclerView
        rvWishlist.setLayoutManager(new LinearLayoutManager(this));

        // Dữ liệu giả lập cho danh sách yêu thích
        List<Room> favoriteRooms = new ArrayList<>();
        favoriteRooms.add(new Room(
                R.drawable.vungtau_1,
                "Luxury Suite Vũng Tàu",
                "Phòng suite sang trọng hướng biển",
                "5.000.000đ",
                "Được khách yêu thích",
                true
        ));
        favoriteRooms.add(new Room(
                R.drawable.dalat_1,
                "Skyline Pool Villa Đà Lạt",
                "Biệt thự sân thượng có hồ bơi riêng",
                "10.000.000đ",
                "Được khách yêu thích",
                true
        ));
        favoriteRooms.add(new Room(
                R.drawable.quynhon_1,
                "Quy Nhơn Beach Hotel",
                "Phòng view biển cực đẹp",
                "14.000.000đ",
                "",
                true
        ));

        // Cập nhật số lượng
        tvWishlistCount.setText(favoriteRooms.size() + " chỗ ở đã lưu");

        // Sử dụng WishlistAdapter riêng
        rvWishlist.setAdapter(new WishlistAdapter(this, favoriteRooms));
    }
}

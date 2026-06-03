package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hotelmanagement.R;
import com.hotelmanagement.adapters.WishlistAdapter;
import com.hotelmanagement.database.entities.UserEntity;
import com.hotelmanagement.services.FavoriteService;
import com.hotelmanagement.services.UserService;
import com.hotelmanagement.models.Room;

import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        RecyclerView rvWishlist = findViewById(R.id.rvWishlist);
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        TextView tvWishlistCount = findViewById(R.id.tvWishlistCount);

        // Điều hướng footer
        // Sử dụng finish() để quay về Home với hiệu ứng Back
        navHome.setOnClickListener(v -> {
            finish();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });

        // Thiết lập RecyclerView
        rvWishlist.setLayoutManager(new LinearLayoutManager(this));

        FavoriteService favoriteService = new FavoriteService(this);
        UserService userService = new UserService(this);
        UserEntity currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem danh sách yêu thích", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        long userId = currentUser.id;

        List<Room> favoriteRooms = favoriteService.getFavoriteRoomModels(userId);

        tvWishlistCount.setText(favoriteRooms.size() + " chỗ ở đã lưu");
        rvWishlist.setAdapter(new WishlistAdapter(this, favoriteRooms));
    }
}

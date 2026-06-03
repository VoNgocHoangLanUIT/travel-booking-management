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
import com.hotelmanagement.adapters.SearchResultAdapter;
import com.hotelmanagement.models.Room;
import com.hotelmanagement.services.BookingService;
import com.hotelmanagement.services.RoomService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ImageView btnBack = findViewById(R.id.btnBackResults);
        TextView tvSearchLocation = findViewById(R.id.tvSearchLocation);
        TextView tvSearchDetails = findViewById(R.id.tvSearchDetails);
        RecyclerView rvSearchResults = findViewById(R.id.rvSearchResults);
        TextView tvSearchEmpty = findViewById(R.id.tvSearchEmpty);
        LinearLayout headerSearchSummary = findViewById(R.id.headerSearchSummary);
        
        LinearLayout navWishlist = findViewById(R.id.navWishlist);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navHome = findViewById(R.id.navHome);

        btnBack.setOnClickListener(v -> finish());
        headerSearchSummary.setOnClickListener(v -> finish());

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

        // Thiết lập RecyclerView hiển thị theo chiều dọc
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));

        RoomService roomService = new RoomService(this);
        BookingService bookingService = new BookingService(this);

        int guestCount = getIntent() != null ? getIntent().getIntExtra("GUEST_COUNT", 1) : 1;
        String city = getIntent() != null ? getIntent().getStringExtra("CITY") : null;
        String checkInText = getIntent() != null ? getIntent().getStringExtra("CHECK_IN_TEXT") : null;
        String checkOutText = getIntent() != null ? getIntent().getStringExtra("CHECK_OUT_TEXT") : null;
        long checkInMillis = getIntent() != null ? getIntent().getLongExtra("CHECK_IN_MILLIS", -1L) : -1L;
        long checkOutMillis = getIntent() != null ? getIntent().getLongExtra("CHECK_OUT_MILLIS", -1L) : -1L;

        tvSearchLocation.setText(city == null || city.trim().isEmpty() ? "Tất cả chỗ ở" : "Chỗ ở tại " + city);
        tvSearchDetails.setText((checkInText == null ? "" : checkInText)
                + (checkOutText == null || checkOutText.isEmpty() ? "" : " - " + checkOutText)
                + " · " + guestCount + " khách");

        // Load dữ liệu từ database theo điều kiện tìm kiếm hiện tại.
        List<Room> searchResults = roomService.searchPublishedRoomModels(city, guestCount);
        searchResults = filterAvailableRooms(searchResults, bookingService, checkInMillis, checkOutMillis);

        if (searchResults.isEmpty()) {
            tvSearchEmpty.setVisibility(android.view.View.VISIBLE);
            rvSearchResults.setVisibility(android.view.View.GONE);
        } else {
            tvSearchEmpty.setVisibility(android.view.View.GONE);
            rvSearchResults.setVisibility(android.view.View.VISIBLE);
        }

        // Sử dụng SearchResultAdapter mới tạo
        rvSearchResults.setAdapter(new SearchResultAdapter(this, searchResults, guestCount, checkInText, checkOutText, checkInMillis, checkOutMillis));
    }

    private List<Room> filterAvailableRooms(
            List<Room> rooms,
            BookingService bookingService,
            long checkInMillis,
            long checkOutMillis
    ) {
        if (rooms == null || rooms.isEmpty()) {
            return new ArrayList<>();
        }

        if (checkInMillis <= 0 || checkOutMillis <= checkInMillis) {
            return rooms;
        }

        Date checkInDate = new Date(checkInMillis);
        Date checkOutDate = new Date(checkOutMillis);
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room != null && bookingService.isRoomAvailable(room.getId(), checkInDate, checkOutDate)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }
}

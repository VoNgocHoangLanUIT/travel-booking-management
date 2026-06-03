package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hotelmanagement.R;
import com.hotelmanagement.adapters.RoomAdapter;
import com.hotelmanagement.database.AppDatabase;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.models.Room;

import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvVungTau;
    private RecyclerView rvDaLat;
    private RecyclerView rvQuyNhon;
    private AppDatabase database;

    private final Locale viLocale = new Locale("vi", "VN");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = AppDatabase.getInstance(this);

        LinearLayout searchBar = findViewById(R.id.searchBar);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navWishlist = findViewById(R.id.navWishlist);

        rvVungTau = findViewById(R.id.rvVungTau);
        rvDaLat = findViewById(R.id.rvDaLat);
        rvQuyNhon = findViewById(R.id.rvQuyNhon);

        setupRecyclerViews();

        searchBar.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        navWishlist.setOnClickListener(v ->
                startActivity(new Intent(this, WishlistActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoomData();
    }

    private void setupRecyclerViews() {
        rvVungTau.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDaLat.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvQuyNhon.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void loadRoomData() {
        rvVungTau.setAdapter(new RoomAdapter(this, getRoomModelsByCity("Vũng Tàu")));
        rvDaLat.setAdapter(new RoomAdapter(this, getRoomModelsByCity("Đà Lạt")));
        rvQuyNhon.setAdapter(new RoomAdapter(this, getRoomModelsByCity("Quy Nhơn")));
    }

    private List<Room> getRoomModelsByCity(String city) {
        List<RoomEntity> entities = database.roomDao().getPublishedByCity(city);
        List<Room> rooms = new ArrayList<>();

        for (RoomEntity entity : entities) {
            rooms.add(toRoomModel(entity));
        }

        return rooms;
    }

    private Room toRoomModel(RoomEntity entity) {
        String title = entity.title == null ? "" : entity.title;

        String meta = entity.maxGuests + " Người";
        if (entity.rating > 0) {
            meta += " • ★ " + String.format(Locale.US, "%.2f", entity.rating);
        } else if (entity.city != null && !entity.city.trim().isEmpty()) {
            meta += " • " + entity.city.trim();
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(viLocale);
        String price = formatter.format(entity.pricePerNight) + "đ / đêm";

        String badge = entity.badge == null ? "" : entity.badge;

        int imageResId = entity.imageResId != 0
                ? entity.imageResId
                : getDefaultImageByCity(entity.city);

        return new Room(
                entity.id,
                entity.imageUri,
                imageResId,
                title,
                meta,
                price,
                badge,
                false
        );
    }

    private int getDefaultImageByCity(String city) {
        String key = normalizeKey(city);

        if (key.contains("dalat")) {
            return R.drawable.dalat_1;
        }

        if (key.contains("quynhon")) {
            return R.drawable.quynhon_1;
        }

        return R.drawable.vungtau_1;
    }

    private String normalizeKey(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        normalized = normalized.replace("Đ", "D").replace("đ", "d");

        return normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }
}

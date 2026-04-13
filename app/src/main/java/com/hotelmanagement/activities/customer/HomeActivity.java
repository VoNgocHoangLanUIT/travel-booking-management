package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hotelmanagement.R;
import com.hotelmanagement.adapters.RoomAdapter;
import com.hotelmanagement.models.Room;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvVungTau;
    private RecyclerView rvDaLat;
    private RecyclerView rvQuyNhon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout searchBar = findViewById(R.id.searchBar);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        LinearLayout navWishlist = findViewById(R.id.navWishlist);

        rvVungTau = findViewById(R.id.rvVungTau);
        rvDaLat = findViewById(R.id.rvDaLat);
        rvQuyNhon = findViewById(R.id.rvQuyNhon);

        setupRecyclerViews();
        loadRoomData();

        searchBar.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        navWishlist.setOnClickListener(v ->
                startActivity(new Intent(this, WishlistActivity.class)));
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
        List<Room> vungTauList = new ArrayList<>();
        vungTauList.add(new Room(
                R.drawable.vungtau_1,
                "Luxury Suite Vũng Tàu",
                "2 Người • ★ 4.95",
                "5.000.000đ",
                "",
                false
        ));
        vungTauList.add(new Room(
                R.drawable.vungtau_2,
                "Ocean View Vũng Tàu",
                "2 Người • ★ 4.90",
                "8.000.000đ",
                "",
                false
        ));
        vungTauList.add(new Room(
                R.drawable.vungtau_3,
                "Studio Retreat Vũng Tàu",
                "2 Người • ★ 4.96",
                "7.000.000đ",
                "",
                false
        ));

        List<Room> daLatList = new ArrayList<>();
        daLatList.add(new Room(
                R.drawable.dalat_1,
                "Skyline Pool Villa Đà Lạt",
                "4 Người • ★ 4.95",
                "10.000.000đ",
                "Được khách yêu thích",
                false
        ));
        daLatList.add(new Room(
                R.drawable.dalat_2,
                "Modern Luxury Suite Đà Lạt",
                "2 Người • ★ 4.95",
                "6.000.000đ",
                "Được khách yêu thích",
                false
        ));

        List<Room> quyNhonList = new ArrayList<>();
        quyNhonList.add(new Room(
                R.drawable.quynhon_1,
                "Quy Nhơn Beach Hotel",
                "6 Người • ★ 4.95",
                "14.000.000đ",
                "",
                false
        ));
        quyNhonList.add(new Room(
                R.drawable.quynhon_2,
                "FLC Luxury Quy Nhơn",
                "2 Người • ★ 4.95",
                "3.000.000đ",
                "",
                false
        ));

        rvVungTau.setAdapter(new RoomAdapter(this, vungTauList));
        rvDaLat.setAdapter(new RoomAdapter(this, daLatList));
        rvQuyNhon.setAdapter(new RoomAdapter(this, quyNhonList));
    }
}

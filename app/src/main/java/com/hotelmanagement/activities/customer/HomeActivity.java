package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout searchBar = findViewById(R.id.searchBar);

        LinearLayout vtCard1 = findViewById(R.id.vtCard1);
        LinearLayout vtCard2 = findViewById(R.id.vtCard2);
        LinearLayout vtCard3 = findViewById(R.id.vtCard3);

        LinearLayout dlCard1 = findViewById(R.id.dlCard1);
        LinearLayout dlCard2 = findViewById(R.id.dlCard2);

        LinearLayout qnCard1 = findViewById(R.id.qnCard1);
        LinearLayout qnCard2 = findViewById(R.id.qnCard2);

        LinearLayout navProfile = findViewById(R.id.navProfile);

        searchBar.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));

        vtCard1.setOnClickListener(v ->
                startActivity(new Intent(this, RoomDetailActivity.class)));
        vtCard2.setOnClickListener(v ->
                startActivity(new Intent(this, RoomDetailActivity.class)));
        vtCard3.setOnClickListener(v ->
                startActivity(new Intent(this, RoomDetailActivity.class)));

        dlCard1.setOnClickListener(v ->
                startActivity(new Intent(this, RoomDetailActivity.class)));
        dlCard2.setOnClickListener(v ->
                startActivity(new Intent(this, RoomDetailActivity.class)));

        qnCard1.setOnClickListener(v ->
                startActivity(new Intent(this, RoomDetailActivity.class)));
        qnCard2.setOnClickListener(v ->
                startActivity(new Intent(this, RoomDetailActivity.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}
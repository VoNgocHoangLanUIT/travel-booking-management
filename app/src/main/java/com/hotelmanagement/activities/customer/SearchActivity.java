package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;

public class SearchActivity extends AppCompatActivity {

    private int guestCount = 2;
    private TextView tvGuestCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TextView btnBack = findViewById(R.id.btnBackSearch);
        TextView btnMinus = findViewById(R.id.btnMinusGuest);
        TextView btnPlus = findViewById(R.id.btnPlusGuest);
        tvGuestCount = findViewById(R.id.tvGuestCount);
        Button btnSearch = findViewById(R.id.btnSearchRoom);

        btnBack.setOnClickListener(v -> finish());

        btnMinus.setOnClickListener(v -> {
            if (guestCount > 1) {
                guestCount--;
                tvGuestCount.setText(String.valueOf(guestCount));
            }
        });

        btnPlus.setOnClickListener(v -> {
            guestCount++;
            tvGuestCount.setText(String.valueOf(guestCount));
        });

        btnSearch.setOnClickListener(v ->
                startActivity(new Intent(this, RoomDetailActivity.class)));
    }
}
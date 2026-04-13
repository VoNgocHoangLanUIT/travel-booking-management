package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;

public class RoomDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        TextView btnBack = findViewById(R.id.btnBackRoomDetail);
        Button btnReserve = findViewById(R.id.btnReserveRoom);

        btnBack.setOnClickListener(v -> finish());
        btnReserve.setOnClickListener(v ->
                startActivity(new Intent(this, BookingActivity.class)));
    }
}
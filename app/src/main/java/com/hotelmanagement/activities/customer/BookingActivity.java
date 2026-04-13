package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;

public class BookingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        TextView btnBack = findViewById(R.id.btnBackBooking);
        Button btnConfirm = findViewById(R.id.btnConfirmBooking);

        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v ->
                startActivity(new Intent(this, BookingSuccessActivity.class)));
    }
}
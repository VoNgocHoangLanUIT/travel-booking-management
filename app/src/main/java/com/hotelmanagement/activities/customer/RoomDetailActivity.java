package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;

public class RoomDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        ImageView imgRoomDetail = findViewById(R.id.imgRoomDetail);
        TextView tvTitleDetail = findViewById(R.id.tvTitleDetail);
        TextView tvMetaDetail = findViewById(R.id.tvMetaDetail);
        TextView tvPriceDetail = findViewById(R.id.tvPriceDetail);
        ImageView btnBack = findViewById(R.id.btnBackRoomDetail);
        Button btnReserve = findViewById(R.id.btnReserveRoom);

        // NHẬN DỮ LIỆU TỪ INTENT
        Intent intent = getIntent();
        String title = "";
        String price = "";
        
        if (intent != null) {
            int imageRes = intent.getIntExtra("ROOM_IMAGE", R.drawable.vungtau_1);
            title = intent.getStringExtra("ROOM_TITLE");
            String meta = intent.getStringExtra("ROOM_META");
            price = intent.getStringExtra("ROOM_PRICE");

            imgRoomDetail.setImageResource(imageRes);
            if (title != null) tvTitleDetail.setText(title);
            if (meta != null) tvMetaDetail.setText(meta);
            if (price != null) tvPriceDetail.setText(price + " / đêm");
        }

        btnBack.setOnClickListener(v -> finish());
        
        // TRUYỀN DỮ LIỆU TIẾP SANG TRANG BOOKING
        final String finalTitle = title;
        final String finalPrice = price;
        btnReserve.setOnClickListener(v -> {
            Intent bookingIntent = new Intent(this, BookingActivity.class);
            bookingIntent.putExtra("ROOM_TITLE", finalTitle);
            bookingIntent.putExtra("ROOM_PRICE", finalPrice);
            startActivity(bookingIntent);
        });
    }
}

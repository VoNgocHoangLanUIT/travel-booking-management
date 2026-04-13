package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;

public class BookingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        ImageView btnBack = findViewById(R.id.btnBackBooking);
        Button btnConfirm = findViewById(R.id.btnConfirmBooking);
        
        TextView tvRoomTitle = findViewById(R.id.tvBookingRoomTitle);
        TextView tvBasePrice = findViewById(R.id.tvBookingBasePrice);
        TextView tvTotalPrice = findViewById(R.id.tvBookingTotalPrice);

        // NHẬN DỮ LIỆU TỪ INTENT (Truyền từ Detail sang)
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("ROOM_TITLE");
            String price = intent.getStringExtra("ROOM_PRICE");

            if (title != null) tvRoomTitle.setText(title);
            if (price != null) {
                tvBasePrice.setText(price + " x 1 đêm");
                // Giả lập tính tổng (Xóa ký tự đ và dấu chấm để parse số)
                try {
                    String cleanPrice = price.replace("đ", "").replace(".", "").trim();
                    long basePrice = Long.parseLong(cleanPrice);
                    long total = basePrice + 50000 + 20000; // Cộng thêm phí vệ sinh và dịch vụ
                    tvTotalPrice.setText("Tổng: " + formatPrice(total) + "đ");
                } catch (Exception e) {
                    tvTotalPrice.setText("Tổng: " + price);
                }
            }
        }

        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v ->
                startActivity(new Intent(this, BookingSuccessActivity.class)));
    }

    private String formatPrice(long price) {
        return String.format("%,d", price).replace(',', '.');
    }
}

package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.ReviewEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.models.Room;
import com.hotelmanagement.services.BookingService;
import com.hotelmanagement.services.ReviewService;
import com.hotelmanagement.services.RoomService;
import com.hotelmanagement.services.mappers.RoomMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd 'thg' M, yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        ImageView btnBack = findViewById(R.id.btnBackBookingDetail);
        ImageView ivRoom = findViewById(R.id.ivBookingDetailRoom);
        TextView tvTitle = findViewById(R.id.tvBookingDetailTitle);
        TextView tvAddress = findViewById(R.id.tvBookingDetailAddress);
        TextView tvStatus = findViewById(R.id.tvBookingDetailStatus);
        TextView tvCheckIn = findViewById(R.id.tvBookingDetailCheckIn);
        TextView tvCheckOut = findViewById(R.id.tvBookingDetailCheckOut);
        TextView tvGuests = findViewById(R.id.tvBookingDetailGuests);
        TextView tvNights = findViewById(R.id.tvBookingDetailNights);
        TextView tvTotal = findViewById(R.id.tvBookingDetailTotal);
        Button btnReview = findViewById(R.id.btnBookingDetailReview);

        BookingService bookingService = new BookingService(this);
        RoomService roomService = new RoomService(this);
        ReviewService reviewService = new ReviewService(this);
        RoomMapper roomMapper = new RoomMapper();

        long bookingId = getIntent() != null ? getIntent().getLongExtra("BOOKING_ID", 0L) : 0L;
        BookingEntity booking = bookingId > 0 ? bookingService.getBookingById(bookingId) : null;
        if (booking == null) {
            Toast.makeText(this, "Không tìm thấy đặt phòng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        RoomEntity room = roomService.getRoomById(booking.roomId);
        if (room != null) {
            Room roomModel = roomMapper.fromEntity(room);
            tvTitle.setText(roomModel.getTitle());
            tvAddress.setText(buildAddress(room));
            if (roomModel.getImageUri() != null && !roomModel.getImageUri().trim().isEmpty()) {
                ivRoom.setImageURI(Uri.parse(roomModel.getImageUri()));
            } else {
                ivRoom.setImageResource(roomModel.getImageResId());
            }
        } else {
            tvTitle.setText("Phòng đã đặt");
            tvAddress.setText("Thông tin phòng không còn khả dụng");
            ivRoom.setImageResource(R.drawable.vungtau_1);
        }

        tvStatus.setText(mapStatus(booking.status));
        tvCheckIn.setText(formatDateRange(booking.checkInDate, booking.checkOutDate));
        tvCheckOut.setVisibility(View.GONE);
        tvGuests.setText(booking.guestCount + " khách");
        tvNights.setText(countNights(booking.checkInDate, booking.checkOutDate) + " đêm");
        tvTotal.setText("đ" + formatPrice((long) booking.totalPrice));

        ReviewEntity review = reviewService.getReviewByBooking(booking.id);
        btnReview.setText(review == null ? "Đánh giá chuyến đi" : "Xem / sửa đánh giá");
        btnReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("BOOKING_ID", booking.id);
            startActivity(intent);
        });
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private String buildAddress(RoomEntity room) {
        String address = room.address == null ? "" : room.address.trim();
        String city = room.city == null ? "" : room.city.trim();
        if (address.isEmpty()) {
            return city.isEmpty() ? "Chỗ ở" : city;
        }
        if (city.isEmpty()) {
            return address;
        }
        return address + ", " + city;
    }

    private String mapStatus(String status) {
        if ("completed".equals(status)) {
            return "Đã hoàn thành";
        }
        if ("cancelled".equals(status)) {
            return "Đã hủy";
        }
        if ("accepted".equals(status)) {
            return "Đang ở";
        }
        if ("pending".equals(status)) {
            return "Sắp tới";
        }
        return "Đã thanh toán";
    }

    private String formatDate(Date date) {
        return date == null ? "" : formatter.format(date);
    }

    private String formatDateRange(Date checkIn, Date checkOut) {
        if (checkIn == null || checkOut == null) {
            return "";
        }
        return formatter.format(checkIn) + " - " + formatter.format(checkOut);
    }

    private long countNights(Date checkIn, Date checkOut) {
        if (checkIn == null || checkOut == null) {
            return 0;
        }
        return Math.max(1, (checkOut.getTime() - checkIn.getTime()) / (24L * 60L * 60L * 1000L));
    }

    private String formatPrice(long price) {
        return String.format("%,d", price).replace(',', '.');
    }
}

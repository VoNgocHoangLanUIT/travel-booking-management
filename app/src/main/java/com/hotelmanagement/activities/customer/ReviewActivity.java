package com.hotelmanagement.activities.customer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.ReviewEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.services.BookingService;
import com.hotelmanagement.services.ReviewService;
import com.hotelmanagement.services.RoomService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd 'thg' M, yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ImageView btnBack = findViewById(R.id.btnBackReview);
        TextView tvRoomTitle = findViewById(R.id.tvReviewRoomTitle);
        TextView tvTripInfo = findViewById(R.id.tvReviewTripInfo);
        RatingBar ratingBar = findViewById(R.id.ratingBarReview);
        EditText edtComment = findViewById(R.id.edtReviewComment);
        Button btnSubmit = findViewById(R.id.btnSubmitReview);
        Button btnDelete = findViewById(R.id.btnDeleteReview);

        BookingService bookingService = new BookingService(this);
        RoomService roomService = new RoomService(this);
        ReviewService reviewService = new ReviewService(this);

        long bookingId = getIntent() != null ? getIntent().getLongExtra("BOOKING_ID", 0L) : 0L;
        BookingEntity booking = bookingId > 0 ? bookingService.getBookingById(bookingId) : null;
        if (booking == null) {
            Toast.makeText(this, "Không tìm thấy đặt phòng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        RoomEntity room = roomService.getRoomById(booking.roomId);
        tvRoomTitle.setText(room != null && room.title != null ? room.title : "Phòng đã đặt");
        tvTripInfo.setText(formatDate(booking.checkInDate) + " - " + formatDate(booking.checkOutDate)
                + " · " + booking.guestCount + " khách");

        ReviewEntity existingReview = reviewService.getReviewByBooking(booking.id);
        if (existingReview != null) {
            ratingBar.setRating(existingReview.rating);
            edtComment.setText(existingReview.comment);
            btnSubmit.setText("Cập nhật đánh giá");
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnBack.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> {
            int rating = Math.round(ratingBar.getRating());
            if (rating <= 0) {
                Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                return;
            }

            ReviewEntity review = existingReview == null ? new ReviewEntity() : existingReview;
            review.bookingId = booking.id;
            review.rating = rating;
            review.comment = edtComment.getText().toString().trim();
            review.createdAt = existingReview == null ? new Date() : existingReview.createdAt;

            if (existingReview == null) {
                reviewService.createReview(review);
                booking.status = "completed";
                booking.updatedAt = new Date();
                bookingService.updateBooking(booking);
            } else {
                reviewService.updateReview(review);
            }

            Toast.makeText(this, "Đã lưu đánh giá", Toast.LENGTH_LONG).show();
            finish();
        });

        btnDelete.setOnClickListener(v -> {
            if (existingReview == null) {
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Xóa đánh giá")
                    .setMessage("Bạn có chắc muốn xóa đánh giá này?")
                    .setNegativeButton("Hủy", null)
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        int deleted = reviewService.deleteReview(existingReview);
                        if (deleted > 0) {
                            booking.status = "paid";
                            booking.updatedAt = new Date();
                            bookingService.updateBooking(booking);
                        }
                        Toast.makeText(this, deleted > 0 ? "Đã xóa đánh giá" : "Không thể xóa đánh giá", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .show();
        });
    }

    private String formatDate(Date date) {
        return date == null ? "" : formatter.format(date);
    }
}

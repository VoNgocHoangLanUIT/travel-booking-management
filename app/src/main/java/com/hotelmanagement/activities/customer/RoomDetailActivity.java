package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.ReviewEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.database.entities.UserEntity;
import com.hotelmanagement.services.BookingService;
import com.hotelmanagement.services.ReviewService;
import com.hotelmanagement.services.RoomService;
import com.hotelmanagement.services.ServiceCatalogService;
import com.hotelmanagement.services.UserService;
import com.hotelmanagement.services.mappers.RoomMapper;

import java.util.List;
import java.util.Locale;

public class RoomDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        ImageView imgRoomDetail = findViewById(R.id.imgRoomDetail);
        TextView tvTitleDetail = findViewById(R.id.tvTitleDetail);
        TextView tvMetaDetail = findViewById(R.id.tvMetaDetail);
        TextView tvPriceDetail = findViewById(R.id.tvPriceDetail);
        LinearLayout cardRoomServices = findViewById(R.id.cardRoomServices);
        TextView tvRoomServices = findViewById(R.id.tvRoomServices);
        LinearLayout cardRoomReviews = findViewById(R.id.cardRoomReviews);
        TextView tvRoomReviewsTitle = findViewById(R.id.tvRoomReviewsTitle);
        LinearLayout layoutRoomReviews = findViewById(R.id.layoutRoomReviews);
        ImageView btnBack = findViewById(R.id.btnBackRoomDetail);
        Button btnReserve = findViewById(R.id.btnReserveRoom);

        RoomService roomService = new RoomService(this);
        BookingService bookingService = new BookingService(this);
        ReviewService reviewService = new ReviewService(this);
        ServiceCatalogService serviceCatalogService = new ServiceCatalogService(this);
        UserService userService = new UserService(this);
        RoomMapper roomMapper = new RoomMapper();

        // NHẬN DỮ LIỆU TỪ INTENT
        Intent intent = getIntent();
        String title = "";
        String price = "";
        String meta = "";
        int imageResId = R.drawable.vungtau_1;
        String imageUri = null;
        long roomId = 0;
        int guestCount = 1;
        String checkInText = null;
        String checkOutText = null;
        long checkInMillis = -1L;
        long checkOutMillis = -1L;

        if (intent != null) {
            roomId = intent.getLongExtra("ROOM_ID", 0L);
            guestCount = intent.getIntExtra("GUEST_COUNT", 1);
            checkInText = intent.getStringExtra("CHECK_IN_TEXT");
            checkOutText = intent.getStringExtra("CHECK_OUT_TEXT");
            checkInMillis = intent.getLongExtra("CHECK_IN_MILLIS", -1L);
            checkOutMillis = intent.getLongExtra("CHECK_OUT_MILLIS", -1L);
        }

        if (roomId > 0) {
            RoomEntity roomEntity = roomService.getRoomById(roomId);
            if (roomEntity != null) {
                com.hotelmanagement.models.Room roomModel = roomMapper.fromEntity(roomEntity);
                imageResId = roomModel.getImageResId();
                imageUri = roomModel.getImageUri();
                title = roomModel.getTitle();
                int reviewCount = reviewService.countReviewsByRoom(roomEntity.id);
                double rating = reviewService.getAverageRatingByRoom(roomEntity.id, roomEntity.rating);
                meta = reviewCount > 0
                        ? String.format(Locale.getDefault(), "★ %.2f (%d đánh giá) · %d khách", rating, reviewCount, roomEntity.maxGuests)
                        : roomModel.getMeta();
                price = roomModel.getPrice();
                String serviceNames = serviceCatalogService.getServiceNamesText(roomEntity.id);
                if (serviceNames == null || serviceNames.trim().isEmpty()) {
                    cardRoomServices.setVisibility(android.view.View.GONE);
                } else {
                    cardRoomServices.setVisibility(android.view.View.VISIBLE);
                    tvRoomServices.setText(serviceNames);
                }
                bindRoomReviews(
                        cardRoomReviews,
                        tvRoomReviewsTitle,
                        layoutRoomReviews,
                        reviewService.getReviewsByRoom(roomEntity.id),
                        bookingService,
                        userService
                );
            }
        } else if (intent != null) {
            imageResId = intent.getIntExtra("ROOM_IMAGE", R.drawable.vungtau_1);
            title = intent.getStringExtra("ROOM_TITLE");
            meta = intent.getStringExtra("ROOM_META");
            price = intent.getStringExtra("ROOM_PRICE");
        }

        if (imageUri != null && !imageUri.trim().isEmpty()) {
            imgRoomDetail.setImageURI(android.net.Uri.parse(imageUri));
        } else {
            imgRoomDetail.setImageResource(imageResId);
        }
        if (title != null) tvTitleDetail.setText(title);
        if (meta != null) tvMetaDetail.setText(meta);
        if (price != null) tvPriceDetail.setText(price + " / đêm");

        btnBack.setOnClickListener(v -> finish());

        // TRUYỀN DỮ LIỆU TIẾP SANG TRANG BOOKING
        final long finalRoomId = roomId;
        final String finalTitle = title;
        final String finalPrice = price;
        final String finalMeta = meta == null ? "" : meta;
        final String finalImageUri = imageUri;
        final int finalImageRes = imageResId;
        final int finalGuestCount = guestCount;
        final String finalCheckInText = checkInText;
        final String finalCheckOutText = checkOutText;
        final long finalCheckInMillis = checkInMillis;
        final long finalCheckOutMillis = checkOutMillis;

        btnReserve.setOnClickListener(v -> {
            Intent bookingIntent = new Intent(this, BookingActivity.class);
            bookingIntent.putExtra("ROOM_ID", finalRoomId);
            bookingIntent.putExtra("ROOM_IMAGE_URI", finalImageUri);
            bookingIntent.putExtra("ROOM_IMAGE", finalImageRes);
            bookingIntent.putExtra("ROOM_TITLE", finalTitle);
            bookingIntent.putExtra("ROOM_PRICE", finalPrice);
            bookingIntent.putExtra("ROOM_META", finalMeta);
            bookingIntent.putExtra("GUEST_COUNT", finalGuestCount);
            bookingIntent.putExtra("CHECK_IN_TEXT", finalCheckInText);
            bookingIntent.putExtra("CHECK_OUT_TEXT", finalCheckOutText);
            bookingIntent.putExtra("CHECK_IN_MILLIS", finalCheckInMillis);
            bookingIntent.putExtra("CHECK_OUT_MILLIS", finalCheckOutMillis);
            startActivity(bookingIntent);
        });
    }

    private void bindRoomReviews(
            LinearLayout cardRoomReviews,
            TextView tvRoomReviewsTitle,
            LinearLayout layoutRoomReviews,
            List<ReviewEntity> reviews,
            BookingService bookingService,
            UserService userService
    ) {
        layoutRoomReviews.removeAllViews();
        if (reviews == null || reviews.isEmpty()) {
            cardRoomReviews.setVisibility(View.GONE);
            return;
        }

        cardRoomReviews.setVisibility(View.VISIBLE);
        tvRoomReviewsTitle.setText("Nhận xét (" + reviews.size() + ")");

        for (int i = 0; i < reviews.size(); i++) {
            ReviewEntity review = reviews.get(i);
            if (review == null) {
                continue;
            }

            if (i > 0) {
                View divider = new View(this);
                divider.setBackgroundColor(0xFFEAEAEA);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );
                dividerParams.setMargins(0, dp(12), 0, dp(12));
                layoutRoomReviews.addView(divider, dividerParams);
            }

            TextView tvReviewer = new TextView(this);
            tvReviewer.setText(resolveReviewerName(review, bookingService, userService)
                    + " · ★ " + review.rating);
            tvReviewer.setTextColor(0xFF111111);
            tvReviewer.setTextSize(14);
            tvReviewer.setTypeface(null, android.graphics.Typeface.BOLD);
            layoutRoomReviews.addView(tvReviewer);

            TextView tvComment = new TextView(this);
            tvComment.setText(
                    review.comment == null || review.comment.trim().isEmpty()
                            ? "Không có nhận xét bằng chữ."
                            : review.comment.trim()
            );
            tvComment.setTextColor(0xFF555555);
            tvComment.setTextSize(14);
            tvComment.setLineSpacing(dp(2), 1.0f);
            LinearLayout.LayoutParams commentParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            commentParams.setMargins(0, dp(6), 0, 0);
            layoutRoomReviews.addView(tvComment, commentParams);
        }
    }

    private String resolveReviewerName(
            ReviewEntity review,
            BookingService bookingService,
            UserService userService
    ) {
        BookingEntity booking = bookingService.getBookingById(review.bookingId);
        if (booking == null) {
            return "Khách";
        }

        UserEntity guest = userService.getUserById(booking.guestId);
        if (guest == null || guest.fullName == null || guest.fullName.trim().isEmpty()) {
            return "Khách";
        }

        return guest.fullName.trim();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}

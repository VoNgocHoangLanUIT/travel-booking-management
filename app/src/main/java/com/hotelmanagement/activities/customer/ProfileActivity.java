package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;
import com.hotelmanagement.adapters.ProfileBookingHistoryAdapter;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.ReviewEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.database.entities.UserEntity;
import com.hotelmanagement.services.BookingService;
import com.hotelmanagement.services.ReviewService;
import com.hotelmanagement.services.RoomService;
import com.hotelmanagement.services.UserService;
import com.hotelmanagement.services.mappers.RoomMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navWishlist = findViewById(R.id.navWishlist);
        Button btnLogout = findViewById(R.id.btnLogout);
        ImageView btnNotifications = findViewById(R.id.btnNotifications);
        LinearLayout cardBecomeHost = findViewById(R.id.cardBecomeHost);
        LinearLayout cardHostDashboard = findViewById(R.id.cardHostDashboard);
        androidx.recyclerview.widget.RecyclerView rvHistory = findViewById(R.id.rvProfileBookingHistory);
        TextView tvHistoryEmpty = findViewById(R.id.tvProfileBookingEmpty);
        TextView tvProfileInitials = findViewById(R.id.tvProfileInitials);
        TextView tvProfileName = findViewById(R.id.tvProfileName);
        TextView tvProfileRole = findViewById(R.id.tvProfileRole);
        TextView tvProfileTripCount = findViewById(R.id.tvProfileTripCount);
        TextView tvProfileAverageRating = findViewById(R.id.tvProfileAverageRating);
        TextView tvProfileCommentCount = findViewById(R.id.tvProfileCommentCount);
        ImageView btnShowAllBookingHistory = findViewById(R.id.btnShowAllBookingHistory);

        RoomService roomService = new RoomService(this);
        BookingService bookingService = new BookingService(this);
        ReviewService reviewService = new ReviewService(this);
        UserService userService = new UserService(this);
        RoomMapper roomMapper = new RoomMapper();

        UserEntity guest = userService.getCurrentUser();
        if (guest == null) {
            Intent intent = new Intent(this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }
        long guestId = guest.id;
        List<BookingEntity> bookings = bookingService.getBookingsByGuest(guestId);
        List<ReviewEntity> receivedReviews = getReceivedReviews(roomService.getRoomsByHost(guestId), reviewService);

        tvProfileName.setText(guest.fullName != null ? guest.fullName : "Khách");
        tvProfileRole.setText(guest.isHost ? "Chủ nhà" : "Khách du lịch");
        tvProfileInitials.setText(buildInitials(guest.fullName));
        tvProfileTripCount.setText(String.valueOf(bookings.size()));
        tvProfileAverageRating.setText(formatAverageRating(receivedReviews));
        tvProfileCommentCount.setText(String.valueOf(countComments(receivedReviews)));

        rvHistory.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        List<BookingEntity> visibleBookings = getRecentBookings(bookings, 3);
        rvHistory.setAdapter(new ProfileBookingHistoryAdapter(visibleBookings, roomService, roomMapper));
        btnShowAllBookingHistory.setVisibility(bookings.size() > 3 ? android.view.View.VISIBLE : android.view.View.GONE);
        btnShowAllBookingHistory.setOnClickListener(v -> {
            rvHistory.setAdapter(new ProfileBookingHistoryAdapter(bookings, roomService, roomMapper));
            btnShowAllBookingHistory.setVisibility(android.view.View.GONE);
        });

        if (bookings.isEmpty()) {
            tvHistoryEmpty.setVisibility(android.view.View.VISIBLE);
            rvHistory.setVisibility(android.view.View.GONE);
        } else {
            tvHistoryEmpty.setVisibility(android.view.View.GONE);
            rvHistory.setVisibility(android.view.View.VISIBLE);
        }

        // ĐIỀU HƯỚNG SANG TRANG TRỞ THÀNH HOST (ĐĂNG PHÒNG)
        cardBecomeHost.setOnClickListener(v -> {
            startActivity(new Intent(this, HostAddRoomActivity.class));
        });

        // ĐIỀU HƯỚNG SANG BẢNG ĐIỀU KHIỂN HOST QUẢN LÝ
        cardHostDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, HostDashboardActivity.class));
        });

        // ĐIỀU HƯỚNG ĐẾN TRANG THÔNG BÁO
        btnNotifications.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        // Điều hướng footer bằng finish() để quay về Home với hiệu ứng Back mượt mà
        navHome.setOnClickListener(v -> {
            finish();
        });

        navWishlist.setOnClickListener(v -> {
            startActivity(new Intent(this, WishlistActivity.class));
            finish();
        });

        // XỬ LÝ ĐĂNG XUẤT
        btnLogout.setOnClickListener(v -> {
            userService.signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            // Xóa sạch lịch sử các màn hình để quay về trang Đăng nhập
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private String buildInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "KH";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(new Locale("vi", "VN"));
        }
        String first = parts[0].substring(0, 1);
        String last = parts[parts.length - 1].substring(0, 1);
        return (first + last).toUpperCase(new Locale("vi", "VN"));
    }

    private List<BookingEntity> getRecentBookings(List<BookingEntity> bookings, int limit) {
        if (bookings == null || bookings.isEmpty()) {
            return new ArrayList<>();
        }

        int endIndex = Math.min(Math.max(0, limit), bookings.size());
        return new ArrayList<>(bookings.subList(0, endIndex));
    }

    private List<ReviewEntity> getReceivedReviews(List<RoomEntity> hostedRooms, ReviewService reviewService) {
        List<ReviewEntity> receivedReviews = new ArrayList<>();
        if (hostedRooms == null || hostedRooms.isEmpty()) {
            return receivedReviews;
        }

        for (RoomEntity room : hostedRooms) {
            if (room != null && room.id > 0) {
                receivedReviews.addAll(reviewService.getReviewsByRoom(room.id));
            }
        }
        return receivedReviews;
    }

    private String formatAverageRating(List<ReviewEntity> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return "0.0";
        }

        int totalRating = 0;
        int ratingCount = 0;
        for (ReviewEntity review : reviews) {
            if (review != null && review.rating > 0) {
                totalRating += review.rating;
                ratingCount++;
            }
        }

        if (ratingCount == 0) {
            return "0.0";
        }

        double average = (double) totalRating / ratingCount;
        return String.format(new Locale("vi", "VN"), "%.1f", average);
    }

    private int countComments(List<ReviewEntity> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }

        int commentCount = 0;
        for (ReviewEntity review : reviews) {
            if (review != null && review.comment != null && !review.comment.trim().isEmpty()) {
                commentCount++;
            }
        }
        return commentCount;
    }
}

package com.hotelmanagement.activities.customer;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.services.BookingService;
import com.hotelmanagement.services.RoomService;
import com.hotelmanagement.services.UserService;

import java.util.List;

public class HostRoomHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_room_history);

        ImageView btnBack = findViewById(R.id.btnBackHistory);
        TextView tvTitle = findViewById(R.id.tvHostRoomHistoryTitle);
        androidx.recyclerview.widget.RecyclerView rvHistory = findViewById(R.id.rvHostRoomHistory);
        TextView tvEmpty = findViewById(R.id.tvHostRoomHistoryEmpty);

        RoomService roomService = new RoomService(this);
        BookingService bookingService = new BookingService(this);
        UserService userService = new UserService(this);

        long roomId = getIntent() != null ? getIntent().getLongExtra("ROOM_ID", 0L) : 0L;
        RoomEntity room = roomId > 0 ? roomService.getRoomById(roomId) : null;
        if (room != null) {
            tvTitle.setText("Khách đã đặt: " + room.title);
        }

        List<BookingEntity> bookings = roomId > 0
                ? bookingService.getBookingsByRoom(roomId)
                : java.util.Collections.emptyList();

        rvHistory.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        com.hotelmanagement.adapters.HostRoomHistoryAdapter adapter =
                new com.hotelmanagement.adapters.HostRoomHistoryAdapter(bookings, userService);
        rvHistory.setAdapter(adapter);

        if (bookings.isEmpty()) {
            tvEmpty.setVisibility(android.view.View.VISIBLE);
            rvHistory.setVisibility(android.view.View.GONE);
        } else {
            tvEmpty.setVisibility(android.view.View.GONE);
            rvHistory.setVisibility(android.view.View.VISIBLE);
        }

        btnBack.setOnClickListener(v -> finish());
    }
}

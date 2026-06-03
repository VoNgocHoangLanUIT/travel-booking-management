package com.hotelmanagement.activities.customer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.database.entities.UserEntity;
import com.hotelmanagement.models.Room;
import com.hotelmanagement.services.BookingService;
import com.hotelmanagement.services.RoomService;
import com.hotelmanagement.services.UserService;
import com.hotelmanagement.services.mappers.RoomMapper;

import java.util.List;

public class HostDashboardActivity extends AppCompatActivity {
    private LinearLayout hostRoomsContainer;
    private TextView tvRoomsEmpty;
    private TextView tvIncome;
    private RoomService roomService;
    private BookingService bookingService;
    private RoomMapper roomMapper;
    private UserEntity host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_dashboard);

        ImageView btnBack = findViewById(R.id.btnBackHostDashboard);
        tvIncome = findViewById(R.id.tvHostIncome);
        tvRoomsEmpty = findViewById(R.id.tvHostRoomsEmpty);
        hostRoomsContainer = findViewById(R.id.hostRoomsContainer);

        roomService = new RoomService(this);
        bookingService = new BookingService(this);
        UserService userService = new UserService(this);
        roomMapper = new RoomMapper();

        host = userService.getCurrentUser();
        if (host == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để quản lý chỗ ở", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (host != null) {
            renderDashboard();
        }
    }

    private void renderDashboard() {
        long hostId = host.id;
        List<RoomEntity> hostRooms = hostId > 0 ? roomService.getRoomsByHost(hostId) : java.util.Collections.emptyList();

        double incomeTotal = 0;
        if (hostId > 0) {
            for (BookingEntity booking : bookingService.getBookingsByHost(hostId)) {
                incomeTotal += booking.totalPrice;
            }
        }
        tvIncome.setText(formatPrice((long) incomeTotal) + " đ");

        hostRoomsContainer.removeAllViews();
        tvRoomsEmpty.setVisibility(hostRooms.isEmpty() ? View.VISIBLE : View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (RoomEntity roomEntity : hostRooms) {
            hostRoomsContainer.addView(createRoomItem(inflater, roomEntity));
        }
    }

    private View createRoomItem(LayoutInflater inflater, RoomEntity roomEntity) {
        View itemView = inflater.inflate(R.layout.item_host_room_manage, hostRoomsContainer, false);
        ImageView imageView = itemView.findViewById(R.id.ivHostRoomImage);
        TextView tvTitle = itemView.findViewById(R.id.tvHostRoomTitle);
        TextView tvPrice = itemView.findViewById(R.id.tvHostRoomPrice);
        TextView btnHistory = itemView.findViewById(R.id.btnHostRoomHistory);
        TextView btnEdit = itemView.findViewById(R.id.btnEditHostRoom);
        TextView btnDelete = itemView.findViewById(R.id.btnDeleteHostRoom);

        Room roomModel = roomMapper.fromEntity(roomEntity);
        bindRoomImage(imageView, roomModel);
        tvTitle.setText(roomModel.getTitle());
        tvPrice.setText(roomModel.getPrice() + " / đêm");

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HostRoomHistoryActivity.class);
            intent.putExtra("ROOM_ID", roomEntity.id);
            startActivity(intent);
        });
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, HostAddRoomActivity.class);
            intent.putExtra("ROOM_ID", roomEntity.id);
            startActivity(intent);
        });
        btnDelete.setOnClickListener(v -> confirmDeleteRoom(roomEntity));
        return itemView;
    }

    private void confirmDeleteRoom(RoomEntity roomEntity) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa phòng")
                .setMessage("Bạn có chắc muốn xóa phòng này? Lịch sử đặt phòng và đánh giá liên quan cũng sẽ bị xóa.")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int deleted = roomService.deleteRoom(roomEntity);
                    Toast.makeText(this, deleted > 0 ? "Đã xóa phòng" : "Không thể xóa phòng", Toast.LENGTH_LONG).show();
                    renderDashboard();
                })
                .show();
    }

    private String formatPrice(long price) {
        return String.format("%,d", price).replace(',', '.');
    }

    private void bindRoomImage(ImageView imageView, Room room) {
        if (room.getImageUri() != null && !room.getImageUri().trim().isEmpty()) {
            imageView.setImageURI(android.net.Uri.parse(room.getImageUri()));
        } else {
            imageView.setImageResource(room.getImageResId());
        }
    }
}

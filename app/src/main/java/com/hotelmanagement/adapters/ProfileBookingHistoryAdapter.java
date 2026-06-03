package com.hotelmanagement.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hotelmanagement.R;
import com.hotelmanagement.activities.customer.BookingDetailActivity;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.models.Room;
import com.hotelmanagement.services.RoomService;
import com.hotelmanagement.services.mappers.RoomMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileBookingHistoryAdapter extends RecyclerView.Adapter<ProfileBookingHistoryAdapter.ViewHolder> {
    private final List<BookingEntity> bookings;
    private final RoomService roomService;
    private final RoomMapper roomMapper;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd 'thg' M", Locale.getDefault());

    public ProfileBookingHistoryAdapter(
            List<BookingEntity> bookings,
            RoomService roomService,
            RoomMapper roomMapper
    ) {
        this.bookings = bookings;
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingEntity booking = bookings.get(position);
        RoomEntity roomEntity = roomService.getRoomById(booking.roomId);
        Room roomModel = roomEntity != null ? roomMapper.fromEntity(roomEntity) : null;

        if (roomModel != null) {
            holder.tvTitle.setText(roomModel.getTitle());
            if (roomModel.getImageUri() != null && !roomModel.getImageUri().trim().isEmpty()) {
                holder.ivRoom.setImageURI(Uri.parse(roomModel.getImageUri()));
            } else {
                holder.ivRoom.setImageResource(roomModel.getImageResId());
            }
        } else {
            holder.tvTitle.setText("Phòng");
            holder.ivRoom.setImageResource(R.drawable.vungtau_1);
        }

        holder.tvDates.setText(formatDateRange(booking.checkInDate, booking.checkOutDate));
        String statusText = mapStatus(booking.status);
        if (statusText.isEmpty()) {
            holder.tvStatus.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(statusText);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), BookingDetailActivity.class);
            intent.putExtra("BOOKING_ID", booking.id);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivRoom;
        final TextView tvTitle;
        final TextView tvDates;
        final TextView tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoom = itemView.findViewById(R.id.ivProfileBookingRoom);
            tvTitle = itemView.findViewById(R.id.tvProfileBookingTitle);
            tvDates = itemView.findViewById(R.id.tvProfileBookingDates);
            tvStatus = itemView.findViewById(R.id.tvProfileBookingStatus);
        }
    }

    private String mapStatus(String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "pending":
                return "Sắp tới";
            case "accepted":
                return "Đang ở";
            case "paid":
                return "";
            case "rejected":
                return "Từ chối";
            case "cancelled":
                return "Đã hủy";
            case "completed":
                return "Đã hoàn thành";
            default:
                return "";
        }
    }

    private String formatDateRange(Date checkIn, Date checkOut) {
        if (checkIn == null || checkOut == null) {
            return "";
        }
        long nights = (checkOut.getTime() - checkIn.getTime()) / (24L * 60L * 60L * 1000L);
        return formatter.format(checkIn) + " - " + formatter.format(checkOut) + " (" + nights + " đêm)";
    }
}


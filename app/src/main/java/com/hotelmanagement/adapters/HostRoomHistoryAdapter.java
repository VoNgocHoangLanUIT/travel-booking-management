package com.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hotelmanagement.R;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.UserEntity;
import com.hotelmanagement.services.UserService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HostRoomHistoryAdapter extends RecyclerView.Adapter<HostRoomHistoryAdapter.ViewHolder> {
    private final List<BookingEntity> bookings;
    private final UserService userService;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd 'thg' M", Locale.getDefault());

    public HostRoomHistoryAdapter(List<BookingEntity> bookings, UserService userService) {
        this.bookings = bookings;
        this.userService = userService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_host_room_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingEntity booking = bookings.get(position);
        UserEntity guest = userService.getUserById(booking.guestId);

        holder.tvGuest.setText(guest != null ? guest.fullName : "Khách");
        String statusText = mapStatus(booking.status);
        if (statusText.isEmpty()) {
            holder.tvStatus.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(statusText);
        }
        holder.tvDates.setText(formatDateRange(booking.checkInDate, booking.checkOutDate));
        holder.tvRevenue.setText("Doanh thu: đ" + formatPrice((long) booking.totalPrice));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvGuest;
        final TextView tvStatus;
        final TextView tvDates;
        final TextView tvRevenue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuest = itemView.findViewById(R.id.tvBookingGuest);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvDates = itemView.findViewById(R.id.tvBookingDates);
            tvRevenue = itemView.findViewById(R.id.tvBookingRevenue);
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
                return "Đã trả phòng";
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

    private String formatPrice(long price) {
        return String.format("%,d", price).replace(',', '.');
    }
}

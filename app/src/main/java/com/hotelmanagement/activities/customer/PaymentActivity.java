package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.NotificationEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.database.entities.UserEntity;
import com.hotelmanagement.services.BookingService;
import com.hotelmanagement.services.NotificationService;
import com.hotelmanagement.services.RoomService;
import com.hotelmanagement.services.UserService;

import java.util.Date;

public class PaymentActivity extends AppCompatActivity {
    private static final String PAYMENT_METHOD = "Thẻ ngân hàng";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ImageView btnBack = findViewById(R.id.btnBackPayment);
        TextView tvRoomTitle = findViewById(R.id.tvPaymentRoomTitle);
        TextView tvTripInfo = findViewById(R.id.tvPaymentTripInfo);
        TextView tvTotal = findViewById(R.id.tvPaymentTotal);
        EditText edtCardNumber = findViewById(R.id.edtCardNumber);
        EditText edtCardHolder = findViewById(R.id.edtCardHolder);
        EditText edtCardExpiry = findViewById(R.id.edtCardExpiry);
        EditText edtCardCvv = findViewById(R.id.edtCardCvv);
        Button btnPayNow = findViewById(R.id.btnPayNow);
        setupExpiryFormatter(edtCardExpiry);

        BookingService bookingService = new BookingService(this);
        NotificationService notificationService = new NotificationService(this);
        RoomService roomService = new RoomService(this);
        UserService userService = new UserService(this);

        Intent intent = getIntent();
        long roomId = intent != null ? intent.getLongExtra("ROOM_ID", 0L) : 0L;
        int guestCount = intent != null ? intent.getIntExtra("GUEST_COUNT", 1) : 1;
        long checkInMillis = intent != null ? intent.getLongExtra("CHECK_IN_MILLIS", -1L) : -1L;
        long checkOutMillis = intent != null ? intent.getLongExtra("CHECK_OUT_MILLIS", -1L) : -1L;
        int nights = intent != null ? intent.getIntExtra("NIGHTS", 1) : 1;
        double totalPrice = intent != null ? intent.getDoubleExtra("TOTAL_PRICE", 0D) : 0D;
        String checkInText = intent != null ? intent.getStringExtra("CHECK_IN_TEXT") : "";
        String checkOutText = intent != null ? intent.getStringExtra("CHECK_OUT_TEXT") : "";
        String roomTitle = intent != null ? intent.getStringExtra("ROOM_TITLE") : "Chỗ ở";
        RoomEntity room = roomId > 0 ? roomService.getRoomById(roomId) : null;
        if (room != null) {
            roomTitle = room.title;
            totalPrice = room.pricePerNight * nights;
        }

        tvRoomTitle.setText(roomTitle == null ? "Chỗ ở" : roomTitle);
        tvTripInfo.setText(checkInText + " - " + checkOutText + " · " + nights + " đêm · " + guestCount + " khách");
        tvTotal.setText("Tổng thanh toán: đ" + formatPrice((long) totalPrice));

        btnBack.setOnClickListener(v -> finish());

        final RoomEntity finalRoom = room;
        final double finalTotalPrice = totalPrice;
        final String finalRoomTitle = roomTitle;
        btnPayNow.setOnClickListener(v -> {
            if (finalRoom == null || checkInMillis <= 0 || checkOutMillis <= checkInMillis) {
                Toast.makeText(this, "Thông tin đặt phòng không hợp lệ", Toast.LENGTH_LONG).show();
                return;
            }

            UserEntity guest = userService.getCurrentUser();
            if (guest == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, SignInActivity.class));
                return;
            }

            Date checkInDate = new Date(checkInMillis);
            Date checkOutDate = new Date(checkOutMillis);
            if (!bookingService.isRoomAvailable(finalRoom.id, checkInDate, checkOutDate)) {
                Toast.makeText(this, "Phòng này đã được đặt trong khoảng ngày bạn chọn. Vui lòng chọn ngày khác.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            BookingEntity booking = new BookingEntity();
            booking.roomId = finalRoom.id;
            booking.guestId = guest.id;
            booking.guestCount = guestCount;
            booking.status = "paid";
            booking.createdAt = new Date();
            booking.updatedAt = new Date();
            booking.checkInDate = checkInDate;
            booking.checkOutDate = checkOutDate;
            booking.totalPrice = finalTotalPrice;
            long bookingId = bookingService.createBooking(booking);
            if (bookingId <= 0) {
                Toast.makeText(this, "Thanh toán thất bại. Không thể lưu đặt phòng", Toast.LENGTH_LONG).show();
                return;
            }

            NotificationEntity guestNotification = new NotificationEntity();
            guestNotification.userId = guest.id;
            guestNotification.title = "Thanh toán thành công";
            guestNotification.message = "Bạn đã thanh toán " + finalRoomTitle + " bằng " + PAYMENT_METHOD + ".";
            guestNotification.type = "booking_paid";
            guestNotification.isRead = false;
            guestNotification.createdAt = new Date();
            notificationService.createNotification(guestNotification);

            NotificationEntity hostNotification = new NotificationEntity();
            hostNotification.userId = finalRoom.hostId;
            hostNotification.title = "Có khách thanh toán đặt phòng";
            hostNotification.message = guest.fullName + " đã thanh toán đặt " + finalRoom.title + ".";
            hostNotification.type = "booking_received";
            hostNotification.isRead = false;
            hostNotification.createdAt = new Date();
            notificationService.createNotification(hostNotification);

            Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_LONG).show();
            Intent successIntent = new Intent(this, BookingSuccessActivity.class);
            successIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(successIntent);
            finish();
        });
    }

    private String formatPrice(long price) {
        return String.format("%,d", price).replace(',', '.');
    }

    private void setupExpiryFormatter(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isFormatting) {
                    return;
                }

                String digits = editable.toString().replaceAll("[^0-9]", "");
                if (digits.length() > 4) {
                    digits = digits.substring(0, 4);
                }

                String formatted = digits;
                if (digits.length() > 2) {
                    formatted = digits.substring(0, 2) + "/" + digits.substring(2);
                }

                if (!formatted.equals(editable.toString())) {
                    isFormatting = true;
                    editText.setText(formatted);
                    editText.setSelection(formatted.length());
                    isFormatting = false;
                }
            }
        });
    }
}

package com.hotelmanagement.activities.customer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.hotelmanagement.R;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.services.BookingService;
import com.hotelmanagement.services.RoomService;
import com.hotelmanagement.services.mappers.RoomMapper;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class BookingActivity extends AppCompatActivity {
    private final Calendar checkInCalendar = Calendar.getInstance();
    private final Calendar checkOutCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'thg' M, yyyy", Locale.getDefault());

    private RoomEntity roomEntity;
    private int guestCount = 1;
    private int nights = 1;
    private double pricePerNight = 0D;
    private TextView tvStayInfo;
    private TextView tvBasePrice;
    private TextView tvLineTotalPrice;
    private TextView tvTotalPrice;
    private TextView tvGuestInfo;
    private BookingService bookingService;
    private long roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        ImageView btnBack = findViewById(R.id.btnBackBooking);
        Button btnConfirm = findViewById(R.id.btnConfirmBooking);
        
        ImageView ivRoomImage = findViewById(R.id.ivBookingRoomImage);
        TextView tvRoomTitle = findViewById(R.id.tvBookingRoomTitle);
        TextView tvRoomRating = findViewById(R.id.tvBookingRoomRating);
        tvBasePrice = findViewById(R.id.tvBookingBasePrice);
        tvLineTotalPrice = findViewById(R.id.tvBookingLineTotalPrice);
        tvTotalPrice = findViewById(R.id.tvBookingTotalPrice);
        tvStayInfo = findViewById(R.id.tvBookingStayInfo);
        tvGuestInfo = findViewById(R.id.tvBookingGuestInfo);
        TextView btnChangeDates = findViewById(R.id.btnChangeBookingDates);
        TextView btnChangeGuests = findViewById(R.id.btnChangeBookingGuests);

        RoomService roomService = new RoomService(this);
        bookingService = new BookingService(this);
        RoomMapper roomMapper = new RoomMapper();

        // NHẬN DỮ LIỆU TỪ INTENT (Truyền từ Detail sang)
        Intent intent = getIntent();
        roomId = intent != null ? intent.getLongExtra("ROOM_ID", 0L) : 0L;
        guestCount = intent != null ? intent.getIntExtra("GUEST_COUNT", 1) : 1;
        roomEntity = roomId > 0 ? roomService.getRoomById(roomId) : null;
        Calendar calendar = Calendar.getInstance();
        long defaultCheckInMillis = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long defaultCheckOutMillis = calendar.getTimeInMillis();
        long checkInMillis = intent != null ? intent.getLongExtra("CHECK_IN_MILLIS", defaultCheckInMillis) : defaultCheckInMillis;
        long checkOutMillis = intent != null ? intent.getLongExtra("CHECK_OUT_MILLIS", defaultCheckOutMillis) : defaultCheckOutMillis;
        if (checkInMillis <= 0) {
            checkInMillis = defaultCheckInMillis;
        }
        if (checkOutMillis <= 0) {
            checkOutMillis = defaultCheckOutMillis;
        }
        if (checkOutMillis <= checkInMillis) {
            checkOutMillis = checkInMillis + 24L * 60L * 60L * 1000L;
        }
        checkInCalendar.setTimeInMillis(checkInMillis);
        checkOutCalendar.setTimeInMillis(checkOutMillis);
        normalizeDateCalendars();
        updateTripAndPrice();
        tvGuestInfo.setText(guestCount + " khách");

        if (roomEntity != null) {
            pricePerNight = roomEntity.pricePerNight;
            com.hotelmanagement.models.Room roomModel = roomMapper.fromEntity(roomEntity);
            tvRoomTitle.setText(roomModel.getTitle());
            tvRoomRating.setText(roomModel.getMeta());
            if (roomModel.getImageUri() != null && !roomModel.getImageUri().trim().isEmpty()) {
                ivRoomImage.setImageURI(android.net.Uri.parse(roomModel.getImageUri()));
            } else {
                ivRoomImage.setImageResource(roomModel.getImageResId());
            }
            updateTripAndPrice();
        } else if (intent != null) {
            String title = intent.getStringExtra("ROOM_TITLE");
            String price = intent.getStringExtra("ROOM_PRICE");
            String meta = intent.getStringExtra("ROOM_META");
            String imageUri = intent.getStringExtra("ROOM_IMAGE_URI");
            int imageRes = intent.getIntExtra("ROOM_IMAGE", R.drawable.vungtau_1);

            if (title != null) tvRoomTitle.setText(title);
            if (meta != null) tvRoomRating.setText(meta);
            if (imageUri != null && !imageUri.trim().isEmpty()) {
                ivRoomImage.setImageURI(android.net.Uri.parse(imageUri));
            } else {
                ivRoomImage.setImageResource(imageRes);
            }

            if (price != null) {
                pricePerNight = parsePrice(price);
                updateTripAndPrice();
            }
        }

        btnBack.setOnClickListener(v -> finish());
        btnChangeDates.setOnClickListener(v -> showBookedDatesAndDatePicker());
        btnChangeGuests.setOnClickListener(v -> showGuestPicker());

        btnConfirm.setOnClickListener(v -> {
            if (!bookingService.isRoomAvailable(roomId, checkInCalendar.getTime(), checkOutCalendar.getTime())) {
                Toast.makeText(this, "Phòng này đã được đặt trong khoảng ngày bạn chọn. Vui lòng chọn ngày khác.", Toast.LENGTH_LONG).show();
                return;
            }

            Intent paymentIntent = new Intent(this, PaymentActivity.class);
            paymentIntent.putExtra("ROOM_ID", roomId);
            paymentIntent.putExtra("ROOM_TITLE", tvRoomTitle.getText().toString());
            paymentIntent.putExtra("GUEST_COUNT", guestCount);
            paymentIntent.putExtra("CHECK_IN_MILLIS", checkInCalendar.getTimeInMillis());
            paymentIntent.putExtra("CHECK_OUT_MILLIS", checkOutCalendar.getTimeInMillis());
            paymentIntent.putExtra("CHECK_IN_TEXT", dateFormat.format(checkInCalendar.getTime()));
            paymentIntent.putExtra("CHECK_OUT_TEXT", dateFormat.format(checkOutCalendar.getTime()));
            paymentIntent.putExtra("NIGHTS", nights);
            paymentIntent.putExtra("TOTAL_PRICE", pricePerNight * nights);
            startActivity(paymentIntent);
        });
    }

    private void showBookedDatesAndDatePicker() {
        List<BookingEntity> activeBookings = bookingService.getActiveBookingsByRoom(roomId);
        if (activeBookings.isEmpty()) {
            showDateRangePicker(activeBookings);
            return;
        }

        String[] bookedRanges = new String[activeBookings.size()];
        for (int i = 0; i < activeBookings.size(); i++) {
            BookingEntity booking = activeBookings.get(i);
            bookedRanges[i] = dateFormat.format(booking.checkInDate)
                    + " - "
                    + dateFormat.format(booking.checkOutDate);
        }

        new AlertDialog.Builder(this)
                .setTitle("Ngày đã được đặt")
                .setItems(bookedRanges, null)
                .setPositiveButton("Chọn ngày khác", (dialog, which) -> showDateRangePicker(activeBookings))
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void showDateRangePicker(List<BookingEntity> activeBookings) {
        Set<Long> unavailableUtcDays = buildUnavailableUtcDays(activeBookings);
        Pair<Long, Long> currentSelection = new Pair<>(
                toUtcDayMillis(checkInCalendar.getTimeInMillis()),
                toUtcDayMillis(checkOutCalendar.getTimeInMillis())
        );
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(new UnavailableDatesValidator(unavailableUtcDays))
                .build();

        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Chọn ngày nhận và trả phòng")
                .setSelection(currentSelection)
                .setCalendarConstraints(constraints)
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection == null || selection.first == null || selection.second == null) {
                Toast.makeText(this, "Vui lòng chọn ngày nhận và trả phòng.", Toast.LENGTH_LONG).show();
                return;
            }

            Date selectedCheckIn = new Date(selection.first);
            Date selectedCheckOut = new Date(selection.second);
            if (!selectedCheckOut.after(selectedCheckIn)) {
                Toast.makeText(this, "Ngày trả phòng phải sau ngày nhận phòng.", Toast.LENGTH_LONG).show();
                return;
            }

            if (!bookingService.isRoomAvailable(roomId, selectedCheckIn, selectedCheckOut)) {
                Toast.makeText(this, "Khoảng ngày này có ngày đã được đặt. Vui lòng chọn khoảng khác.", Toast.LENGTH_LONG).show();
                return;
            }

            checkInCalendar.setTime(selectedCheckIn);
            checkOutCalendar.setTime(selectedCheckOut);
            normalizeDateCalendars();
            updateTripAndPrice();
        });
        picker.show(getSupportFragmentManager(), "booking_date_range_picker");
    }

    private void showGuestPicker() {
        final int maxGuests = roomEntity != null && roomEntity.maxGuests > 0 ? roomEntity.maxGuests : 20;
        final String[] guestOptions = new String[maxGuests];
        for (int i = 0; i < maxGuests; i++) {
            int guests = i + 1;
            guestOptions[i] = guests + " khách";
        }

        int selectedIndex = Math.max(0, Math.min(guestCount - 1, maxGuests - 1));
        new AlertDialog.Builder(this)
                .setTitle("Chọn số khách")
                .setSingleChoiceItems(guestOptions, selectedIndex, (dialog, which) -> {
                    guestCount = which + 1;
                    updateGuestInfo();
                    dialog.dismiss();
                })
                .show();
    }

    private void normalizeDateCalendars() {
        checkInCalendar.set(Calendar.HOUR_OF_DAY, 0);
        checkInCalendar.set(Calendar.MINUTE, 0);
        checkInCalendar.set(Calendar.SECOND, 0);
        checkInCalendar.set(Calendar.MILLISECOND, 0);
        checkOutCalendar.set(Calendar.HOUR_OF_DAY, 0);
        checkOutCalendar.set(Calendar.MINUTE, 0);
        checkOutCalendar.set(Calendar.SECOND, 0);
        checkOutCalendar.set(Calendar.MILLISECOND, 0);
        if (!checkOutCalendar.after(checkInCalendar)) {
            checkOutCalendar.setTimeInMillis(checkInCalendar.getTimeInMillis());
            checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void updateTripAndPrice() {
        Date checkIn = checkInCalendar.getTime();
        Date checkOut = checkOutCalendar.getTime();
        nights = Math.max(1, (int) ((checkOut.getTime() - checkIn.getTime()) / (24L * 60L * 60L * 1000L)));
        if (tvStayInfo != null) {
            tvStayInfo.setText(dateFormat.format(checkIn) + " - " + dateFormat.format(checkOut));
        }
        if (pricePerNight > 0 && tvBasePrice != null && tvTotalPrice != null) {
            long total = (long) (pricePerNight * nights);
            tvBasePrice.setText(nights + " đêm x đ" + formatPrice((long) pricePerNight));
            if (tvLineTotalPrice != null) {
                tvLineTotalPrice.setText("đ" + formatPrice(total));
            }
            tvTotalPrice.setText("đ" + formatPrice(total));
        }
    }

    private void updateGuestInfo() {
        if (tvGuestInfo != null) {
            tvGuestInfo.setText(guestCount + " khách");
        }
    }

    private double parsePrice(String priceText) {
        if (priceText == null) {
            return 0D;
        }
        String digits = priceText.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0D;
        }
        try {
            return Double.parseDouble(digits);
        } catch (NumberFormatException ex) {
            return 0D;
        }
    }

    private String formatPrice(long price) {
        return String.format("%,d", price).replace(',', '.');
    }

    private Set<Long> buildUnavailableUtcDays(List<BookingEntity> bookings) {
        Set<Long> unavailableDays = new HashSet<>();
        for (BookingEntity booking : bookings) {
            Calendar day = Calendar.getInstance();
            day.setTime(booking.checkInDate);
            normalizeCalendarDay(day);

            Calendar checkout = Calendar.getInstance();
            checkout.setTime(booking.checkOutDate);
            normalizeCalendarDay(checkout);

            while (day.before(checkout)) {
                unavailableDays.add(toUtcDayMillis(day.getTimeInMillis()));
                day.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return unavailableDays;
    }

    private long toUtcDayMillis(long millis) {
        Calendar localDay = Calendar.getInstance();
        localDay.setTimeInMillis(millis);
        normalizeCalendarDay(localDay);

        Calendar utcDay = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcDay.clear();
        utcDay.set(
                localDay.get(Calendar.YEAR),
                localDay.get(Calendar.MONTH),
                localDay.get(Calendar.DAY_OF_MONTH)
        );
        return utcDay.getTimeInMillis();
    }

    private void normalizeCalendarDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static class UnavailableDatesValidator implements CalendarConstraints.DateValidator {
        private final Set<Long> unavailableUtcDays;

        UnavailableDatesValidator(Set<Long> unavailableUtcDays) {
            this.unavailableUtcDays = unavailableUtcDays == null
                    ? new HashSet<>()
                    : new HashSet<>(unavailableUtcDays);
        }

        protected UnavailableDatesValidator(Parcel in) {
            List<Long> days = new ArrayList<>();
            in.readList(days, Long.class.getClassLoader());
            this.unavailableUtcDays = new HashSet<>(days);
        }

        @Override
        public boolean isValid(long date) {
            return !unavailableUtcDays.contains(date);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(new ArrayList<>(unavailableUtcDays));
        }

        public static final Parcelable.Creator<UnavailableDatesValidator> CREATOR =
                new Parcelable.Creator<UnavailableDatesValidator>() {
                    @Override
                    public UnavailableDatesValidator createFromParcel(Parcel in) {
                        return new UnavailableDatesValidator(in);
                    }

                    @Override
                    public UnavailableDatesValidator[] newArray(int size) {
                        return new UnavailableDatesValidator[size];
                    }
                };
    }
}

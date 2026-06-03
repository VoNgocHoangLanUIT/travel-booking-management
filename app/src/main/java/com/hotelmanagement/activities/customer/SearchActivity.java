package com.hotelmanagement.activities.customer;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;
import com.hotelmanagement.services.RoomService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private int guestCount = 1;
    private String selectedCity = "Đà Lạt";
    private final Calendar checkInCalendar = Calendar.getInstance();
    private final Calendar checkOutCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'thg' M", Locale.getDefault());
    private TextView tvGuestCount;
    private TextView tvDestinationCity;
    private TextView tvCheckInDate;
    private TextView tvCheckOutDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageView btnBack = findViewById(R.id.btnBackSearch);
        TextView btnMinus = findViewById(R.id.btnMinusGuest);
        TextView btnPlus = findViewById(R.id.btnPlusGuest);
        tvGuestCount = findViewById(R.id.tvGuestCount);
        tvDestinationCity = findViewById(R.id.tvDestinationCity);
        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        Button btnSearch = findViewById(R.id.btnSearchRoom);
        RoomService roomService = new RoomService(this);

        checkInCalendar.add(Calendar.DAY_OF_MONTH, 1);
        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 2);
        updateSelectedCityText();
        updateDateTexts();

        btnBack.setOnClickListener(v -> finish());

        tvDestinationCity.setOnClickListener(v -> showCityPicker(roomService.getPublishedCities()));
        tvCheckInDate.setOnClickListener(v -> showDatePicker(true));
        tvCheckOutDate.setOnClickListener(v -> showDatePicker(false));

        btnMinus.setOnClickListener(v -> {
            if (guestCount > 1) {
                guestCount--;
                tvGuestCount.setText(String.valueOf(guestCount));
            }
        });

        btnPlus.setOnClickListener(v -> {
            guestCount++;
            tvGuestCount.setText(String.valueOf(guestCount));
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra("GUEST_COUNT", guestCount);
            intent.putExtra("CITY", selectedCity);
            intent.putExtra("CHECK_IN_TEXT", dateFormat.format(checkInCalendar.getTime()));
            intent.putExtra("CHECK_OUT_TEXT", dateFormat.format(checkOutCalendar.getTime()));
            intent.putExtra("CHECK_IN_MILLIS", checkInCalendar.getTimeInMillis());
            intent.putExtra("CHECK_OUT_MILLIS", checkOutCalendar.getTimeInMillis());
            startActivity(intent);
        });
    }

    private void showCityPicker(List<String> cities) {
        if (cities == null || cities.isEmpty()) {
            cities = java.util.Arrays.asList("Đà Lạt", "Vũng Tàu", "TP. Hồ Chí Minh", "Hà Nội");
        }
        final String[] cityArray = cities.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("Chọn thành phố")
                .setItems(cityArray, (dialog, which) -> {
                    selectedCity = cityArray[which];
                    updateSelectedCityText();
                })
                .show();
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar source = isCheckIn ? checkInCalendar : checkOutCalendar;
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 0, 0, 0);
                    selected.set(Calendar.MILLISECOND, 0);
                    if (isCheckIn) {
                        checkInCalendar.setTimeInMillis(selected.getTimeInMillis());
                        if (!checkOutCalendar.after(checkInCalendar)) {
                            checkOutCalendar.setTimeInMillis(checkInCalendar.getTimeInMillis());
                            checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        }
                    } else {
                        if (!selected.after(checkInCalendar)) {
                            selected.setTimeInMillis(checkInCalendar.getTimeInMillis());
                            selected.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        checkOutCalendar.setTimeInMillis(selected.getTimeInMillis());
                    }
                    updateDateTexts();
                },
                source.get(Calendar.YEAR),
                source.get(Calendar.MONTH),
                source.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void updateSelectedCityText() {
        tvDestinationCity.setText(selectedCity + ", Việt Nam");
    }

    private void updateDateTexts() {
        tvCheckInDate.setText(dateFormat.format(checkInCalendar.getTime()));
        tvCheckOutDate.setText(dateFormat.format(checkOutCalendar.getTime()));
    }
}

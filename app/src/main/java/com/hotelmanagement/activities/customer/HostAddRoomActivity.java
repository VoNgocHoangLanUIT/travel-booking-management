package com.hotelmanagement.activities.customer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import com.hotelmanagement.R;
import com.hotelmanagement.database.AppDatabase;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.database.entities.ServiceEntity;
import com.hotelmanagement.database.entities.UserEntity;
import com.hotelmanagement.services.RoomService;
import com.hotelmanagement.services.ServiceCatalogService;
import com.hotelmanagement.services.UserService;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HostAddRoomActivity extends AppCompatActivity {

    private AppDatabase database;
    private static final String[] CITY_OPTIONS = {
            "Hà Nội",
            "TP. Hồ Chí Minh",
            "Đà Nẵng",
            "Hải Phòng",
            "Cần Thơ",
            "Huế",
            "Nha Trang",
            "Đà Lạt",
            "Vũng Tàu",
            "Phú Quốc",
            "Hội An",
            "Quy Nhơn",
            "Hạ Long",
            "Sa Pa",
            "Mũi Né",
            "Phan Thiết",
            "Ninh Bình",
            "Tam Đảo",
            "Mộc Châu",
            "Mai Châu",
            "Đồng Hới",
            "Tuy Hòa",
            "Buôn Ma Thuột",
            "Pleiku",
            "Kon Tum",
            "Rạch Giá",
            "Cà Mau",
            "Long Xuyên",
            "Mỹ Tho",
            "Bến Tre",
            "Vĩnh Long",
            "Sóc Trăng",
            "Tây Ninh",
            "Biên Hòa",
            "Thủ Dầu Một",
            "Đồng Xoài",
            "Thanh Hóa",
            "Vinh",
            "Hà Tĩnh",
            "Lạng Sơn",
            "Cao Bằng",
            "Hà Giang",
            "Điện Biên Phủ",
            "Lào Cai"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_add_room);

        database = AppDatabase.getInstance(this);

        ImageView btnBack = findViewById(R.id.btnBackHost);
        TextView tvFormTitle = findViewById(R.id.tvHostFormTitle);
        Button btnSubmit = findViewById(R.id.btnSubmitHost);
        EditText edtTitle = findViewById(R.id.edtHostTitle);
        EditText edtPrice = findViewById(R.id.edtHostPrice);
        EditText edtLocation = findViewById(R.id.edtHostLocation);
        TextView tvCity = findViewById(R.id.tvHostCity);
        EditText edtGuests = findViewById(R.id.edtHostGuests);
        EditText edtDescription = findViewById(R.id.edtHostDescription);
        TextView tvServices = findViewById(R.id.tvHostServices);
        ImageView ivHostRoomImage = findViewById(R.id.ivHostRoomImage);

        UserService userService = new UserService(this);
        RoomService roomService = new RoomService(this);
        ServiceCatalogService serviceCatalogService = new ServiceCatalogService(this);
        List<ServiceEntity> allServices = serviceCatalogService.getAllServices();

        UserEntity currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đăng chỗ ở", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        long editRoomId = getIntent() != null ? getIntent().getLongExtra("ROOM_ID", 0L) : 0L;
        RoomEntity editingRoom = editRoomId > 0 ? roomService.getRoomById(editRoomId) : null;
        if (editRoomId > 0 && (editingRoom == null || editingRoom.hostId != currentUser.id)) {
            Toast.makeText(this, "Không tìm thấy phòng cần sửa", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final String[] selectedImageUri = new String[] { null };
        final String[] selectedCity = new String[] { "" };
        final List<Long> selectedServiceIds = new ArrayList<>();

        if (editingRoom != null) {
            tvFormTitle.setText("Sửa chỗ ở");
            btnSubmit.setText("Lưu thay đổi");
            edtTitle.setText(editingRoom.title);
            edtPrice.setText(formatPrice((long) editingRoom.pricePerNight));
            edtLocation.setText(editingRoom.address);
            selectedCity[0] = editingRoom.city == null ? "" : editingRoom.city;
            tvCity.setText(TextUtils.isEmpty(selectedCity[0]) ? "Chọn thành phố" : selectedCity[0]);
            edtGuests.setText(String.valueOf(Math.max(1, editingRoom.maxGuests)));
            edtDescription.setText(editingRoom.description);
            selectedServiceIds.addAll(serviceCatalogService.getSelectedServiceIds(editingRoom.id));
            updateSelectedServicesText(tvServices, allServices, selectedServiceIds);
            selectedImageUri[0] = editingRoom.imageUri;
            if (editingRoom.imageUri != null && !editingRoom.imageUri.trim().isEmpty()) {
                showImagePreview(ivHostRoomImage);
                ivHostRoomImage.setImageURI(android.net.Uri.parse(editingRoom.imageUri));
            } else if (editingRoom.imageResId != 0) {
                showImagePreview(ivHostRoomImage);
                ivHostRoomImage.setImageResource(editingRoom.imageResId);
            }
        }

        ActivityResultLauncher<String[]> imagePicker = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri[0] = uri.toString();

                        try {
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (SecurityException ignored) {
                            // Nếu thiết bị không cho giữ quyền lâu dài thì app vẫn hiển thị được trong phiên hiện tại.
                        }

                        showImagePreview(ivHostRoomImage);
                        ivHostRoomImage.setImageURI(uri);
                    }
                }
        );

        btnBack.setOnClickListener(v -> finish());

        ivHostRoomImage.setOnClickListener(v -> imagePicker.launch(new String[] {"image/*"}));

        tvCity.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Chọn thành phố")
                .setItems(CITY_OPTIONS, (dialog, which) -> {
                    selectedCity[0] = CITY_OPTIONS[which];
                    tvCity.setText(selectedCity[0]);
                })
                .show());

        tvServices.setOnClickListener(v -> showServicePicker(
                tvServices,
                allServices,
                selectedServiceIds
        ));

        btnSubmit.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String priceText = edtPrice.getText().toString().trim();
            String location = edtLocation.getText().toString().trim();
            String guestsText = edtGuests.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Vui lòng nhập tên chỗ ở.", Toast.LENGTH_LONG).show();
                return;
            }

            double price = parseNumber(priceText);
            if (price <= 0) {
                Toast.makeText(this, "Vui lòng nhập giá phòng hợp lệ.", Toast.LENGTH_LONG).show();
                return;
            }

            String city = selectedCity[0];
            if (TextUtils.isEmpty(city)) {
                Toast.makeText(this, "Vui lòng chọn thành phố.", Toast.LENGTH_LONG).show();
                return;
            }

            int maxGuests = (int) parseNumber(guestsText);
            if (maxGuests <= 0) {
                maxGuests = 1;
            }

            // Một user có thể vừa là khách vừa là host.
            // Khi user đăng phòng lần đầu, chỉ cập nhật cờ isHost = true, không tạo tài khoản host riêng.
            if (!currentUser.isHost) {
                currentUser.isHost = true;
                currentUser.updatedAt = new Date();
                userService.updateUser(currentUser);
            }

            RoomEntity room = editingRoom == null ? new RoomEntity() : editingRoom;
            room.hostId = currentUser.id;
            room.title = title;
            room.description = description;
            room.address = location;
            room.city = city;
            room.pricePerNight = price;
            room.maxGuests = maxGuests;
            room.status = "published";
            room.rating = 0;

            if (selectedImageUri[0] != null && !selectedImageUri[0].trim().isEmpty()) {
                room.imageUri = selectedImageUri[0];
                room.imageResId = 0;
            } else if (editingRoom != null && editingRoom.imageResId != 0) {
                room.imageUri = null;
                room.imageResId = editingRoom.imageResId;
            } else {
                room.imageUri = null;
                room.imageResId = getDefaultImageByCity(city, database.roomDao().countRooms());
            }

            room.badge = "";
            if (room.createdAt == null) {
                room.createdAt = new Date();
            }
            room.updatedAt = new Date();

            boolean saved;
            long savedRoomId = room.id;
            if (editingRoom == null) {
                savedRoomId = database.roomDao().insert(room);
                saved = savedRoomId > 0;
            } else {
                saved = roomService.updateRoom(room) > 0;
            }

            if (saved) {
                roomService.replaceRoomServices(savedRoomId, selectedServiceIds);
                Toast.makeText(this, editingRoom == null ? "Chúc mừng! Chỗ ở của bạn đã được xuất bản." : "Đã cập nhật phòng.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, editingRoom == null ? "Không thể đăng chỗ ở. Vui lòng thử lại." : "Không thể cập nhật phòng. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private double parseNumber(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }

        try {
            String cleaned = value.replaceAll("[^0-9]", "");
            if (TextUtils.isEmpty(cleaned)) {
                return 0;
            }
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void showServicePicker(
            TextView tvServices,
            List<ServiceEntity> allServices,
            List<Long> selectedServiceIds
    ) {
        if (allServices == null || allServices.isEmpty()) {
            Toast.makeText(this, "Chưa có danh mục dịch vụ", Toast.LENGTH_LONG).show();
            return;
        }

        String[] serviceNames = new String[allServices.size()];
        boolean[] checkedServices = new boolean[allServices.size()];
        for (int i = 0; i < allServices.size(); i++) {
            ServiceEntity service = allServices.get(i);
            serviceNames[i] = service.name;
            checkedServices[i] = selectedServiceIds.contains(service.id);
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn dịch vụ / tiện nghi")
                .setMultiChoiceItems(serviceNames, checkedServices, (dialog, which, isChecked) -> {
                    long serviceId = allServices.get(which).id;
                    if (isChecked) {
                        if (!selectedServiceIds.contains(serviceId)) {
                            selectedServiceIds.add(serviceId);
                        }
                    } else {
                        selectedServiceIds.remove(serviceId);
                    }
                })
                .setPositiveButton("Xong", (dialog, which) ->
                        updateSelectedServicesText(tvServices, allServices, selectedServiceIds))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateSelectedServicesText(
            TextView tvServices,
            List<ServiceEntity> allServices,
            List<Long> selectedServiceIds
    ) {
        if (selectedServiceIds == null || selectedServiceIds.isEmpty()) {
            tvServices.setText("Chọn dịch vụ / tiện nghi");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (ServiceEntity service : allServices) {
            if (!selectedServiceIds.contains(service.id)) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(service.name);
        }

        tvServices.setText(builder.length() == 0 ? "Chọn dịch vụ / tiện nghi" : builder.toString());
    }

    private void showImagePreview(ImageView imageView) {
        imageView.setPadding(0, 0, 0, 0);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(false);
        imageView.setBackgroundColor(0xFFE0E0E0);
        ImageViewCompat.setImageTintList(imageView, null);
    }

    private String extractCity(String location) {
        if (TextUtils.isEmpty(location)) {
            return "";
        }

        String[] parts = location.split(",");
        return parts.length > 0 ? parts[parts.length - 1].trim() : location.trim();
    }

    private String normalizeCity(String city) {
        if (TextUtils.isEmpty(city)) {
            return "";
        }

        String key = normalizeKey(city);

        if (key.contains("vungtau")) {
            return "Vũng Tàu";
        }

        if (key.contains("dalat")) {
            return "Đà Lạt";
        }

        if (key.contains("quynhon")) {
            return "Quy Nhơn";
        }

        return city.trim();
    }

    private String formatPrice(long price) {
        return String.format("%,d", price).replace(',', '.');
    }

    private int getDefaultImageByCity(String city, int seed) {
        String key = normalizeKey(city);

        if (key.contains("dalat")) {
            return pickImage(seed, new int[] {
                    R.drawable.dalat_1,
                    R.drawable.dalat_2
            });
        }

        if (key.contains("quynhon")) {
            return pickImage(seed, new int[] {
                    R.drawable.quynhon_1,
                    R.drawable.quynhon_2
            });
        }

        return pickImage(seed, new int[] {
                R.drawable.vungtau_1,
                R.drawable.vungtau_2,
                R.drawable.vungtau_3
        });
    }

    private int pickImage(int seed, int[] imageResIds) {
        if (imageResIds == null || imageResIds.length == 0) {
            return R.drawable.vungtau_1;
        }
        return imageResIds[Math.abs(seed) % imageResIds.length];
    }

    private String normalizeKey(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        normalized = normalized.replace("Đ", "D").replace("đ", "d");

        return normalized.toLowerCase(new Locale("vi", "VN")).replaceAll("[^a-z]", "");
    }
}

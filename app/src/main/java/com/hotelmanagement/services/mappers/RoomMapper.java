package com.hotelmanagement.services.mappers;

import com.hotelmanagement.R;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.models.Room;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

public class RoomMapper {
    public RoomEntity toEntity(Room room, long hostId, String city) {
        RoomEntity entity = new RoomEntity();
        entity.hostId = hostId;
        entity.title = room.getTitle();
        entity.description = room.getMeta();
        entity.address = "";
        entity.city = city;
        entity.pricePerNight = parsePrice(room.getPrice());
        entity.maxGuests = 0;
        entity.status = "published";
        entity.rating = 0;
        entity.imageResId = room.getImageResId();
        entity.imageUri = room.getImageUri();
        entity.badge = room.getBadge();
        entity.createdAt = new Date();
        entity.updatedAt = new Date();
        return entity;
    }

    public Room fromEntity(RoomEntity entity) {
        int imageResId = entity.imageResId != 0 ? entity.imageResId : resolveImageResId(entity);
        String badge = (entity.badge != null && !entity.badge.trim().isEmpty())
                ? entity.badge
                : resolveBadge(entity.rating);
        return new Room(
                entity.id,
                entity.imageUri,
                imageResId,
                entity.title,
                buildMeta(entity),
                formatPrice(entity.pricePerNight),
                badge,
                false
        );
    }

    private String buildMeta(RoomEntity entity) {
        if (entity.maxGuests > 0 || entity.rating > 0) {
            StringBuilder meta = new StringBuilder();
            if (entity.maxGuests > 0) {
                meta.append(entity.maxGuests).append(" Người");
            }
            if (entity.rating > 0) {
                if (meta.length() > 0) {
                    meta.append(" • ");
                }
                meta.append("★ ").append(String.format(Locale.getDefault(), "%.2f", entity.rating));
            }
            return meta.toString();
        }
        return entity.description == null ? "" : entity.description;
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        format.setMaximumFractionDigits(0);
        return format.format(price) + "đ";
    }

    private double parsePrice(String priceText) {
        if (priceText == null) {
            return 0;
        }
        String digits = priceText.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(digits);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String resolveBadge(double rating) {
        return rating >= 4.95 ? "Được khách yêu thích" : "";
    }

    private int resolveImageResId(RoomEntity entity) {
        if (entity.title == null) {
            return R.drawable.ic_launcher_foreground;
        }
        if (entity.title.contains("Vũng Tàu")) {
            if (entity.title.contains("Luxury Suite")) {
                return R.drawable.vungtau_1;
            }
            if (entity.title.contains("Ocean")) {
                return R.drawable.vungtau_2;
            }
            return R.drawable.vungtau_3;
        }
        if (entity.title.contains("Đà Lạt")) {
            if (entity.title.contains("Skyline")) {
                return R.drawable.dalat_1;
            }
            return R.drawable.dalat_2;
        }
        if (entity.title.contains("Quy Nhơn")) {
            if (entity.title.contains("Beach")) {
                return R.drawable.quynhon_1;
            }
            return R.drawable.quynhon_2;
        }
        return R.drawable.ic_launcher_foreground;
    }
}

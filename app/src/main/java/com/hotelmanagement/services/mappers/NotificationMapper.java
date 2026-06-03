package com.hotelmanagement.services.mappers;

import android.text.format.DateUtils;

import com.hotelmanagement.database.entities.NotificationEntity;
import com.hotelmanagement.models.Notification;

public class NotificationMapper {
    public Notification fromEntity(NotificationEntity entity) {
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                entity.createdAt.getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
        );
        return new Notification(
                entity.title,
                relativeTime.toString(),
                resolveEmoji(entity.type),
                entity.isRead
        );
    }

    private String resolveEmoji(String type) {
        if (type == null) {
            return "🔔";
        }
        switch (type) {
            case "booking":
                return "🏨";
            case "payment":
                return "💳";
            case "review":
                return "⭐";
            case "system":
                return "📣";
            default:
                return "🔔";
        }
    }
}


package com.hotelmanagement.services;

import android.content.Context;

import com.hotelmanagement.database.AppDatabase;
import com.hotelmanagement.database.dao.NotificationDao;
import com.hotelmanagement.database.entities.NotificationEntity;
import com.hotelmanagement.models.Notification;
import com.hotelmanagement.services.mappers.NotificationMapper;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final NotificationDao notificationDao;
    private final NotificationMapper notificationMapper;

    public NotificationService(Context context) {
        this.notificationDao = AppDatabase.getInstance(context).notificationDao();
        this.notificationMapper = new NotificationMapper();
    }

    public long createNotification(NotificationEntity notification) {
        return notificationDao.insert(notification);
    }

    public int updateNotification(NotificationEntity notification) {
        return notificationDao.update(notification);
    }

    public NotificationEntity getNotificationById(long id) {
        return notificationDao.getById(id);
    }

    public List<NotificationEntity> getNotificationsByUser(long userId) {
        return notificationDao.getByUserId(userId);
    }

    public List<Notification> getNotificationModelsByUser(long userId) {
        List<NotificationEntity> entities = notificationDao.getByUserId(userId);
        List<Notification> notifications = new ArrayList<>();
        for (NotificationEntity entity : entities) {
            notifications.add(notificationMapper.fromEntity(entity));
        }
        return notifications;
    }
}

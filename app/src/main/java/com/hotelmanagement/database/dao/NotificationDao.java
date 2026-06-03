package com.hotelmanagement.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hotelmanagement.database.entities.NotificationEntity;

import java.util.List;

@Dao
public interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(NotificationEntity notification);

    @Update
    int update(NotificationEntity notification);

    @Delete
    int delete(NotificationEntity notification);

    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC")
    List<NotificationEntity> getByUserId(long userId);

    @Query("SELECT * FROM notifications WHERE id = :id LIMIT 1")
    NotificationEntity getById(long id);
}


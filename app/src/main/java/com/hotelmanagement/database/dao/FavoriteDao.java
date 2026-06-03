package com.hotelmanagement.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hotelmanagement.database.entities.FavoriteEntity;
import com.hotelmanagement.database.entities.RoomEntity;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(FavoriteEntity favorite);

    @Delete
    int delete(FavoriteEntity favorite);

    @Query("SELECT * FROM favorites WHERE user_id = :userId ORDER BY created_at DESC")
    List<FavoriteEntity> getByUserId(long userId);

    @Query("SELECT * FROM favorites WHERE user_id = :userId AND room_id = :roomId LIMIT 1")
    FavoriteEntity getByUserAndRoom(long userId, long roomId);

    @Query("SELECT rooms.* FROM rooms INNER JOIN favorites ON rooms.id = favorites.room_id WHERE favorites.user_id = :userId ORDER BY favorites.created_at DESC")
    List<RoomEntity> getFavoriteRooms(long userId);
}

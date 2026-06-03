package com.hotelmanagement.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hotelmanagement.database.entities.RoomEntity;

import java.util.List;

@Dao
public interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(RoomEntity room);

    @Update
    int update(RoomEntity room);

    @Delete
    int delete(RoomEntity room);

    @Query("SELECT * FROM rooms WHERE id = :id LIMIT 1")
    RoomEntity getById(long id);

    @Query("SELECT * FROM rooms WHERE host_id = :hostId ORDER BY created_at DESC")
    List<RoomEntity> getByHostId(long hostId);

    @Query("SELECT * FROM rooms WHERE status = :status ORDER BY created_at DESC")
    List<RoomEntity> getByStatus(String status);

    @Query("SELECT * FROM rooms WHERE status = 'published' ORDER BY created_at DESC")
    List<RoomEntity> getAllPublished();

    @Query("SELECT * FROM rooms WHERE status = 'published' AND max_guests >= :guestCount ORDER BY created_at DESC")
    List<RoomEntity> searchPublished(int guestCount);

    @Query("SELECT * FROM rooms WHERE status = 'published' AND max_guests >= :guestCount AND (:city IS NULL OR :city = '' OR city = :city COLLATE NOCASE) ORDER BY created_at DESC")
    List<RoomEntity> searchPublishedByCity(String city, int guestCount);

    @Query("SELECT * FROM rooms ORDER BY created_at DESC")
    List<RoomEntity> getAll();

    @Query("SELECT * FROM rooms WHERE city = :city ORDER BY created_at DESC")
    List<RoomEntity> getByCity(String city);

    @Query("SELECT * FROM rooms WHERE status = 'published' AND city = :city COLLATE NOCASE ORDER BY created_at DESC")
    List<RoomEntity> getPublishedByCity(String city);

    @Query("SELECT DISTINCT city FROM rooms WHERE status = 'published' AND city IS NOT NULL AND city != '' ORDER BY city ASC")
    List<String> getPublishedCities();

    @Query("SELECT COUNT(*) FROM rooms")
    int countRooms();
}

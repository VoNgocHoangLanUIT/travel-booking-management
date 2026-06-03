package com.hotelmanagement.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hotelmanagement.database.entities.RoomServiceEntity;

import java.util.List;

@Dao
public interface RoomServiceDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(RoomServiceEntity roomService);

    @Delete
    int delete(RoomServiceEntity roomService);

    @Query("SELECT * FROM room_services WHERE room_id = :roomId")
    List<RoomServiceEntity> getByRoomId(long roomId);

    @Query("DELETE FROM room_services WHERE room_id = :roomId")
    int deleteByRoomId(long roomId);
}


package com.hotelmanagement.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hotelmanagement.database.entities.ServiceEntity;

import java.util.List;

@Dao
public interface ServiceDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(ServiceEntity service);

    @Update
    int update(ServiceEntity service);

    @Delete
    int delete(ServiceEntity service);

    @Query("SELECT * FROM services ORDER BY name ASC")
    List<ServiceEntity> getAll();

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    ServiceEntity getById(long id);

    @Query("SELECT * FROM services WHERE name = :name LIMIT 1")
    ServiceEntity getByName(String name);

    @Query("SELECT services.* FROM services " +
            "INNER JOIN room_services ON room_services.service_id = services.id " +
            "WHERE room_services.room_id = :roomId " +
            "ORDER BY services.name ASC")
    List<ServiceEntity> getByRoomId(long roomId);
}


package com.hotelmanagement.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "room_services",
        primaryKeys = {"room_id", "service_id"},
        foreignKeys = {
                @ForeignKey(
                        entity = RoomEntity.class,
                        parentColumns = "id",
                        childColumns = "room_id",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = ServiceEntity.class,
                        parentColumns = "id",
                        childColumns = "service_id",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = "room_id"),
                @Index(value = "service_id")
        }
)
public class RoomServiceEntity {
    @ColumnInfo(name = "room_id")
    public long roomId;

    @ColumnInfo(name = "service_id")
    public long serviceId;

    @ColumnInfo(name = "created_at")
    public Date createdAt;
}

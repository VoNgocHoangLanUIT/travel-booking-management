package com.hotelmanagement.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "favorites",
        foreignKeys = {
                @ForeignKey(
                        entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = RoomEntity.class,
                        parentColumns = "id",
                        childColumns = "room_id",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = {"user_id", "room_id"}, unique = true),
                @Index(value = "room_id")
        }
)
public class FavoriteEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(name = "room_id")
    public long roomId;

    @ColumnInfo(name = "created_at")
    public Date createdAt;
}

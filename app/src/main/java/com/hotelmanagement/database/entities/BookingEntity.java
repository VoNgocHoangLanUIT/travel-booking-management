package com.hotelmanagement.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "bookings",
        foreignKeys = {
                @ForeignKey(
                        entity = RoomEntity.class,
                        parentColumns = "id",
                        childColumns = "room_id",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "guest_id",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = "room_id"),
                @Index(value = "guest_id")
        }
)
public class BookingEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "room_id")
    public long roomId;

    @ColumnInfo(name = "guest_id")
    public long guestId;

    @ColumnInfo(name = "check_in_date")
    public Date checkInDate;

    @ColumnInfo(name = "check_out_date")
    public Date checkOutDate;

    @ColumnInfo(name = "guest_count")
    public int guestCount;

    @ColumnInfo(name = "total_price")
    public double totalPrice;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "created_at")
    public Date createdAt;

    @ColumnInfo(name = "updated_at")
    public Date updatedAt;
}

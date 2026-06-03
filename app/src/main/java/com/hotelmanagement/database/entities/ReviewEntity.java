package com.hotelmanagement.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "reviews",
        foreignKeys = @ForeignKey(
                entity = BookingEntity.class,
                parentColumns = "id",
                childColumns = "booking_id",
                onDelete = CASCADE
        ),
        indices = {
                @Index(value = "booking_id", unique = true)
        }
)
public class ReviewEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "booking_id")
    public long bookingId;

    @ColumnInfo(name = "rating")
    public int rating;

    @ColumnInfo(name = "comment")
    public String comment;

    @ColumnInfo(name = "created_at")
    public Date createdAt;
}

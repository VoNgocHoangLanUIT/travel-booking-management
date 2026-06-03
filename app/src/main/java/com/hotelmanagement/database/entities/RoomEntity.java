package com.hotelmanagement.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "rooms",
        foreignKeys = @ForeignKey(
                entity = UserEntity.class,
                parentColumns = "id",
                childColumns = "host_id",
                onDelete = CASCADE
        ),
        indices = {
                @Index(value = "host_id")
        }
)
public class RoomEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "host_id")
    public long hostId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "city")
    public String city;

    @ColumnInfo(name = "price_per_night")
    public double pricePerNight;

    @ColumnInfo(name = "max_guests")
    public int maxGuests;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "rating")
    public double rating;

    @ColumnInfo(name = "image_res_id")
    public int imageResId;

    @ColumnInfo(name = "image_uri")
    public String imageUri;

    @ColumnInfo(name = "badge")
    public String badge;

    @ColumnInfo(name = "created_at")
    public Date createdAt;

    @ColumnInfo(name = "updated_at")
    public Date updatedAt;
}

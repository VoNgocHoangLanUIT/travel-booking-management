package com.hotelmanagement.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hotelmanagement.database.entities.ReviewEntity;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(ReviewEntity review);

    @Update
    int update(ReviewEntity review);

    @Delete
    int delete(ReviewEntity review);

    @Query("SELECT * FROM reviews WHERE id = :id LIMIT 1")
    ReviewEntity getById(long id);

    @Query("SELECT * FROM reviews WHERE booking_id = :bookingId LIMIT 1")
    ReviewEntity getByBookingId(long bookingId);

    @Query("SELECT reviews.* FROM reviews INNER JOIN bookings ON bookings.id = reviews.booking_id WHERE bookings.room_id = :roomId ORDER BY reviews.created_at DESC")
    List<ReviewEntity> getByRoomId(long roomId);

    @Query("SELECT reviews.* FROM reviews INNER JOIN bookings ON bookings.id = reviews.booking_id WHERE bookings.guest_id = :guestId ORDER BY reviews.created_at DESC")
    List<ReviewEntity> getByGuestId(long guestId);

    @Query("SELECT COUNT(*) FROM reviews INNER JOIN bookings ON bookings.id = reviews.booking_id WHERE bookings.room_id = :roomId")
    int countByRoomId(long roomId);

    @Query("SELECT AVG(rating) FROM reviews INNER JOIN bookings ON bookings.id = reviews.booking_id WHERE bookings.room_id = :roomId")
    Double getAverageRatingByRoomId(long roomId);
}


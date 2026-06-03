package com.hotelmanagement.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hotelmanagement.database.entities.BookingEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(BookingEntity booking);

    @Update
    int update(BookingEntity booking);

    @Delete
    int delete(BookingEntity booking);

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    BookingEntity getById(long id);

    @Query("SELECT * FROM bookings WHERE room_id = :roomId ORDER BY created_at DESC")
    List<BookingEntity> getByRoomId(long roomId);

    @Query("SELECT * FROM bookings WHERE guest_id = :guestId ORDER BY created_at DESC")
    List<BookingEntity> getByGuestId(long guestId);

    @Query("SELECT bookings.* FROM bookings INNER JOIN rooms ON rooms.id = bookings.room_id WHERE rooms.host_id = :hostId ORDER BY bookings.created_at DESC")
    List<BookingEntity> getByHostId(long hostId);

    @Query("SELECT COUNT(*) FROM bookings " +
            "WHERE room_id = :roomId " +
            "AND (status IS NULL OR status NOT IN ('cancelled', 'canceled')) " +
            "AND check_in_date < :checkOutDate " +
            "AND check_out_date > :checkInDate")
    int countOverlappingBookings(long roomId, Date checkInDate, Date checkOutDate);
}

package com.hotelmanagement.services;

import android.content.Context;

import com.hotelmanagement.database.AppDatabase;
import com.hotelmanagement.database.dao.BookingDao;
import com.hotelmanagement.database.entities.BookingEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingService {
    private final BookingDao bookingDao;

    public BookingService(Context context) {
        this.bookingDao = AppDatabase.getInstance(context).bookingDao();
    }

    public long createBooking(BookingEntity booking) {
        if (booking == null
                || !isRoomAvailable(booking.roomId, booking.checkInDate, booking.checkOutDate)) {
            return -1L;
        }

        return bookingDao.insert(booking);
    }

    public int updateBooking(BookingEntity booking) {
        return bookingDao.update(booking);
    }

    public BookingEntity getBookingById(long id) {
        return bookingDao.getById(id);
    }

    public List<BookingEntity> getBookingsByRoom(long roomId) {
        return bookingDao.getByRoomId(roomId);
    }

    public List<BookingEntity> getActiveBookingsByRoom(long roomId) {
        List<BookingEntity> bookings = bookingDao.getByRoomId(roomId);
        List<BookingEntity> activeBookings = new ArrayList<>();
        for (BookingEntity booking : bookings) {
            if (booking != null
                    && isActiveStatus(booking.status)
                    && booking.checkInDate != null
                    && booking.checkOutDate != null
                    && booking.checkOutDate.after(booking.checkInDate)) {
                activeBookings.add(booking);
            }
        }
        return activeBookings;
    }

    public List<BookingEntity> getBookingsByGuest(long guestId) {
        return bookingDao.getByGuestId(guestId);
    }

    public List<BookingEntity> getBookingsByHost(long hostId) {
        return bookingDao.getByHostId(hostId);
    }

    public boolean isRoomAvailable(long roomId, Date checkInDate, Date checkOutDate) {
        if (roomId <= 0 || checkInDate == null || checkOutDate == null || !checkOutDate.after(checkInDate)) {
            return false;
        }

        return bookingDao.countOverlappingBookings(roomId, checkInDate, checkOutDate) == 0;
    }

    private boolean isActiveStatus(String status) {
        return status == null
                || (!"cancelled".equalsIgnoreCase(status) && !"canceled".equalsIgnoreCase(status));
    }
}

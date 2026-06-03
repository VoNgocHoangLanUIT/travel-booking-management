package com.hotelmanagement.services;

import android.content.Context;

import com.hotelmanagement.database.AppDatabase;
import com.hotelmanagement.database.dao.ReviewDao;
import com.hotelmanagement.database.entities.ReviewEntity;

import java.util.List;

public class ReviewService {
    private final ReviewDao reviewDao;

    public ReviewService(Context context) {
        this.reviewDao = AppDatabase.getInstance(context).reviewDao();
    }

    public long createReview(ReviewEntity review) {
        return reviewDao.insert(review);
    }

    public int updateReview(ReviewEntity review) {
        return reviewDao.update(review);
    }

    public int deleteReview(ReviewEntity review) {
        return reviewDao.delete(review);
    }

    public ReviewEntity getReviewById(long id) {
        return reviewDao.getById(id);
    }

    public ReviewEntity getReviewByBooking(long bookingId) {
        return reviewDao.getByBookingId(bookingId);
    }

    public List<ReviewEntity> getReviewsByRoom(long roomId) {
        return reviewDao.getByRoomId(roomId);
    }

    public List<ReviewEntity> getReviewsByGuest(long guestId) {
        return reviewDao.getByGuestId(guestId);
    }

    public int countReviewsByRoom(long roomId) {
        return reviewDao.countByRoomId(roomId);
    }

    public double getAverageRatingByRoom(long roomId, double fallbackRating) {
        Double averageRating = reviewDao.getAverageRatingByRoomId(roomId);
        return averageRating == null ? fallbackRating : averageRating;
    }
}


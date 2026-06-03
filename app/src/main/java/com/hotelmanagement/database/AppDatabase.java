package com.hotelmanagement.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.hotelmanagement.database.converters.DateConverters;
import com.hotelmanagement.database.dao.BookingDao;
import com.hotelmanagement.database.dao.FavoriteDao;
import com.hotelmanagement.database.dao.NotificationDao;
import com.hotelmanagement.database.dao.ReviewDao;
import com.hotelmanagement.database.dao.RoomDao;
import com.hotelmanagement.database.dao.RoomServiceDao;
import com.hotelmanagement.database.dao.ServiceDao;
import com.hotelmanagement.database.dao.UserDao;
import com.hotelmanagement.database.entities.BookingEntity;
import com.hotelmanagement.database.entities.FavoriteEntity;
import com.hotelmanagement.database.entities.NotificationEntity;
import com.hotelmanagement.database.entities.ReviewEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.database.entities.RoomServiceEntity;
import com.hotelmanagement.database.entities.ServiceEntity;
import com.hotelmanagement.database.entities.UserEntity;

@Database(
        entities = {
                UserEntity.class,
                RoomEntity.class,
                BookingEntity.class,
                ReviewEntity.class,
                FavoriteEntity.class,
                ServiceEntity.class,
                RoomServiceEntity.class,
                NotificationEntity.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters(DateConverters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract UserDao userDao();

    public abstract RoomDao roomDao();

    public abstract BookingDao bookingDao();

    public abstract ReviewDao reviewDao();

    public abstract FavoriteDao favoriteDao();

    public abstract ServiceDao serviceDao();

    public abstract RoomServiceDao roomServiceDao();

    public abstract NotificationDao notificationDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "hotel_management.db"
                            )
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
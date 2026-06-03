package com.hotelmanagement.services;

import android.content.Context;

import com.hotelmanagement.database.AppDatabase;
import com.hotelmanagement.database.dao.FavoriteDao;
import com.hotelmanagement.database.entities.FavoriteEntity;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.models.Room;
import com.hotelmanagement.services.mappers.RoomMapper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteService {
    private final FavoriteDao favoriteDao;
    private final RoomMapper roomMapper;

    public FavoriteService(Context context) {
        this.favoriteDao = AppDatabase.getInstance(context).favoriteDao();
        this.roomMapper = new RoomMapper();
    }

    public long addFavorite(FavoriteEntity favorite) {
        return favoriteDao.insert(favorite);
    }

    public int removeFavorite(FavoriteEntity favorite) {
        return favoriteDao.delete(favorite);
    }

    public List<FavoriteEntity> getFavoritesByUser(long userId) {
        return favoriteDao.getByUserId(userId);
    }

    public FavoriteEntity getFavorite(long userId, long roomId) {
        return favoriteDao.getByUserAndRoom(userId, roomId);
    }

    public List<Room> getFavoriteRoomModels(long userId) {
        List<RoomEntity> rooms = favoriteDao.getFavoriteRooms(userId);
        List<Room> result = new ArrayList<>();
        for (RoomEntity room : rooms) {
            result.add(roomMapper.fromEntity(room));
        }
        return result;
    }
}

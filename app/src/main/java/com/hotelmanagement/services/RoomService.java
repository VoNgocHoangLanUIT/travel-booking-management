package com.hotelmanagement.services;

import android.content.Context;

import com.hotelmanagement.database.AppDatabase;
import com.hotelmanagement.database.dao.RoomDao;
import com.hotelmanagement.database.dao.RoomServiceDao;
import com.hotelmanagement.database.entities.RoomEntity;
import com.hotelmanagement.database.entities.RoomServiceEntity;
import com.hotelmanagement.models.Room;
import com.hotelmanagement.services.mappers.RoomMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomService {
    private final RoomDao roomDao;
    private final RoomServiceDao roomServiceDao;
    private final RoomMapper roomMapper;

    public RoomService(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.roomDao = database.roomDao();
        this.roomServiceDao = database.roomServiceDao();
        this.roomMapper = new RoomMapper();
    }

    public long createRoom(RoomEntity room) {
        return roomDao.insert(room);
    }

    public int updateRoom(RoomEntity room) {
        return roomDao.update(room);
    }

    public int deleteRoom(RoomEntity room) {
        return roomDao.delete(room);
    }

    public RoomEntity getRoomById(long id) {
        return roomDao.getById(id);
    }

    public List<RoomEntity> getRoomsByHost(long hostId) {
        return roomDao.getByHostId(hostId);
    }

    public List<RoomEntity> getRoomsByStatus(String status) {
        return roomDao.getByStatus(status);
    }

    public List<RoomEntity> getAllRooms() {
        return roomDao.getAll();
    }

    public List<RoomEntity> getAllPublishedRooms() {
        return roomDao.getAllPublished();
    }

    public List<RoomEntity> searchPublishedRooms(int guestCount) {
        return roomDao.searchPublished(Math.max(1, guestCount));
    }

    public List<RoomEntity> searchPublishedRooms(String city, int guestCount) {
        if (city == null || city.trim().isEmpty()) {
            return searchPublishedRooms(guestCount);
        }
        return roomDao.searchPublishedByCity(city.trim(), Math.max(1, guestCount));
    }

    public long addRoomService(RoomServiceEntity roomService) {
        return roomServiceDao.insert(roomService);
    }

    public int removeRoomService(RoomServiceEntity roomService) {
        return roomServiceDao.delete(roomService);
    }

    public List<RoomServiceEntity> getRoomServices(long roomId) {
        return roomServiceDao.getByRoomId(roomId);
    }

    public void replaceRoomServices(long roomId, List<Long> serviceIds) {
        if (roomId <= 0) {
            return;
        }

        roomServiceDao.deleteByRoomId(roomId);
        if (serviceIds == null || serviceIds.isEmpty()) {
            return;
        }

        for (Long serviceId : serviceIds) {
            if (serviceId == null || serviceId <= 0) {
                continue;
            }

            RoomServiceEntity roomService = new RoomServiceEntity();
            roomService.roomId = roomId;
            roomService.serviceId = serviceId;
            roomService.createdAt = new Date();
            roomServiceDao.insert(roomService);
        }
    }

    public List<RoomEntity> getRoomsByCity(String city) {
        return roomDao.getByCity(city);
    }

    public List<String> getPublishedCities() {
        return roomDao.getPublishedCities();
    }

    public List<Room> getRoomModelsByCity(String city) {
        return mapRooms(roomDao.getByCity(city));
    }

    public List<Room> getAllRoomModels() {
        return mapRooms(roomDao.getAllPublished());
    }

    public List<Room> searchPublishedRoomModels(int guestCount) {
        return mapRooms(searchPublishedRooms(guestCount));
    }

    public List<Room> searchPublishedRoomModels(String city, int guestCount) {
        return mapRooms(searchPublishedRooms(city, guestCount));
    }

    private List<Room> mapRooms(List<RoomEntity> rooms) {
        List<Room> result = new ArrayList<>();
        for (RoomEntity room : rooms) {
            result.add(roomMapper.fromEntity(room));
        }
        return result;
    }
}

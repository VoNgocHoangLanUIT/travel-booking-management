package com.hotelmanagement.services;

import android.content.Context;

import com.hotelmanagement.database.AppDatabase;
import com.hotelmanagement.database.dao.ServiceDao;
import com.hotelmanagement.database.entities.ServiceEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceCatalogService {
    private static final String[][] DEFAULT_SERVICES = {
            {"Wifi", "Internet không dây tốc độ cao"},
            {"Bếp", "Khu vực nấu ăn cơ bản"},
            {"Máy lạnh", "Điều hòa không khí"},
            {"Máy giặt", "Có máy giặt cho khách sử dụng"},
            {"Bãi đỗ xe", "Có chỗ đỗ xe"},
            {"Hồ bơi", "Có hồ bơi"},
            {"Ban công", "Có ban công hoặc sân hiên"},
            {"Cho phép thú cưng", "Khách có thể mang thú cưng"},
            {"Tự nhận phòng", "Có thể tự check-in"},
            {"TV", "Có TV trong chỗ ở"}
    };

    private final ServiceDao serviceDao;

    public ServiceCatalogService(Context context) {
        this.serviceDao = AppDatabase.getInstance(context).serviceDao();
    }

    public long createService(ServiceEntity service) {
        return serviceDao.insert(service);
    }

    public int updateService(ServiceEntity service) {
        return serviceDao.update(service);
    }

    public List<ServiceEntity> getAllServices() {
        seedDefaultServicesIfNeeded();
        return serviceDao.getAll();
    }

    public ServiceEntity getServiceById(long id) {
        return serviceDao.getById(id);
    }

    public List<ServiceEntity> getServicesByRoom(long roomId) {
        if (roomId <= 0) {
            return new ArrayList<>();
        }
        return serviceDao.getByRoomId(roomId);
    }

    public List<Long> getSelectedServiceIds(long roomId) {
        List<Long> serviceIds = new ArrayList<>();
        for (ServiceEntity service : getServicesByRoom(roomId)) {
            serviceIds.add(service.id);
        }
        return serviceIds;
    }

    public String getServiceNamesText(long roomId) {
        List<ServiceEntity> services = getServicesByRoom(roomId);
        if (services.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < services.size(); i++) {
            if (i > 0) {
                builder.append(" · ");
            }
            builder.append(services.get(i).name);
        }
        return builder.toString();
    }

    public void seedDefaultServicesIfNeeded() {
        for (String[] defaultService : DEFAULT_SERVICES) {
            String name = defaultService[0];
            if (serviceDao.getByName(name) != null) {
                continue;
            }

            ServiceEntity service = new ServiceEntity();
            service.name = name;
            service.description = defaultService[1];
            service.createdAt = new Date();
            serviceDao.insert(service);
        }
    }
}


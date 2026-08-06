package com.auca.library.service;

import com.auca.library.dao.LocationDao;
import com.auca.library.domain.Location;
import com.auca.library.domain.enums.LocationType;
import com.auca.library.exception.DuplicateLocationCodeException;

import java.util.UUID;

public class LocationService {

    private final LocationDao locationDao = new LocationDao();

    public Location createLocation(Location location, UUID parentId) {
        if (locationDao.findByCode(location.getLocationCode()) != null) {
            throw new DuplicateLocationCodeException("Location code already exists: " + location.getLocationCode());
        }
        if (location.getLocationType() != LocationType.PROVINCE) {
            if (parentId == null || locationDao.findById(parentId) == null) {
                throw new IllegalArgumentException("A valid parentId is required for non-PROVINCE locations");
            }
            location.setParentId(parentId);
        }
        locationDao.save(location);
        return location;
    }

    public String getProvinceNameByVillageId(UUID villageId) {
        Location current = locationDao.findById(villageId);
        while (current != null && current.getLocationType() != LocationType.PROVINCE) {
            current = locationDao.findById(current.getParentId());
        }
        return current != null ? current.getLocationName() : null;
    }
}

package com.auca.library.service;

import com.auca.library.dao.UserDao;
import com.auca.library.domain.User;
import com.auca.library.util.PasswordUtil;

import java.util.UUID;

public class UserService {

    private final UserDao userDao = new UserDao();
    private final LocationService locationService = new LocationService();

    public String getProvinceNameByPersonId(UUID personId) {
        User user = userDao.findById(personId);
        if (user == null) return null;
        return locationService.getProvinceNameByVillageId(user.getVillageId());
    }

    public boolean authenticate(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return false;
        }
        User user = userDao.findByUsername(username);
        if (user == null) return false;
        return user.getPassword().equals(PasswordUtil.hash(rawPassword));
    }
}

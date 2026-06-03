package com.hotelmanagement.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.hotelmanagement.database.AppDatabase;
import com.hotelmanagement.database.dao.UserDao;
import com.hotelmanagement.database.entities.UserEntity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserService {
    private static final String PREFS_NAME = "auth_session";
    private static final String KEY_USER_ID = "user_id";

    private final UserDao userDao;
    private final SharedPreferences prefs;

    public UserService(Context context) {
        this.userDao = AppDatabase.getInstance(context).userDao();
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public long createUser(UserEntity user) {
        return userDao.insert(user);
    }

    public int updateUser(UserEntity user) {
        return userDao.update(user);
    }

    public UserEntity getUserById(long id) {
        return userDao.getById(id);
    }

    public UserEntity getUserByEmail(String email) {
        return userDao.getByEmail(email);
    }

    public List<UserEntity> getAllUsers() {
        return userDao.getAll();
    }

    public UserEntity signIn(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        UserEntity user = userDao.getByEmail(email.trim().toLowerCase(Locale.US));
        if (user == null) {
            return null;
        }
        String hashed = hashPassword(password);
        if (hashed == null || !hashed.equals(user.passwordHash)) {
            return null;
        }
        setCurrentUserId(user.id);
        return user;
    }

    public UserEntity signUp(String fullName, String email, String password, String phone, boolean isHost) {
        if (email == null || password == null) {
            return null;
        }
        String normalizedEmail = email.trim().toLowerCase(Locale.US);
        if (userDao.getByEmail(normalizedEmail) != null) {
            return null;
        }
        String hashed = hashPassword(password);
        if (hashed == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.fullName = fullName == null || fullName.trim().isEmpty() ? "Khach" : fullName.trim();
        user.email = normalizedEmail;
        user.passwordHash = hashed;
        user.phone = phone == null ? "" : phone.trim();
        user.isHost = isHost;
        user.createdAt = new Date();
        user.updatedAt = new Date();

        long id = userDao.insert(user);
        if (id <= 0) {
            return null;
        }
        user.id = id;
        setCurrentUserId(id);
        return user;
    }

    public UserEntity getCurrentUser() {
        long userId = prefs.getLong(KEY_USER_ID, 0L);
        return userId > 0 ? userDao.getById(userId) : null;
    }

    public void signOut() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }

    private void setCurrentUserId(long userId) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : encoded) {
                builder.append(String.format(Locale.US, "%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}

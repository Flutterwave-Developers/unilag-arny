package com.yorubadev.arny.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntry userEntry);

    @Query("SELECT * FROM users ORDER BY uid LIMIT 1")
    UserEntry getUser();

    @Query("SELECT * FROM users WHERE uid = :userId")
    LiveData<UserEntry> getUserById(String userId);

    @Query("SELECT phoneNumber FROM users WHERE uid = :userId")
    LiveData<String> getUserPhoneNumber(String userId);

    @Query("SELECT phoneNumber FROM users WHERE uid = :userId")
    String getUserPhoneNumberString(String userId);

    @Query("UPDATE users SET phoneNumber = :newPhoneNumber WHERE uid = :userId")
    void updatePhoneNumber(String userId, String newPhoneNumber);

    @Query("DELETE FROM users")
    void deleteAllUsers();

    @Query("UPDATE users SET name = :name WHERE uid = :userId")
    void updateName(String userId, String name);

    @Query("SELECT name FROM users WHERE uid = :userId LIMIT 1")
    LiveData<String> getUserName(String userId);
}
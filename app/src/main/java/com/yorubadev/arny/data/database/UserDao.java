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

    @Query("SELECT * FROM users ORDER BY pushId LIMIT 1")
    UserEntry getUser();

    @Query("SELECT * FROM users WHERE pushId = :userId")
    LiveData<UserEntry> getUserById(String userId);

    @Query("SELECT phoneNumber FROM users WHERE pushId = :userId")
    LiveData<String> getUserPhoneNumber(String userId);

    @Query("SELECT phoneNumber FROM users WHERE pushId = :userId")
    String getUserPhoneNumberString(String userId);

    @Query("UPDATE users SET phoneNumber = :newPhoneNumber WHERE pushId = :userId")
    void updatePhoneNumber(String userId, String newPhoneNumber);

    @Query("SELECT name, statusMessage FROM users WHERE pushId = :userId")
    LiveData<ModifiableUserData> getModifiableUserData(String userId);

    @Query("SELECT name, statusMessage, phoneNumber FROM users WHERE pushId = :userId")
    LiveData<ModifiableUserDataWithPhone> getModifiableUserDataWithPhone(String userId);

    @Query("DELETE FROM users")
    void deleteAllUsers();

    @Query("UPDATE users SET statusMessage = :statusMessage WHERE pushId = :userId")
    void updateStatusMessage(String userId, String statusMessage);

    @Query("UPDATE users SET name = :name WHERE pushId = :userId")
    void updateName(String userId, String name);

    @Query("SELECT name FROM users WHERE pushId = :userId LIMIT 1")
    LiveData<String> getUserName(String userId);
}
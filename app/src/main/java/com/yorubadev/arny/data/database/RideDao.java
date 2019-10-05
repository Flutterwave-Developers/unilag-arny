package com.yorubadev.arny.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface RideDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRide(RideEntry rideEntry);

    @Query("SELECT * FROM rides ORDER BY rideId")
    UserEntry getAllRides();
}
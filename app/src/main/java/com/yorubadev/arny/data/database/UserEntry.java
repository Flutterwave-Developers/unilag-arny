package com.yorubadev.arny.data.database;


import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Keep
@Entity(tableName = "users")
public class UserEntry {

    @PrimaryKey
    @NonNull
    private String uid = "";
    private String name;
    private String phoneNumber;
    private double safetyRating;

    // Empty constructor for Firebase
    @Ignore
    public UserEntry() {
    }

    public UserEntry(@NonNull String uid, String name, String phoneNumber, double safetyRating) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
        this.safetyRating = safetyRating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public double getSafetyRating() {
        return safetyRating;
    }

    public void setSafetyRating(double safetyRating) {
        this.safetyRating = safetyRating;
    }
}

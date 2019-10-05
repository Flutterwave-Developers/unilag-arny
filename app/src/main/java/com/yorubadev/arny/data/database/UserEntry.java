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
    private String pushId = "";
    private String name;
    private String phoneNumber;
    private String statusMessage;

    // Empty constructor for Firebase
    @Ignore
    public UserEntry() {
    }

    public UserEntry(@NonNull String pushId, String name, String phoneNumber, String statusMessage) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.pushId = pushId;
        this.statusMessage = statusMessage;
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
    public String getPushId() {
        return pushId;
    }

    public void setPushId(@NonNull String pushId) {
        this.pushId = pushId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}

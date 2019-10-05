package com.yorubadev.arny.data.database;

import androidx.annotation.Keep;

@Keep
public class RideResponse {

    private String riderId;
    private String ridePrefs;
    private String pickup;
    private long timestamp;

    public RideResponse(String riderId, String ridePrefs, String pickup, long timestamp) {
        this.riderId = riderId;
        this.ridePrefs = ridePrefs;
        this.pickup = pickup;
        this.timestamp = timestamp;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getRidePrefs() {
        return ridePrefs;
    }

    public void setRidePrefs(String ridePrefs) {
        this.ridePrefs = ridePrefs;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

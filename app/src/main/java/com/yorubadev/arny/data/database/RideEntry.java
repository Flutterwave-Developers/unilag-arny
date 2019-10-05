package com.yorubadev.arny.data.database;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Keep
@Entity(tableName = "rides")
public class RideEntry {

    @PrimaryKey
    @NonNull
    private String rideId = "";
    private String from;
    private String to;
    private int numSeats;
    private String ridePrefs;
    private long departureTs;
    private String rideService;
    private double rideEstimate;
    private long timestamp;

    // Empty constructor for Firebase
    @Ignore
    public RideEntry() {
    }

    public RideEntry(@NonNull String rideId, String from, String to, int numSeats, String ridePrefs, long departureTs, String rideService, double rideEstimate, long timestamp) {
        this.rideId = rideId;
        this.from = from;
        this.to = to;
        this.numSeats = numSeats;
        this.ridePrefs = ridePrefs;
        this.departureTs = departureTs;
        this.rideService = rideService;
        this.rideEstimate = rideEstimate;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getRideId() {
        return rideId;
    }

    public void setRideId(@NonNull String rideId) {
        this.rideId = rideId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getNumSeats() {
        return numSeats;
    }

    public void setNumSeats(int numSeats) {
        this.numSeats = numSeats;
    }

    public String getRidePrefs() {
        return ridePrefs;
    }

    public void setRidePrefs(String ridePrefs) {
        this.ridePrefs = ridePrefs;
    }

    public long getDepartureTs() {
        return departureTs;
    }

    public void setDepartureTs(long departureTs) {
        this.departureTs = departureTs;
    }

    public String getRideService() {
        return rideService;
    }

    public void setRideService(String rideService) {
        this.rideService = rideService;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getRideEstimate() {
        return rideEstimate;
    }

    public void setRideEstimate(double rideEstimate) {
        this.rideEstimate = rideEstimate;
    }
}

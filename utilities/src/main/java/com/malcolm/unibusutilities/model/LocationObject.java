package com.malcolm.unibusutilities.model;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class LocationObject {

    private Boolean success;
    private Location location;

    public LocationObject(@NonNull Boolean success, @Nullable Location location) {
        this.success = success;
        this.location = location;
        //Todo: Make into Kotlin Data Class in future
    }

    public Boolean isSuccessful() {
        return success;
    }

    public Location getLocation() {
        return location;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

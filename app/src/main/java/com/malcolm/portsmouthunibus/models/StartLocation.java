package com.malcolm.portsmouthunibus.models;


import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class StartLocation implements Serializable {
    @SerializedName("lat")
    private Double lat;
    @SerializedName("lng")
    private Double lng;
    private final static long serialVersionUID = -1095649872761905358L;


    public Location getLocation() {
        Location location = new Location("Last Location");
        location.setLatitude(lat);
        location.setLongitude(lng);
        return location;
    }
}

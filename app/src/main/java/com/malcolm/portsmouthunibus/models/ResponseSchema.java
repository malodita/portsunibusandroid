package com.malcolm.portsmouthunibus.models;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ResponseSchema implements Serializable {
    @SerializedName("routes")
    private List<Route> routes = null;
    @SerializedName("status")
    private String status;
    private long time;
    private Location location;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 2988541670284738700L;



    public List<Route> getRoutes() {
        return routes;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Location getLocation() {
        return getRoutes().get(0).getLegs().get(0).getStartLocation().getLocation();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }


}


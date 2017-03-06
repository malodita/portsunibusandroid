package com.malcolm.portsmouthunibus.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class OverviewPolyline implements Serializable {
    @SerializedName("points")
    private String points;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 1844494828718790673L;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }


}

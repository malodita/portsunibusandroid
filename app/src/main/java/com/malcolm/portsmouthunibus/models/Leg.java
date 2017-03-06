package com.malcolm.portsmouthunibus.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Leg implements Serializable {

    private Distance distance;
    private Duration duration;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 8160144812660242559L;

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }



}

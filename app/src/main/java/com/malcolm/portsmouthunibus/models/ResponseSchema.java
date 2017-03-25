package com.malcolm.portsmouthunibus.models;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ResponseSchema implements Serializable {
    private final static long serialVersionUID = 2988541670284738700L;
    @SerializedName("routes")
    private List<Route> routes = null;
    @SerializedName("status")
    private String status;
    private long time;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<Route> getRoutes() {
        return routes;
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

    public static class Duration implements Serializable{
        @SerializedName("value")
        private Double value;
        private final static long serialVersionUID = -2907738741189184610L;


        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }
    }

    public static class StartLocation implements Serializable{
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

    public static class OverviewPolyline implements Serializable{
        @SerializedName("points")
        private String points;
        private final static long serialVersionUID = 1844494828718790673L;



        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }


    }

    public static class Distance implements Serializable{
        @SerializedName("text")
        private String text;
        @SerializedName("value")
        private Integer value;
        private final static long serialVersionUID = -3105138012835463825L;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    public static class Route implements Serializable{

        private List<Leg> legs = null;
        @SerializedName("overview_polyline")
        private OverviewPolyline overviewPolyline;
        @SerializedName("summary")
        private String summary;
        private final static long serialVersionUID = 8592740390759606326L;



        public List<Leg> getLegs() {
            return legs;
        }

        public void setLegs(List<Leg> legs) {
            this.legs = legs;
        }

        public OverviewPolyline getOverviewPolyline() {
            return overviewPolyline;
        }

        public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
            this.overviewPolyline = overviewPolyline;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

    }

    public static class Leg implements Serializable{

        private Distance distance;
        private Duration duration;
        @SerializedName("start_location")
        private StartLocation startLocation;
        private final static long serialVersionUID = 8160144812660242559L;


        public StartLocation getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(StartLocation startLocation) {
            this.startLocation = startLocation;
        }

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


}


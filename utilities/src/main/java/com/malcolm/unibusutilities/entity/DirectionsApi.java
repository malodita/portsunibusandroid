package com.malcolm.unibusutilities.entity;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class DirectionsApi implements Serializable {
    public static final int ERROR_NO_RESPONSE = -1;
    public static final int OK = 0;
    public static final int ERROR_NOT_OK_RESPONSE = 1;
    public static final int ERROR_NO_NETWORK = 2;
    public static final int ERROR_PERMISSION = 3;
    public static final int ERROR_NOT_CLOSE = 4;
    private final static long serialVersionUID = 2988541670284738700L;
    @SerializedName("routes")
    private List<Route> routes = null;
    @SerializedName("status")
    private String status;
    @SerializedName("time")
    private long time;


    /**
     * Error Code Summary
     * <p>
     * -1: No response
     * </p>
     * 1: Not an OK response
     * <p>
     * 2: No network return
     * </p>
     * 3: Permission error (Shouldn't normally happen)
     * <p>
     * 4: Location not close enough
     * </p>
     */
    @SerializedName("errorCode")
    private int errorCode = 0;
    /**
     * Reports an error is any
     */
    @SerializedName("isError")
    private boolean isError = false;

    /**
     * Reports if near a stop meaning no cache retrieval or api request was made
     */
    private boolean isNearStop = false;

    public boolean isNearStop() {
        return isNearStop;
    }

    public void setNearStop(boolean nearStop) {
        isNearStop = nearStop;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public long getCacheTime() {
        return time;
    }

    public void setCacheTime(long time) {
        this.time = time;
    }

    public List<Route> getRoutes() {
        if (routes == null){
            routes = new ArrayList<>();
            routes.add(new Route());
        }
        return routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Location getStartLocation() {
        return getRoutes().get(0).getLegs().get(0).getStartLocation().getLocation();
    }

    public void setStartLocation(Location startLocation) {
        this.getRoutes().get(0).getLegs().get(0).getStartLocation().setLocation(startLocation);
    }

    public Location getEndLocation() {
        return getRoutes().get(0).getLegs().get(0).getEndLocation().getLocation();
    }

    public void setEndLocation(Location endLocation) {
        this.getRoutes().get(0).getLegs().get(0).getEndLocation().setLocation(endLocation);
    }

    public interface Dao {

        /**
         * This is the method used to obtain a response from the directions API.
         *
         * @param origin      The start point for the request (AKA the current or last location)
         * @param destination The end point for the request
         * @param mode        The mode of transport tor eport for (Only use walking)
         * @param key         The directions API key
         *
         * @return A {@link DirectionsApi ResponseSchema} object with the returned information that is
         * then parsed by the models
         */
        @GET("/maps/api/directions/json?")
        Call<DirectionsApi> getResponse(@Query("origin") String origin,
                                        @Query("destination") String destination,
                                        @Query("mode") String mode,
                                        @Query("key") String key);

    }

    public static class Duration implements Serializable {
        private final static long serialVersionUID = -2907738741189184610L;
        @SerializedName("value")
        private Double value;

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }
    }

    public static class EndLocation implements Serializable
    {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;
        @SerializedName("provider")
        private String provider;
        private final static long serialVersionUID = -3966917252628940991L;

        public Location getLocation() {
            Location location = new Location("End Location");
            location.setLatitude(lat);
            location.setLongitude(lng);
            if (provider != null){
                location.setProvider(provider);
            }
            return location;
        }

        public void setLocation(Location location){
            lng = location.getLongitude();
            lat = location.getLatitude();
            provider = location.getProvider();

        }


    }



    public static class StartLocation implements Serializable {
        private final static long serialVersionUID = -1095649872761905358L;
        @SerializedName("lat")
        private Double lat;
        @SerializedName("lng")
        private Double lng;
        @SerializedName("provider")
        private String provider;

        public Location getLocation() {
            Location location = new Location("End Location");
            location.setLatitude(lat);
            location.setLongitude(lng);
            if (provider != null){
                location.setProvider(provider);
            }
            return location;
        }

        public void setLocation(Location location){
            lng = location.getLongitude();
            lat = location.getLatitude();
            provider = location.getProvider();

        }
    }

    public static class OverviewPolyline implements Serializable {
        private final static long serialVersionUID = 1844494828718790673L;
        @SerializedName("points")
        private String points;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }


    }

    public static class Distance implements Serializable {
        private final static long serialVersionUID = -3105138012835463825L;
        @SerializedName("text")
        private String text;
        @SerializedName("value")
        private Integer value;

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

    public static class Route implements Serializable {

        private final static long serialVersionUID = 8592740390759606326L;
        private List<Leg> legs = null;
        @SerializedName("overview_polyline")
        private OverviewPolyline overviewPolyline;
        @SerializedName("summary")
        private String summary;

        public List<Leg> getLegs() {
            if (legs == null){
                legs = new ArrayList<>();
                legs.add(new Leg());
            }
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

    public static class Leg implements Serializable {

        private final static long serialVersionUID = 8160144812660242559L;
        private Distance distance;
        private Duration duration;
        @SerializedName("start_location")
        private StartLocation startLocation;
        @SerializedName("end_location")
        private EndLocation endLocation;

        public StartLocation getStartLocation() {
            if (startLocation == null){
                startLocation = new StartLocation();
            }
            return startLocation;
        }

        public EndLocation getEndLocation() {
            if (endLocation == null){
                endLocation = new EndLocation();
            }
            return endLocation;
        }

        public void setEndLocation(EndLocation endLocation) {
            this.endLocation = endLocation;
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


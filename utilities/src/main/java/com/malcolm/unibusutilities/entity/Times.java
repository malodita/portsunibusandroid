package com.malcolm.unibusutilities.entity;

/**
 * Created by Malcolm on 22/08/2016. Forms the base class containing the time, stop name and an ID
 * that is not currently used at this point however may be in a future version
 *
 */
public class Times {
    private String time;
    private String destination;
    private int id;
    private String via;

    public Times() {
    }

    public Times(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }
}

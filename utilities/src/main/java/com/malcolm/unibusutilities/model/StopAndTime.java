package com.malcolm.unibusutilities.model;

public class StopAndTime {

    private String stop;
    private String time;

    public StopAndTime() {
    }

    public StopAndTime(String stop, String time) {
        this.stop = stop;
        this.time = time;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

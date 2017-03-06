package com.malcolm.portsmouthunibus.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Route implements Serializable{

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

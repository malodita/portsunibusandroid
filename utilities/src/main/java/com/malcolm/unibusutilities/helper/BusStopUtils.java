package com.malcolm.unibusutilities.helper;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * A small class which holds all the bus stop locations. It exposes static methods
 * and provides methods to make a stop array and to find and return the location for the closest stop.
 */

public final class BusStopUtils {
    private static final String TAG = "BusStopLocations";
    private static final LatLng LANGSTONE = new LatLng(50.79618265, -1.04210624);
    private static final LatLng LOCKSWAY = new LatLng(50.79337835, -1.05708995);
    private static final LatLng GOLDSMITHMILTON = new LatLng(50.79257583, -1.05841332);
    private static final LatLng GOLDSMITHLIDL = new LatLng(50.794932, -1.069818);
    private static final LatLng GOLDSMITHOPPLIDL = new LatLng(50.794973, -1.069486);
    private static final LatLng GOLDSMITHFRATTON = new LatLng(50.796089, -1.075942);
    private static final LatLng GOLDSMITHFAWCETT = new LatLng(50.795931, -1.075994);
    private static final LatLng WINSTONCHURCHILLHOTEL = new LatLng(50.795563, -1.092330);
    private static final LatLng CAMBRIDGEROAD = new LatLng(50.794552, -1.097006);
    private static final LatLng WINSTONCHURCHILLLAW = new LatLng(50.795592, -1.092266);
    private static final LatLng IMSEASTNEY = new LatLng(50.795075, -1.030000);
    //Centred locations
    private static final LatLng GOLDSMITHLIDLCENTRE = new LatLng(50.79507785, -1.06974676);
    private static final LatLng GOLDSMITHFRATTONCENTRE = new LatLng(50.7961137, -1.0759583);
    private static final LatLng WINSTONCHURCHILLROADCENTRE = new LatLng(50.795462, -1.092276);

    public static final String LANGSTONETAG = "Langstone";
    public static final String LOCKSWAYTAG = "Locksway Road (for Milton Park)";
    public static final String ADJLIDLTAG = "Goldsmith Avenue (adj Lidi)";
    public static final String MILTONTAG = "Goldsmith Avenue adj Milton Park";
    public static final String FAWCETTTAG = "Goldsmith Avenue (opp Fratton Station)";
    public static final String IBISTAG = "Winston Churchill Avenue (adj Ibis Hotel)";
    public static final String NUFFIELDTAG = "Cambridge Road (adj Nuffield Building)";
    public static final String LAWTAG = "Winston Churchill Avenue (adj Law Courts)";
    public static final String FRATTONTAG = "Goldsmith Avenue (adj Fratton Station)";
    public static final String OPPLIDLTAG = "Goldsmith Avenue (opp Lidl)";
    public static final String EASTNEYTAG = "IMS Eastney";

    public static final String LANGSTONESTOPTAG = "Langstone";
    public static final String LOCKSWAYSTOPTAG = "Locksway Road";
    public static final String POMPEYSTOPTAG = "Goldsmith Avenue (Pompey Centre)";
    public static final String MILTONSTOPTAG = "Goldsmith Avenue (Prince Albert Road)";
    public static final String BRIDGESTOPTAG = "Goldsmith Avenue (Fratton Bridge)";
    public static final String WINSTONSTOPTAG = "Winston Churchill Avenue";
    public static final String UNIONSTOPTAG =  "Cambridge Road";
    public static final String IMSSTOPTAG = "IMS Eastney";

    public static final String LANGSTONEINTROTAG = "Langstone";
    public static final String LOCKSWAYINTROTAG = "Locksway Road";
    public static final String ADJLIDLINTROTAG = "Goldsmith Avenue adj Lidl";
    public static final String FAWCETTINTROTAG = "Goldsmith Avenue opp Station";
    public static final String WINSTONINTROTAG = "Winston Churchill Avenue";





    /**
     * Obtains the closest stop to the current location the user
     * is in. WARNING: This method only works up to 3000 metres away from the closest stop. Any
     * further out and it will throw a null item for location. This possibility should be dealt
     * with
     *
     * @param currentLocation The current location provided by the calling class
     *
     * @return The location of the closest stop as a Location object or null if over 3km out.
     */
    @Nullable
    public static Location getClosestStop(Location currentLocation) {
        if (currentLocation == null){
            return null;
        }
        ArrayList<Location> stopLocations = new ArrayList<>();
        Location langstone = new Location(LANGSTONETAG);
        Location locksway = new Location(LOCKSWAYTAG);
        Location goldsmithLidl = new Location(ADJLIDLTAG);
        Location goldsmithMilton = new Location(MILTONTAG);
        Location goldsmithFawcett = new Location(FAWCETTTAG);
        Location winstonChurchill = new Location(IBISTAG);
        Location cambridgeRoad = new Location(NUFFIELDTAG);
        Location winstonChurchillLaw = new Location(LAWTAG);
        Location goldsmithFratton = new Location(FRATTONTAG);
        Location goldsmithOppLidl = new Location(OPPLIDLTAG);
        Location imsEastney = new Location(EASTNEYTAG);
        langstone.setLatitude(LANGSTONE.latitude);
        langstone.setLongitude(LANGSTONE.longitude);
        locksway.setLatitude(LOCKSWAY.latitude);
        locksway.setLongitude(LOCKSWAY.longitude);
        goldsmithLidl.setLatitude(GOLDSMITHLIDL.latitude);
        goldsmithLidl.setLongitude(GOLDSMITHLIDL.longitude);
        goldsmithMilton.setLatitude(GOLDSMITHMILTON.latitude);
        goldsmithMilton.setLongitude(GOLDSMITHMILTON.longitude);
        goldsmithFawcett.setLatitude(GOLDSMITHFAWCETT.latitude);
        goldsmithFawcett.setLongitude(GOLDSMITHFAWCETT.longitude);
        winstonChurchill.setLatitude(WINSTONCHURCHILLHOTEL.latitude);
        winstonChurchill.setLongitude(WINSTONCHURCHILLHOTEL.longitude);
        cambridgeRoad.setLatitude(CAMBRIDGEROAD.latitude);
        cambridgeRoad.setLongitude(CAMBRIDGEROAD.longitude);
        winstonChurchillLaw.setLongitude(WINSTONCHURCHILLLAW.longitude);
        winstonChurchillLaw.setLatitude(WINSTONCHURCHILLLAW.latitude);
        goldsmithFratton.setLatitude(GOLDSMITHFRATTON.latitude);
        goldsmithFratton.setLongitude(GOLDSMITHFRATTON.longitude);
        goldsmithOppLidl.setLatitude(GOLDSMITHOPPLIDL.latitude);
        goldsmithOppLidl.setLongitude(GOLDSMITHOPPLIDL.longitude);
        imsEastney.setLatitude(IMSEASTNEY.latitude);
        imsEastney.setLongitude(IMSEASTNEY.longitude);
        stopLocations.add(langstone);
        stopLocations.add(locksway);
        stopLocations.add(goldsmithLidl);
        stopLocations.add(goldsmithOppLidl);
        stopLocations.add(goldsmithFratton);
        stopLocations.add(goldsmithFawcett);
        stopLocations.add(goldsmithMilton);
        stopLocations.add(winstonChurchill);
        stopLocations.add(winstonChurchillLaw);
        stopLocations.add(cambridgeRoad);
        stopLocations.add(imsEastney);
        float best = 3000;
        Location closest = null;
        for (int i = 0; i < stopLocations.size(); i++) {
            float test = currentLocation.distanceTo(stopLocations.get(i));
            if (test < best) {
                best = currentLocation.distanceTo(stopLocations.get(i));
                closest = stopLocations.get(i);
            }
        }
        return closest;
    }

    public static String getShortenedStopName(String longName){
        String shortname = "";
        switch (longName){
            case "Langstone":
                shortname = "Langstone";
                break;
            case "Locksway Road (for Milton Park)":
                shortname = "Locksway Road";
                break;
            case "Goldsmith Avenue (adj Lidi)":
                shortname = "Goldsmith Ave";
                break;
            case "Goldsmith Avenue (opp Fratton Station)":
                shortname = "Fawcett Road";
                break;
            case "Winston Churchill Avenue (adj Ibis Hotel)":
                shortname = "University";
                break;
            case "Cambridge Road (adj Nuffield Building)":
                shortname = "Student Union";
                break;
            case "Winston Churchill Avenue (adj Law Courts)":
                shortname = "Law Courts";
                break;
            case "Goldsmith Avenue (adj Fratton Station)":
                shortname = "Fratton Station";
                break;
            case "Goldsmith Avenue (opp Lidl)":
                shortname = "Goldsmith opp Lidl";
                break;
            case "Goldsmith Avenue adj Milton Park":
                shortname = "Milton Park";
                break;
            case "IMS Eastney (Departures)":
                shortname = "IMS Eastney";
                break;
        }
        return shortname;
    }

    /**
     * Creates an array of the bus stop locations
     *
     * @return An ArrayList of bus stops
     */
    public static ArrayList<LatLng> makeArrayOfStops() {
        ArrayList<LatLng> latLngList = new ArrayList<>();
        latLngList.add(LANGSTONE);
        latLngList.add(LOCKSWAY);
        latLngList.add(GOLDSMITHLIDLCENTRE);
        latLngList.add(GOLDSMITHFRATTONCENTRE);
        latLngList.add(WINSTONCHURCHILLROADCENTRE);
        latLngList.add(CAMBRIDGEROAD);
        latLngList.add(GOLDSMITHMILTON);
        latLngList.add(IMSEASTNEY);
        return latLngList;
    }
}

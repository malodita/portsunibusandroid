package com.malcolm.unibusutilities;

import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * A small class which holds all the bus stop locations. It exposes static methods
 * prevent unneccesary object creation and provides methods to make a stop array and to find
 * and return the location fo the closest stop.
 */

public final class BusStops {
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
    //Centred locations
    private static final LatLng GOLDSMITHLIDLCENTRE = new LatLng(50.79507785, -1.06974676);
    private static final LatLng GOLDSMITHFRATTONCENTRE = new LatLng(50.7961137, -1.0759583);
    private static final LatLng WINSTONCHURCHILLROADCENTRE = new LatLng(50.795462, -1.092276);


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
        ArrayList<Location> stopLocations = new ArrayList<>();
        Location langstone = new Location("Langstone");
        langstone.setLatitude(LANGSTONE.latitude);
        langstone.setLongitude(LANGSTONE.longitude);
        Location locksway = new Location("Locksway Road (for Milton Park)");
        locksway.setLatitude(LOCKSWAY.latitude);
        locksway.setLongitude(LOCKSWAY.longitude);
        Location goldsmithLidl = new Location("Goldsmith Avenue (adj Lidi)");
        goldsmithLidl.setLatitude(GOLDSMITHLIDL.latitude);
        goldsmithLidl.setLongitude(GOLDSMITHLIDL.longitude);
        Location goldsmithMilton = new Location("Goldsmith Avenue adj Milton Park");
        goldsmithMilton.setLatitude(GOLDSMITHMILTON.latitude);
        goldsmithMilton.setLongitude(GOLDSMITHMILTON.longitude);
        Location goldsmithFawcett = new Location("Goldsmith Avenue (opp Fratton Station)");
        goldsmithFawcett.setLatitude(GOLDSMITHFAWCETT.latitude);
        goldsmithFawcett.setLongitude(GOLDSMITHFAWCETT.longitude);
        Location winstonChurchill = new Location("Winston Churchill Avenue (adj Ibis Hotel)");
        winstonChurchill.setLatitude(WINSTONCHURCHILLHOTEL.latitude);
        winstonChurchill.setLongitude(WINSTONCHURCHILLHOTEL.longitude);
        Location cambridgeRoad = new Location("Cambridge Road (adj Nuffield Building)");
        cambridgeRoad.setLatitude(CAMBRIDGEROAD.latitude);
        cambridgeRoad.setLongitude(CAMBRIDGEROAD.longitude);
        Location winstonChurchillLaw = new Location("Winston Churchill Avenue (adj Law Courts)");
        winstonChurchillLaw.setLatitude(WINSTONCHURCHILLLAW.latitude);
        winstonChurchillLaw.setLongitude(WINSTONCHURCHILLLAW.longitude);
        stopLocations.add(langstone);
        stopLocations.add(locksway);
        stopLocations.add(goldsmithLidl);
        stopLocations.add(goldsmithFawcett);
        stopLocations.add(goldsmithMilton);
        stopLocations.add(winstonChurchill);
        stopLocations.add(winstonChurchillLaw);
        stopLocations.add(cambridgeRoad);
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
        return latLngList;
    }
}

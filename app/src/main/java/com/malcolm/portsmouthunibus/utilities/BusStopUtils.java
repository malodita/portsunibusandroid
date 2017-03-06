package com.malcolm.portsmouthunibus.utilities;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This file contains methods to format
 */

public final class BusStopUtils {
    private static final String TAG = "BusStopUtils";
    private static BusStopUtils instance;

    private BusStopUtils() {
    }

    public static synchronized BusStopUtils getInstance() {
        if (instance == null) {
            instance = new BusStopUtils();
        }
        return instance;
    }

    /**
     * Converts the time from a figure displayed in seconds to one displayed in minutes. It also
     * takes care of the fact that versions of android before Marshmallow (API 24) wont have the
     * newer SimpleDateFormat API.
     *
     * @param timeToConvert the time to convert in seconds
     *
     * @return the time in minutes
     */
    @SuppressLint("SimpleDateFormat")
    public String formatTime(int timeToConvert) {
        String finalTime = null;
        Date temp;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                android.icu.text.SimpleDateFormat before = new android.icu.text.SimpleDateFormat("s");
                android.icu.text.SimpleDateFormat after = new android.icu.text.SimpleDateFormat("m");
                android.icu.text.SimpleDateFormat afterHour = new android.icu.text.SimpleDateFormat("K");
                temp = before.parse(Integer.toString(timeToConvert));
                if (timeToConvert > 3600) {
                    finalTime = after.format(temp);
                    int temp2 = Integer.valueOf(after.format(temp));
                    int temp3 = Integer.valueOf(afterHour.format(temp));
                    int finalTimeHour = (temp2 + (temp3 * 60));
                    finalTime = String.valueOf(finalTimeHour);
                } else {
                    finalTime = after.format(temp);
                }
            } else {
                SimpleDateFormat before = new SimpleDateFormat("s");
                SimpleDateFormat after = new SimpleDateFormat("mm");
                SimpleDateFormat afterSub10 = new SimpleDateFormat("m");
                SimpleDateFormat afterHour = new SimpleDateFormat("K");
                temp = before.parse(Integer.toString(timeToConvert));
                if (timeToConvert < 600) {
                    finalTime = afterSub10.format(temp);
                } else if (timeToConvert > 3600) {
                    int temp2 = Integer.valueOf(after.format(temp));
                    int temp3 = Integer.valueOf(afterHour.format(temp));
                    finalTime = String.valueOf(temp2 + (temp3 * 60));
                } else {
                    finalTime = after.format(temp);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            FirebaseCrash.log(TAG + "Parsing time failed");
            FirebaseCrash.report(e);
        }
        return finalTime;
    }

    /**
     * Draws the polyline between the current location as detected originally and the nearest stop.
     * Depending on the value returned by the Directions API request, it will either draw the
     * polyline and move the map camera to the centre of the polyline or move the camera tothe
     * nearest stop if within two minutes from the stop.
     *
     * @param map The googlemap to be edited.
     *
     * @throws JSONException If the array has an issue
     */
    public void drawPolyline(GoogleMap map, int nightMode, @Nullable Double time, @Nullable List<LatLng> list,
                             @Nullable LatLng singleLocation) {
        if (map == null) {
            return;
        }
        if (singleLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(singleLocation, 16f));
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(singleLocation);
            map.addMarker(markerOptions);
        } else {
            String formattedTime = formatTime(time.intValue());
            int last = (list.size() - 1);
            LatLng currentLocation = list.get(0);
            LatLng targetLocation = list.get(last);
            int color;
            if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
                color = Color.parseColor("#FFD740");
            } else {
                color = Color.parseColor("#2A1439");
            }
            if (Integer.valueOf(formattedTime) <= 2) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 16f));
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(targetLocation);
                map.addMarker(markerOptions);
            } else {
                PolylineOptions options = new PolylineOptions()
                        .addAll(list)
                        .width(20)
                        .color(color);
                map.addCircle(new CircleOptions()
                        .radius(50)
                        .strokeColor(Color.parseColor("#212121"))
                        .fillColor(Color.parseColor("#FFFFFF"))
                        .strokeWidth(10)
                        .center(list.get(last))
                        .zIndex(1));
                map.addCircle(new CircleOptions()
                        .center(list.get(last))
                        .fillColor(Color.parseColor("#212121"))
                        .zIndex(2)
                        .radius(10));
                LatLngBounds bounds = makeBounds(list);
                map.addPolyline(options);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(targetLocation);
                map.addMarker(markerOptions);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 14f));
            }
        }
    }

    private LatLngBounds makeBounds(List<LatLng> list){
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (int i = 0; i < list.size(); i++){
            builder.include(list.get(i));
        }
        return builder.build();
    }

    /**
     * Sets the travel time to the nearest stop, the capability to do anything with distance remains
     * however at this point it is not used.
     *
     * @return A string with the full statement to be displayed
     */
    public String setTimeAndDistanceToClosestStop(Double time) {
        int newTime = time.intValue();
        if (newTime == -1){
            //If the person is near the closest stop
            return "Close to the nearest stop";
        }
        String formattedTime = formatTime(newTime);
        //Distance can be used when you figure out a use for it
        StringBuilder timeTo = new StringBuilder();
        switch (Integer.valueOf(formattedTime)) {
            case 0:
                timeTo.append("At");
                break;
            case 1:
                timeTo.append(formattedTime).append(" minute to");
                break;
            default:
                timeTo.append(formattedTime).append(" minutes to");
        }
        timeTo.append(" nearest stop");
        return timeTo.toString();
    }

}


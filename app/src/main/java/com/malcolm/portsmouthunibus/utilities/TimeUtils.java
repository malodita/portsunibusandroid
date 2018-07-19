package com.malcolm.portsmouthunibus.utilities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This class contains some useful methods relating to formatting time and displaying maps and information
 */

public final class TimeUtils {
    private static final String TAG = "TimeUtils";
    public static final int MORNING = 1;
    public static final int AFTERNOON = 2;
    public static final int EVENING = 3;
    public static final int NIGHT = 4;

    private static final long MORNING_END = 36000;
    private static final long AFTERNOON_END = 61200;
    private static final long EVENING_END = 75600;


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
    private static String formatTime(int timeToConvert) {
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
        }
        return finalTime;
    }

    public static int getTimeOfDay(String time, boolean is24Hours) {
        int listed = convertTime(time, is24Hours);
        if (listed < MORNING_END){
            return MORNING;
        } else if (listed < AFTERNOON_END){
            return AFTERNOON;
        } else if (listed < EVENING_END){
            return EVENING;
        } else {
            return NIGHT;
        }
    }

    private static int convertTime(String time, boolean is24Hrs) {
        String[] split;
        if (!is24Hrs) {
            if (time.contains(" am")) {
                split = time.replace(" am", "").split(":");
                return ((Integer.valueOf(split[0]) * 60) + Integer.valueOf(split[1]) * 60);
            } else {
                split = time.replace(" pm", "").split(":");
                if (Integer.valueOf(split[0]) == 12) {
                    return ((Integer.valueOf(split[0]) * 60) + Integer.valueOf(split[1]) * 60);
                } else {
                    return (((Integer.valueOf(split[0]) + 12) * 60) + Integer.valueOf(split[1]) * 60);
                }
            }
        } else {
            split = time.split(":");
            return ((Integer.valueOf(split[0]) * 3600) + Integer.valueOf(split[1]) * 60);
        }
    }

    /**
     * Draws the polyline between the current location as detected originally and the nearest stop.
     * Depending on the value returned by the Directions API request, it will either draw the
     * polyline and move the map camera to the centre of the polyline or move the camera tothe
     * nearest stop if within two minutes from the stop.
     *
     * @param map The googlemap to be edited.
     */
    public static void markStop(@NonNull GoogleMap map, Double time, List<LatLng> list,
                                @Nullable LatLng singleLocation) {
        if (singleLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(singleLocation, 16f));
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(singleLocation);
            map.addMarker(markerOptions);
        } else {
            String formattedTime = formatTime(time.intValue());
            int last = (list.size() - 1);
            LatLng targetLocation = list.get(last);
            if (Integer.valueOf(formattedTime) <= 2) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15f));
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(targetLocation);
                map.addMarker(markerOptions);
            } else {
                if (Integer.valueOf(formattedTime) > 12) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 13f));
                } else {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(targetLocation);
                    map.addMarker(markerOptions);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 14f));
                }
            }
        }
    }

    /**
     * Sets the travel time to the nearest stop, the capability to do anything with distance remains
     * however at this point it is not used.
     *
     * @return A string with the full statement to be displayed
     */
    public static String setTimeAndDistanceToClosestStop(Double time, float distance) {
        int newTime = time.intValue();
        if (newTime == -1) {
            //If the person is near the closest stop
            if (distance < 30) {
                return "At stop";
            } else {

                return Math.round(distance) + " metres away";
            }
        }
        String formattedTime = formatTime(newTime);
        //Distance can be used when you figure out a use for it
        StringBuilder timeTo = new StringBuilder();
        switch (Integer.valueOf(formattedTime)) {
            case 0:
                return "At stop";
            case 1:
                timeTo.append(formattedTime).append(" minute");
                break;
            default:
                timeTo.append(formattedTime).append(" minutes");
        }
        timeTo.append(" away");
        return timeTo.toString();
    }

}


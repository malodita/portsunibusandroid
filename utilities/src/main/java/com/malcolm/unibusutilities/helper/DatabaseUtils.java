package com.malcolm.unibusutilities.helper;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;

import com.malcolm.unibusutilities.entity.Times;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class DatabaseUtils {


    private static final String EASTNEYD = "eastneyD";
    private static final String LANGSTONED = "langstoneD";
    private static final String LOCKSWAY = "locksway";
    private static final String GOLDSMITHADJ = "goldsmithAdj";
    private static final String GOLDSMITHFAW = "goldsmithFaw";
    private static final String WINSTONIBIS = "winstonIbis";
    private static final String UNION = "union";
    private static final String NUFFIELD = "nuffield";
    private static final String WINSTONLAW = "winstonLaw";
    private static final String GOLDSMITHFTN = "goldsmithFtn";
    private static final String GOLDSMITHOPP = "goldsmithOpp";
    private static final String MILTON = "milton";
    private static final String EASTNEY = "eastney";
    private static final String LANGSTONE = "langstone";


    public static String retrieveStop(int stop) {
        switch (stop) {
            case 0:
                return EASTNEYD;
            case 1:
                return LANGSTONED;
            case 2:
                return LOCKSWAY;
            case 3:
                return GOLDSMITHADJ;
            case 4:
                return GOLDSMITHFAW;
            case 5:
                return WINSTONIBIS;
            case 6:
                return UNION;
            case 7:
                return NUFFIELD;
            case 8:
                return WINSTONLAW;
            case 9:
                return GOLDSMITHFTN;
            case 10:
                return GOLDSMITHOPP;
            case 11:
                return MILTON;
            case 12:
                return EASTNEY;
            default:
                return LANGSTONE;
        }
    }

    /**
     * Obtains the stop to search the database here}
     *
     * @param homeStopId The id of the stop retrieved
     *
     * @return A formatted string for the search to use
     */
    public static String getStopToShow(int homeStopId) {
        String stopToShowString;
        switch (homeStopId) {
            case 1:
                stopToShowString = "[Langstone Campus (for Departures only)]";
                break;
            case 2:
                stopToShowString = "[Locksway Road (for Milton Park)]";
                break;
            case 3:
                stopToShowString = "[Goldsmith Avenue (adj Lidi)]";
                break;
            case 4:
                stopToShowString = "[Goldsmith Avenue (opp Fratton Station)]";
                break;
            default:
                stopToShowString = "[Winston Churchill Avenue (adj Ibis Hotel)]";
                break;
        }
        return stopToShowString;
    }

    /**
     * Converts the time from a figure displayed in seconds to one displayed in minutes. It also
     * takes care of the fact that versions of android before Marshmallow (API 24) wont have the
     * newer SimpleDateFormat API. It also can alter output depending on if the app should report time
     * in a 12 or 24 hour format.
     *
     * @param input     The time to convert in seconds.
     * @param is24Hours Tf the method should return the time in a 12 or 24 hour format
     *
     * @return The formatted time
     */
    public static String formatTime(String input, boolean is24Hours) {
        StringBuilder output = new StringBuilder();
        try {
            if (is24Hours) {
                if (Build.VERSION.SDK_INT >= 24) {
                    android.icu.text.SimpleDateFormat before = new android.icu.text.SimpleDateFormat("s");
                    Date date = before.parse(input);
                    android.icu.text.SimpleDateFormat after = new android.icu.text.SimpleDateFormat("HH:mm");
                    output.append(after.format(date));
                } else {
                    java.text.SimpleDateFormat before = new java.text.SimpleDateFormat("s");
                    Date date = before.parse(input);
                    java.text.SimpleDateFormat after = new java.text.SimpleDateFormat("HH:mm");
                    after.format(date);
                    output.append(after.format(date));
                }
            } else {
                if (Build.VERSION.SDK_INT >= 24) {
                    android.icu.text.SimpleDateFormat before = new android.icu.text.SimpleDateFormat("s");
                    Date date = before.parse(input);
                    android.icu.text.SimpleDateFormat after = new android.icu.text.SimpleDateFormat("h:mm");
                    output.append(after.format(date));
                } else {
                    java.text.SimpleDateFormat before = new java.text.SimpleDateFormat("s");
                    Date date = before.parse(input);
                    java.text.SimpleDateFormat after = new java.text.SimpleDateFormat("h:mm");
                    after.format(date);
                    output.append(after.format(date));
                }
                if (Integer.valueOf(input) >= 43200) {
                    output.append(" pm");
                } else {
                    output.append(" am");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    /**
     * Converts the time from a figure displayed in seconds to one displayed in minutes. It also
     * takes care of the fact that versions of android before Marshmallow (API 24) wont have the
     * newer SimpleDateFormat API.
     *
     * @param input The time to convert in seconds
     *
     * @return The time in minutes
     */
    @SuppressLint("SimpleDateFormat")
    private static String formatTime(int input) {
        String finalTime = null;
        Date temp;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                android.icu.text.SimpleDateFormat before = new android.icu.text.SimpleDateFormat("s");
                android.icu.text.SimpleDateFormat after = new android.icu.text.SimpleDateFormat("m");
                android.icu.text.SimpleDateFormat afterHour = new android.icu.text.SimpleDateFormat("K");
                temp = before.parse(Integer.toString(input));
                if (input > 3600) {
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
                temp = before.parse(Integer.toString(input));
                if (input < 600) {
                    finalTime = afterSub10.format(temp);
                } else if (input > 3600) {
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

    public static Times createTime(int stop, Cursor cursor, boolean is24Hours) {
        Times times = new Times();
        if (stop < 7) { //All stops before Cambridge road are to Cambridge Road
            if (cursor.getInt(13) != 0) { //Checks if the bus is to go to eastney
                if (cursor.getInt(14) == 0) {
                    times.setDestination("IMS Eastney");
                    times.setVia("via Cambridge Road");
                } else {
                    times.setDestination("Langstone Campus");
                    times.setVia("via Cambridge Road & IMS Eastney");
                }
            } else {
                if (cursor.getInt(1) != 0) {
                    times.setDestination("Langstone Campus");
                    times.setVia("via Cambridge Road");
                } else {
                    times.setDestination("Cambridge Road");
                    times.setVia("Direct service");
                }
            }
        } else {
            if (cursor.getInt(13) != 0) { //Checks if the bus is to go to eastney
                if (cursor.getInt(14) == 0) {
                    times.setDestination(cursor.getColumnName(13));
                    times.setVia("Direct service");
                } else {
                    times.setDestination("Langstone Campus");
                    times.setVia("via IMS Eastney");
                }
            } else {
                if (cursor.getInt(14) == 0) {//Checks if bus does not terminate at langstone
                    times.setDestination("Milton Park");
                    times.setVia("Direct service");
                } else {
                    times.setDestination("Langstone Campus");
                    times.setVia("Direct service");
                }
            }
        }
        times.setTime(formatTime(cursor.getString(stop), is24Hours));
        times.setId(cursor.getInt(0));
        return times;
    }

    /**
     * Takes the input from the list, after which it is run through a comparison to find the
     * time closest to the current clock time and shows the difference in minutes up to an hour. It
     * the calls a further method to format the string ready for display
     *
     * @param times The integer list to be parsed.
     *
     * @return the formatted time until the next bus in minutes or Integer.MAX_VALUE if no further buses
     */
    public static String getTimeToStop(List<Integer> times) {
        int currentTime = getCurrentTime();
        //int nearest = -1;
        int bestSoFar = Integer.MAX_VALUE;
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i) == currentTime) {
                bestSoFar = 0;
            } else {
                int d = Math.abs(currentTime - times.get(i));
                if (d < bestSoFar && currentTime <= times.get(i)) {
                    bestSoFar = d;
                    //nearest = times.get(i); In case we ever need to track the bus in question.
                }
            }
        }
        if (bestSoFar != Integer.MAX_VALUE) {
            return formatTime(bestSoFar);
        } else {
            return String.valueOf(bestSoFar);
        }
    }

    public static String getBestBus(List<Integer> times, boolean is24Hours) {
        int currentTime = getCurrentTime();
        int bestSoFar = Integer.MAX_VALUE;
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i) == currentTime) {
                bestSoFar = i;
            } else {
                int d = Math.abs(currentTime - times.get(i));
                if (d < bestSoFar && currentTime <= times.get(i)) {
                    bestSoFar = i;
                }
            }
        }
        if (bestSoFar == Integer.MAX_VALUE){
            return String.valueOf(bestSoFar);
        } else {
            return String.valueOf(formatTime(String.valueOf(times.get(bestSoFar)), is24Hours));
        }
    }

    private static int getCurrentTime() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        return ((hour * 60) + minute) * 60;
    }
}

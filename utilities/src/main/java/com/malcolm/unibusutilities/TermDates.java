package com.malcolm.unibusutilities;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple class that can be used to check the holiday dates
 */

public final class TermDates {
    private static final String EASTER2017START = "31-03-2017";
    private static final String EASTER2017END = "24-04-2017";
    private static final String SUMMER2017START = "02-06-2017";
    private static final String SUMMER2017END = "25-09-2017";
    private static final String CHRISTMAS2017START = "15-12-2017";
    private static final String CHRISTMAS2017END = "08-01-2018";
    private static final String EASTER2018START = "30-03-2018";
    private static final String EASTER2018END = "23-04-2018";


    public static boolean isItAHoliday(){
        Date today = Calendar.getInstance().getTime();
        List<Date> easter17 = makeDateArray(EASTER2017START, EASTER2017END);
        List<Date> summer17 = makeDateArray(SUMMER2017START, SUMMER2017END);
        List<Date> christmas17 = makeDateArray(CHRISTMAS2017START, CHRISTMAS2017END);
        List<Date> easter18 = makeDateArray(EASTER2018START, EASTER2018END);
        if (today.after(easter17.get(0)) && today.before(easter17.get(easter17.size() - 1))) {
            return true;
        } else if (today.after(summer17.get(0)) && today.before(summer17.get(summer17.size() - 1))) {
            return true;
        } else if (today.after(christmas17.get(0)) && today.before(christmas17.get(christmas17.size() - 1))) {
            return true;
        } else
            return today.after(easter18.get(0)) && today.before(easter18.get(easter18.size() - 1));
    }

    @SuppressLint("SwitchIntDef")
    public static boolean isItTheWeekend(){
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (day){
            case Calendar.SATURDAY:
                return true;
            case Calendar.SUNDAY:
                return true;
            default:
                return false;
        }

    }

    private static List<Date> makeDateArray(String start, String end) {
        ArrayList<Date> dates = new ArrayList<>();
        @SuppressLint("SimpleDateFormat") DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(start);
            date2 = df1.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
            cal1.clear(Calendar.HOUR);
            cal1.clear(Calendar.HOUR_OF_DAY);
            cal1.clear(Calendar.MINUTE);
            cal1.clear(Calendar.SECOND);
            cal1.clear(Calendar.MILLISECOND);
        }
        return dates;
    }

}

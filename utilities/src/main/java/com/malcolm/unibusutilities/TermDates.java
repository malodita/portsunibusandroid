package com.malcolm.unibusutilities;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

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
    private static final DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
    //Uni dates
    private static final String EASTER2017START = "31-03-2017";
    private static final String EASTER2017END = "24-04-2017";
    private static final String SUMMER2017START = "02-06-2017";
    private static final String SUMMER2017END = "25-09-2017";
    private static final String CHRISTMAS2017START = "15-12-2017";
    private static final String CHRISTMAS2017END = "08-01-2018";
    private static final String EASTER2018START = "30-03-2018";
    private static final String EASTER2018END = "23-04-2018";
    private static final String SUMMER2018START = "01-06-2018";
    private static final String SUMMER2018END = "14-09-2018";//Provisional

    //Bank holidays
    private static final String EASTERMONDAY2017 = "17-04-2017";
    private static final String GOODFRIDAY2017 = "14-04-2017";
    private static final String EARLYMAY2017 = "01-05-2017";
    private static final String SPRING2017 = "29-05-2017";
    private static final String SUMMER2017 = "28-08-2017";
    private static final String CHRISTMASDAY2017 = "25-12-2017";
    private static final String BOXINGDAY2017 = "26-12-2017";
    private static final String NEWYEARS2018 = "01-01-2018";
    private static final String GOODFRIDAY2018 = "30-03-2018";
    private static final String EASTERMONDAY2018 = "02-04-2018";
    private static final String EARLYMAY2018 = "07-05-2018";
    private static final String SPRING2018 = "28-05-2018";
    private static final String SUMMER2018 = "27-08-2018";
    private static final String CHRISTMASDAY2018 = "25-12-2018";
    private static final String BOXINGDAY2018 = "26-12-2018";

    private static final String[] BANKHOLIDAYS = new String[]{
            GOODFRIDAY2017, EASTERMONDAY2017, EARLYMAY2017, SPRING2017, SUMMER2017, CHRISTMASDAY2017,
            BOXINGDAY2017, NEWYEARS2018, GOODFRIDAY2018, EASTERMONDAY2018, EARLYMAY2018, SPRING2018,
            SUMMER2018, CHRISTMASDAY2018, BOXINGDAY2018};


    public static boolean isHoliday() {
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
    public static boolean isWeekend() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SATURDAY:
                return true;
            case Calendar.SUNDAY:
                return true;
            default:
                return false;
        }
    }

    public static boolean isWeekendInHoliday(){
        return isWeekend() && isHoliday();
    }

    public static boolean isBankHoliday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        try {
            for (String BANKHOLIDAY : BANKHOLIDAYS) {
                Date holiday = (df1.parse(BANKHOLIDAY));
                if (today.compareTo(holiday) == 0) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @NonNull
    public static String getTimetableName(){
        if (isWeekendInHoliday() || isHoliday()){
            return "Out of Term Timetable";
        } else if (isWeekend()){
            return "Weekend Timetable";
        } else if (isBankHoliday()){
            if (isHoliday()){
                return "Out of Term Timetable";
            } else {
                return "Term Timetable";
            }
        } else {
            return "Term Timetable";
        }
    }

    private static List<Date> makeDateArray(String start, String end) {
        ArrayList<Date> dates = new ArrayList<>();
        @SuppressLint("SimpleDateFormat")

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
            cal1.clear(Calendar.HOUR);
            cal1.clear(Calendar.HOUR_OF_DAY);
            cal1.clear(Calendar.MINUTE);
            cal1.clear(Calendar.SECOND);
            cal1.clear(Calendar.MILLISECOND);
            cal1.add(Calendar.DATE, 1);
            dates.add(cal1.getTime());
        }
        return dates;
    }

}

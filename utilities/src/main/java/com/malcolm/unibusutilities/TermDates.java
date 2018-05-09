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
    //Uni dates, when making bounds use the first & last day of the holiday
    //i.e. Saturday after & Sunday before
    private static final String EASTER2018START = "31-03-2018";
    private static final String EASTER2018END = "22-04-2018";
    private static final String SUMMER2018START = "02-06-2018";
    private static final String SUMMER2018END = "16-09-2018";
    private static final String CHRISTMAS2018START = "15-12-2018";
    private static final String CHRISTMAS2018END = "06-01-2019";
    private static final String EASTER2019START = "06-04-2019";
    private static final String EASTER2019END = "28-04-2019";
    private static final String SUMMER2019START = "08-06-2019";
    private static final String SUMMER2019END = "15-09-2019"; //Provisional

    //Bank holidays
    private static final String EARLYMAY2018 = "07-05-2018";
    private static final String SPRING2018 = "28-05-2018";
    private static final String SUMMER2018 = "27-08-2018";
    private static final String CHRISTMASDAY2018 = "25-12-2018";
    private static final String BOXINGDAY2018 = "26-12-2018";
    private static final String NYD2019 = "01-01-2019";
    private static final String GF2019 = "19-04-2019";
    private static final String EM2019 = "22-04-2019";
    private static final String MAY2019 = "06-05-2019";
    private static final String SPRING2019 = "27-05-2019";
    private static final String SUMMER19 = "26-08-2019";
    private static final String XMAS19 = "25-12-2019";
    private static final String BOXING19 = "26-12-2019";

    private static final String[] BANKHOLIDAYS = new String[]{EARLYMAY2018, SPRING2018,
            SUMMER2018, CHRISTMASDAY2018, BOXINGDAY2018};


    public static boolean isHoliday() {
        Date today = Calendar.getInstance().getTime();
        List<Date> easter18 = makeDateArray(EASTER2018START, EASTER2018END);
        List<Date> summer18 = makeDateArray(SUMMER2018START, SUMMER2018END);
        List<Date> christmas18 = makeDateArray(CHRISTMAS2018START, CHRISTMAS2018END);
        List<Date> easter19 = makeDateArray(EASTER2019START, EASTER2019END);
        if (today.after(easter19.get(0)) && today.before(easter19.get(easter19.size() - 1))) {
            return true;
        } else if (today.after(summer18.get(0)) && today.before(summer18.get(summer18.size() - 1))) {
            return true;
        } else if (today.after(christmas18.get(0)) && today.before(christmas18.get(christmas18.size() - 1))) {
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

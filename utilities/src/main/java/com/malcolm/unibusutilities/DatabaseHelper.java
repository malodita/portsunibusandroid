package com.malcolm.unibusutilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Contains database access logic and exposes some helper methods for use by other modules
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_WEEKEND_NAME = "timetable_weekend.sqlite";
    private static final String DB_WEEKDAY_NAME = "timetable_weekday_normal.sqlite";
    private static final String DB_HOLIDAY_NAME = "timetable_holiday.sqlite";
    private static final String DB_WEEKDAY_WED_NAME = "timetable_weekday_wednesday.sqlite";
    //Term dates
    private static final String EASTER2017START = "31-03-2017";
    private static final String EASTER2017END = "24-04-2017";
    private static final String SUMMER2017START = "02-06-2017";
    private static final String SUMMER2017END = "25-09-2017";
    private static final String CHRISTMAS2017START = "15-12-2017";
    private static final String CHRISTMAS2017END = "08-01-2018";
    private static final String EASTER2018START = "30-03-2018";
    private static final String EASTER2018END = "23-04-2018";
    private static String DB_NAME;
    private static String DB_PATH;
    private static DatabaseHelper instance;
    private final Context context;
    private SQLiteDatabase myDatabase;

    private DatabaseHelper(Context context) {
        super(context, null, null, 1);
        DB_PATH = ContextCompat.getDataDir(context) + "/databases/";
        this.context = context;
        checkAndCopyDatabase();
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void checkAndCopyDatabase() {
        DB_NAME = getDBName();
        boolean dbExist = checkDatabase();
        if (dbExist) {
            return;
        }
        getWritableDatabase();
        try {
            copyDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error copying database");

        }
    }

    @SuppressLint("SwitchIntDef")
    private String getDBName() {
        Calendar calendar = Calendar.getInstance();
        String name;
        if (TermDates.isItAHoliday()) {
            name = DB_HOLIDAY_NAME;
        } else {
            if (TermDates.isItTheWeekend()) {
                name = DB_WEEKEND_NAME;
            } else {
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                if (day == Calendar.WEDNESDAY) {
                    name = DB_WEEKDAY_WED_NAME;
                } else {
                    name = DB_WEEKDAY_NAME;
                }
            }
        }
        return name;
    }

    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            Log.w(TAG, "Database error, it may not have been created");
            e.printStackTrace();
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDatabase() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream myInput = null;
                try {
                    myInput = context.getAssets().open(DB_NAME);
                    String outFileName = DB_PATH + DB_NAME;
                    OutputStream myOutput = new FileOutputStream(outFileName);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = myInput.read(buffer)) > 0) {
                        myOutput.write(buffer, 0, length);
                    }
                    myOutput.flush();
                    myOutput.close();
                    myInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private List<Date> makeDateArray(String start, String end) {
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

    private boolean openDatabase() {
        String myPath = DB_PATH + DB_NAME;
        try {
            myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error opening database");
            e.printStackTrace();

        }
        return myDatabase != null;
    }

    public synchronized void close() {
        if (myDatabase != null) {
            myDatabase.close();
        }
        super.close();
    }

    private Cursor queryData(String query) {
        return myDatabase.rawQuery(query, null);
    }

    /**
     * This method parses through the timetable at the selected home stop in order to count down the
     * time until the next bus arrives at the home stop. After opening the database using the helper
     * class, it then takes the value for this in a switch statement and depending on the value will
     * set the rawQuery for the database search to give all the times in the column for the result.
     *
     * @param stop The string used to search the database
     *
     * @return An arraylist of Integers representing the times the bus will stop in seconds
     */
    public ArrayList<Integer> getTimesForArray(String stop) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        Cursor cursor = null;
        checkAndCopyDatabase();
        boolean databaseOpened = openDatabase();
        if (databaseOpened) {
            try {
                cursor = queryData("select " + stop + " from Timetable");
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            int targetTime = cursor.getInt(0);
                            if (targetTime != 0) {
                                arrayList.add(targetTime);
                            }
                        } while (cursor.moveToNext());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, "Error at loadDatabase: try block B");
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                close();
            }
        }
        return arrayList;
    }

    /**
     * This method parses through the timetable at the selected home stop in order to count down the
     * time until the next bus arrives at the home stop. After opening the database using the helper
     * class, it then takes the value for this in a switch statement and depending on the value will
     * set the rawQuery for the database search to give all the times in the column for the result.
     * <p>
     * This overloaded method instead uses the integer for the stopId which is then searched
     * </P>
     *
     * @param stopId The integer representation of the bus stop
     *
     * @return An arraylist of Integers representing the times the bus will stop in seconds
     */
    public ArrayList<Integer> getTimesForArray(int stopId) {
        String stop = getStopToShow(stopId);
        ArrayList<Integer> arrayList = new ArrayList<>();
        Cursor cursor = null;
        checkAndCopyDatabase();
        boolean databaseOpened = openDatabase();
        if (databaseOpened) {
            try {
                cursor = queryData("select " + stop + " from Timetable");
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            int targetTime = cursor.getInt(0);
                            arrayList.add(targetTime);
                        } while (cursor.moveToNext());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, "Error at loadDatabase: try block B");
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                close();
            }
        }
        return arrayList;
    }

    /**
     * Obtains the stop for {@link DatabaseHelper} to search for displaying the home stop
     *
     * @param homeStopId The id of the stop retrieved
     *
     * @return A formatted string for the search to use
     */
    private String getStopToShow(int homeStopId) {
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
     * This method opens and obtains the entire database and parses it to find the stops that mathch
     * the correct stop to show. It then formats the times and adds to the arrayList (after clearing
     * the list beforehand from previous run results. I could split the method for a v2 but it
     * doesn't seem necessary due to the lack of the need for independent methods like in
     * TopFragment. the fragment refreshes. <p>In TopFragment, only the data from one stop is used
     * to display the departure time, if a new home stop is selected. However since the fragment
     * does not refresh and this method is linked to a selection event for a spinner to display all
     * the stop times for a location </p> Nullable since if no stop is selected, the entire view
     * should be replaced with a prompt to select the stop. Also no asyncTask/Runnable has been used
     * for this since it is not a huge database task database contains no more than 40 rows
     *
     * @param stop The stop to display, if zero then it skips getting the array
     *
     * @return An arrayList of times ready for the adapter to use or null if no default stop is
     * selected
     */
    @Nullable
    public ArrayList<Times> getTimesArray(int stop) {
        ArrayList<Times> arrayList = new ArrayList<>();
        Cursor cursor = null;
        checkAndCopyDatabase();
        boolean databaseOpened = openDatabase();
        if (databaseOpened) {
            try {
                cursor = queryData("select * from Timetable order by id");
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            if (cursor.getString(stop) != null) {
                                Times times = new Times();
                                times.setDestination(cursor.getColumnName(stop));
                                times.setTime(formatTime(cursor.getString(stop)));
                                times.setId(cursor.getInt(0));
                                if (cursor.getInt(0) == 47 && stop >= 7) {
                                    arrayList.add(times);
                                } else {
                                    arrayList.add(times);
                                }
                            }
                        } while (cursor.moveToNext());
                    }
                } else {
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, "Error at loadDatabase: try block B");
            } catch (ParseException e) {
                if (e.getMessage().equals("Unparseable date: \"\"")) {
                    System.out.print("Out of dates");
                } else {
                    e.printStackTrace();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                close();
            }
        }
        return arrayList;
    }

    public ArrayList<Times> getDataForList(int busId) {
        Log.d(TAG, "getDataForList: " + busId);
        ArrayList<Times> array = new ArrayList<>();
        Cursor cursor = null;
        checkAndCopyDatabase();
        boolean databaseOpened = openDatabase();
        if (databaseOpened) {
            try {
                cursor = queryData("select * from Timetable where id=" + busId + " order by id");
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        for (int i = 1; i <= 14; i++) {
                            Log.d(TAG, "getDataForList: " + cursor.getString(i));
                            if (cursor.getString(i) != null) {
                                Times times = new Times();
                                times.setDestination(cursor.getColumnName(i));
                                times.setTime(formatTime(cursor.getString(i)));
                                array.add(times);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, "Error at loadDatabase: try block B");
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                close();
            }
        }
        return array;
    }

    private String formatTime(String input) throws ParseException {
        String output;
        if (Build.VERSION.SDK_INT >= 24) {
            android.icu.text.SimpleDateFormat before = new android.icu.text.SimpleDateFormat("s");
            Date date = before.parse(input);
            android.icu.text.SimpleDateFormat after = new android.icu.text.SimpleDateFormat("HH:mm");
            output = (after.format(date));
        } else {
            java.text.SimpleDateFormat before = new java.text.SimpleDateFormat("s");
            Date date = before.parse(input);
            java.text.SimpleDateFormat after = new java.text.SimpleDateFormat("HH:mm");
            after.format(date);
            output = (after.format(date));
        }
        return output;
    }
}


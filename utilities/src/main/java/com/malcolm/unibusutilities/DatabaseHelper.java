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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Contains database access logic and exposes some helper methods for use by other modules
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_WEEKEND_NAME = "timetable_weekend.sqlite";
    private static final String DB_WEEKDAY_NAME = "timetable_weekday_normal.sqlite";
    private static final String DB_HOLIDAY_NAME = "timetable_holiday.sqlite";
    private static final String DB_WEEKDAY_WED_NAME = "timetable_weekday_wednesday.sqlite";

    private static final String TABLE_NAME = "Timetable";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EASTNEY = "IMS Eastney (Departures)";
    private static final String COLUMN_LANG_DEPT = "Langstone Campus (for Departures only)";
    private static final String COLUMN_LOCKSWAY = "Locksway Road (for Milton Park)";
/*    private static final String COLUMN_
    private static final String COLUMN_
    private static final String COLUMN_
    private static final String COLUMN_
    private static final String COLUMN_
    private static final String COLUMN_
    private static final String COLUMN_
    private static final String COLUMN_
    private static final String COLUMN_
    private static final String COLUMN_
    private static final String COLUMN_*/

/*    private static final String CREATE_DB = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " Integer DEFAULT (null), " + COLUMN_EASTNEY + " INTEGER, " +
            COLUMN_LANG_DEPT + " INTEGER, " + COLUMN_LOCKSWAY + " INTEGER, "
            `Goldsmith Avenue (adj Lidi)`	Integer,
            `Goldsmith Avenue (opp Fratton Station)`	Integer,
            `Winston Churchill Avenue (adj Ibis Hotel)`	Integer,
            `Cambridge Road (adj Student Union for Arrivals only)`	Integer,
            `Cambridge Road (adj Nuffield Building)`	Integer,
            `Winston Churchill Avenue (adj Law Courts)`	Integer,
            `Goldsmith Avenue (adj Fratton Station)`	Integer,
            `Goldsmith Avenue (opp Lidl)`	Integer,
            `Goldsmith Avenue (adj Milton Park)`	Integer,
            `IMS Eastney`	INTEGER,
            `Langstone Campus (for Arrivals only)`	Integer;*/
    private static String DB_NAME;
    private static String DB_PATH;
    @SuppressLint("StaticFieldLeak")
    private static DatabaseHelper instance;
    private final Context context;
    private SQLiteDatabase myDatabase;

    private DatabaseHelper(Context context, boolean isWearable) {
        super(context, null, null, 4);
        if (isWearable) {
            DB_PATH = context.getFilesDir().getPath() + "/";
        } else {
            DB_PATH = ContextCompat.getDataDir(context) + "/databases/";
        }
        this.context = context;
        checkAndCopyDatabase();
    }

    /**
     * Obtains an instance suitable for phones
     *
     * @param context The calling context
     *
     * @return A database instance for phones
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext(), false);
        }
        return instance;
    }

    /**
     * Obtains an instance suitable for wearable devices.
     *
     * @param context The calling context
     *
     * @return A database instance for wearable devices
     */
    public static synchronized DatabaseHelper getWearableInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext(), true);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    private void checkAndCopyDatabase() {
        DB_NAME = getDBName();
        boolean dbExist = checkDatabase();
        if (dbExist) {
            return;
        }
        this.getReadableDatabase();
        try {
            copyDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error copying database");

        }
    }

    private String getDBName() {
        Calendar calendar = Calendar.getInstance();
        String name;
        if (TermDates.isHoliday() || TermDates.isWeekendInHoliday()) {
            name = DB_HOLIDAY_NAME;
        } else {
            if (TermDates.isWeekend()) {
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
                InputStream myInput;
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
     * <P> This method parses through the timetable at the selected stop in order to count down the
     * time until the next bus arrives at the home stop. After opening the database using the helper
     * class, it then takes the value for this in a switch statement and depending on the value will
     * set the query for the database search to give all the times in the column for the result.
     * </P> <p> Unlike the alternative method {@link #getTimesForArray(int)}, this will be able to
     * search any stop as long as the correct string has been passed into the method (This string
     * must be in brackets) </p>
     *
     * @param busStop The string used to search the database
     *
     * @return An arraylist of Integers representing the times the bus will stop in seconds
     */
    public ArrayList<Integer> getTimesForArray(String busStop) {
        String stop = "[" + busStop + "]";
        ArrayList<Integer> arrayList = new ArrayList<>();
        Cursor cursor = null;
        if (openDatabase()) {
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
     * <p>This method parses through the timetable at the selected stopId in order to count down the
     * time until the next bus arrives at the home stop. After opening the database using the helper
     * class, it then takes the value for this in a switch statement and depending on the value will
     * set the query for the database search to give all the times in the column for the result.
     * </p> <p> This method only works for obtaining the array for a home stop as the id check will
     * only obtain one of these stop. Use {@link #getTimesForArray(String) the alternative method}
     * if you wish to search any stop. </p>
     *
     * @param stopId The integer representation of the bus stop
     *
     * @return An arraylist of Integers representing the times the bus will stop in seconds
     */
    public ArrayList<Integer> getTimesForArray(int stopId) {
        String stop = getStopToShow(stopId);
        ArrayList<Integer> arrayList = new ArrayList<>();
        Cursor cursor = null;
        if (openDatabase()) {
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
     * This method opens and obtains the entire database and parses it to find the stops that match
     * the correct stop. It then formats the times and adds to the arrayList (after clearing
     * the list beforehand from previous run results. I could split the method for a v2 but it
     * doesn't seem necessary due to the lack of the need for independent methods like in
     * TopFragment. the fragment refreshes. <p>In TopFragment, only the data from one stop is used
     * to display the departure time, if a new home stop is selected. However since the fragment
     * does not refresh and this method is linked to a selection event for a spinner to display all
     * the stop times for a location </p> Nullable since if no stop is selected, the entire view
     * should be replaced with a prompt to select the stop. Also no asyncTask/Runnable has been used
     * for this since it is not a huge database task database contains no more than 50 rows
     *
     * @param stop      The stop to display, if zero then it skips getting the array
     * @param is24Hours If the 12 hour or 24 hour format should be used
     *
     * @return An arrayList of times ready for the adapter to use or null if no default stop is
     * selected //Todo: Change javadoc
     */
    @Nullable
    public ArrayList<Times> getTimesArray(int stop, boolean is24Hours) {
        ArrayList<Times> arrayList = new ArrayList<>();
        Cursor cursor = null;
        if (openDatabase()) {
            try {
                cursor = queryData("select * from Timetable order by id");
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            if (cursor.getString(stop) != null) {
                                Times times = createTime(stop, cursor, is24Hours);
                                if (cursor.getInt(0) == 48 && stop >= 7) {
                                    //Checks to see if last row (Which should only be seen for return bus). Hardcoded value.
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
            }  finally {
                if (cursor != null) {
                    cursor.close();
                }
                close();
            }
        }
        return arrayList;
    }

    public ArrayList<Times> getDataForList(int busId, boolean is24Hours) {
        ArrayList<Times> array = new ArrayList<>();
        Cursor cursor = null;
        if (openDatabase()) {
            try {
                cursor = queryData("select * from Timetable where id=" + busId);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        for (int i = 1; i < cursor.getColumnCount(); i++) {
                            if (cursor.getString(i) != null) {
                                Times times = new Times();
                                if (cursor.getColumnName(i).equals("Langstone Campus (for Departures only)")){
                                    times.setDestination("Langstone Campus (Departures)");
                                } else if (cursor.getColumnName(i).equals("Langstone Campus (for Arrivals only)")){
                                    times.setDestination("Langstone Campus");
                                } else if (cursor.getColumnName(i).equals("Cambridge Road (adj Student Union for Arrivals only)")){
                                    times.setDestination("Cambridge Road (Student Union)");
                                } else {
                                    times.setDestination(cursor.getColumnName(i));
                                }
                                times.setTime(formatTime(cursor.getString(i), is24Hours));
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

    /**
     * Obtains the stop {@link #getTimesForArray(int) to search the database here}
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

    private Times createTime(int stop, Cursor cursor, boolean is24Hours){
        Times times = new Times();
        if (stop < 7) { //All stops before Cambridge road are to Cambridge Road
            if (cursor.getInt(13) != 0) { //Checks if the bus is to go to eastney
                if (cursor.getInt(14) == 0) {
                    times.setDestination("IMS Eastney via Cambridge Road");
                } else {
                    times.setDestination("Langstone via Cambridge Road & IMS Eastney");
                }
            } else {
                if (cursor.getInt(1) != 0){
                    times.setDestination("Langstone via Cambridge Road");
                } else {
                    times.setDestination("Cambridge Road");
                }
            }
        } else {
            if (cursor.getInt(13) != 0) { //Checks if the bus is to go to eastney
                times.setDestination(cursor.getColumnName(13));
            } else {
                if (cursor.getInt(14) == 0) {//Checks if bus does not terminate at langstone
                    times.setDestination("Milton Park");
                } else {
                    times.setDestination("Langstone Campus");
                }
            }
        }
        try {
            times.setTime(formatTime(cursor.getString(stop), is24Hours));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        times.setId(cursor.getInt(0));
        return times;
    }

    private String formatTime(String input, boolean is24Hours) throws ParseException {
        StringBuilder output = new StringBuilder();
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
        return output.toString();
    }
}


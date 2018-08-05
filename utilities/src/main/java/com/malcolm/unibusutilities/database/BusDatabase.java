package com.malcolm.unibusutilities.database;

import android.content.Context;
import android.util.Log;

import com.malcolm.unibusutilities.entity.Bus;
import com.malcolm.unibusutilities.helper.DatabasePopulater;
import com.malcolm.unibusutilities.helper.TermDateUtils;
import com.malcolm.unibusutilities.utils.AppExecutors;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Bus.class, exportSchema = false, version = 6)
public abstract class BusDatabase extends RoomDatabase {

    private static final String TAG = "BusDatabase";
    private static final String DB_WEEKEND_NAME = "timetable_weekend.sqlite";
    private static final String DB_WEEKDAY_NAME = "timetable_weekday_normal.sqlite";
    private static final String DB_HOLIDAY_NAME = "timetable_holiday.sqlite";
    private static final String DB_WEEKDAY_WED_NAME = "timetable_weekday_wednesday.sqlite";
    private static volatile BusDatabase instance = null;
    private static String currentName;
    public abstract Bus.BusDao busDao();
    private static final AppExecutors appExecutors = AppExecutors.getInstance();

    // FIXME: 04/07/2018 dynamic database loading needed
    public synchronized static BusDatabase getInstance(Context context){
        if (instance == null){
            currentName = getDBName();
            instance = createRoomDatabase(context.getApplicationContext(), currentName);
        } else {
            if (!currentName.equals(getDBName()) && instance.isOpen()){
                instance.close();
                currentName = getDBName();
                instance = createRoomDatabase(context.getApplicationContext(), currentName);
            }
        }
        return instance;
    }

    private static BusDatabase createRoomDatabase(Context context, String name){
        androidx.room.RoomDatabase.Builder<BusDatabase> builder =
                Room.databaseBuilder(context.getApplicationContext()
                        , BusDatabase.class, name)
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                appExecutors.getBackgroundThread().execute(() -> populateDatabase(db, name));
                                super.onCreate(db);
                            }

                            @Override
                            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                Log.d(TAG, "onOpen: " + db.getPath());
                                super.onOpen(db);
                            }
                        });
        switch (name) {
            case DB_HOLIDAY_NAME:
                builder.addMigrations(new Migration(1, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        DatabasePopulater.migrateHolidayTimetable(database);
                    }
                });
                builder.addMigrations(new Migration(4, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        DatabasePopulater.migrateHolidayTimetable(database);
                    }
                });
                break;
            case DB_WEEKDAY_NAME:
                builder.addMigrations(new Migration(1, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        DatabasePopulater.migrateNormalTimetable(database);
                    }
                });
                builder.addMigrations(new Migration(4, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        DatabasePopulater.migrateNormalTimetable(database);
                    }
                });
                break;
            case DB_WEEKDAY_WED_NAME:
                builder.addMigrations(new Migration(1, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        DatabasePopulater.migrateWednesdayTimetable(database);
                    }
                });
                builder.addMigrations(new Migration(4, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        DatabasePopulater.migrateWednesdayTimetable(database);
                    }
                });
                break;
            case DB_WEEKEND_NAME:
                builder.addMigrations(new Migration(1, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        DatabasePopulater.migrateWeekendTimetable(database);
                    }
                });
                builder.addMigrations(new Migration(4, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        DatabasePopulater.migrateWeekendTimetable(database);
                    }
                });
                break;
        }
        return builder.build();
    }

    private static void populateDatabase(SupportSQLiteDatabase database, String name){
        switch (name) {
            case DB_HOLIDAY_NAME:
                DatabasePopulater.populateHolidayTimetable(database);
                break;
            case DB_WEEKDAY_NAME:
                DatabasePopulater.populateNormalTimetable(database);
                break;
            case DB_WEEKDAY_WED_NAME:
                DatabasePopulater.populateWednesdayTimetable(database);
                break;
            case DB_WEEKEND_NAME:
                DatabasePopulater.populateWeekendTimetable(database);
                break;
        }
        database.setVersion(1);
    }

    private static String getDBName() {
        Calendar calendar = Calendar.getInstance();
        String name;
        if (TermDateUtils.isHoliday() || TermDateUtils.isWeekendInHoliday()) {
            name = DB_HOLIDAY_NAME;
        } else {
            if (TermDateUtils.isWeekend()) {
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

}

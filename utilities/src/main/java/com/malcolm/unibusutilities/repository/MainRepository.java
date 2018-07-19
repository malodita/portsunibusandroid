package com.malcolm.unibusutilities.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.database.Cursor;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.malcolm.unibusutilities.database.BusDatabase;
import com.malcolm.unibusutilities.entity.Bus;
import com.malcolm.unibusutilities.entity.Times;
import com.malcolm.unibusutilities.helper.DatabaseUtils;
import com.malcolm.unibusutilities.helper.TermDateUtils;
import com.malcolm.unibusutilities.model.StopAndTime;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainRepository {

    public static final int NOT_TIMETABLE = 0;
    public static final int TWELVE_HOUR = 12;
    public static final int TWENTYFOUR_HOUR = 24;


    private static final String TAG = "MainRepository";
    private static MainRepository instance;
    private final MutableLiveData<String> focusedCountdown = new MutableLiveData<>();
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private final MutableLiveData<StopAndTime> instantCountdown = new MutableLiveData<>();
    private final List<Bus> buses;
    private final LocationRepository locationRepository;
    private Bus.BusDao busDao;
    private TimeRunnable focusedStop;
    private InstantRunnable instantStop;
    private MediatorLiveData<Location> locationLiveData;
    private android.arch.lifecycle.Observer<Location> locationObserver;


    private MainRepository(final BusDatabase database, Application application) {
        busDao = database.busDao();
        buses = busDao.getAll();
        locationRepository = LocationRepository.getInstance(application);
    }

    public static synchronized MainRepository getInstance(Application application, BusDatabase database) {
        if (instance == null) {
            instance = new MainRepository(database, application);
        }
        return instance;
    }

    public LiveData<StopAndTime> getInstantCountdown() {
        return instantCountdown;
    }

    public void fetchListForInstantCountdown() {
        if (locationLiveData == null) {
            locationLiveData = new MediatorLiveData<>();
            locationObserver = location -> {
                if (location != null) {
                    if (!location.getProvider().equals("null")) {
                        if (location.getProvider().equals("IMS Eastney") && TermDateUtils.isHoliday() || TermDateUtils.isWeekend()) {
                            instantCountdown.setValue(null);
                            return;
                        }
                        List<Integer> list = new ArrayList<>();
                        for (int i = 0; i < buses.size(); i++) {
                            Integer integer = (buses.get(i).get(location.getProvider()));
                            if (integer != null) {
                                list.add(integer);
                            }
                        }
                        if (instantStop == null) {
                            instantStop = new InstantRunnable(MainRepository.this, list, location.getProvider());
                            handler.post(instantStop);
                        } else {
                            handler.removeCallbacks(instantStop);
                            instantStop.setList(list, location.getProvider());
                            handler.post(instantStop);
                        }
                        instantStop.setRunning(true);
                    } else {
                        instantCountdown.setValue(null);
                        if (instantStop != null && instantStop.isRunning()) {
                            handler.removeCallbacks(instantStop);
                            instantStop.setRunning(false);
                        }
                    }
                }
            };
            locationRepository.getClosestStop().observeForever(locationObserver);
        }
    }

    public void fetchListForFocusedCountdown(int stop, int hours) {
        if (stop < 0) {
            focusedCountdown.setValue("-1");
            if (focusedStop != null) {
                handler.removeCallbacks(focusedStop);
            }
            return;
        }
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < buses.size(); i++) {
            Integer integer = (buses.get(i).get(stop));
            if (integer != null) {
                list.add(integer);
            }
        }
        if (focusedStop == null) {
            focusedStop = new TimeRunnable(this, list, hours);
            handler.post(focusedStop);
        } else {
            focusedStop.setList(list, hours);
            handler.removeCallbacks(focusedStop);
            handler.post(focusedStop);
        }
    }

    public LiveData<String> getCountdown() {
        return focusedCountdown;
    }

    public void stopObservingInstantStop() {
        if (locationObserver != null) {
            locationRepository.removeObserver();
            locationRepository.getClosestStop().removeObserver(locationObserver);
        }
        handler.removeCallbacks(instantStop);
    }

    public void stopObservingFocusedStop() {
        if (focusedStop != null) {
            handler.removeCallbacks(focusedStop);
        }
    }

    /**
     * Uses a cursor to retrieve a list of Times objects for each stop that one particular bus will
     * alight at on one journey
     *
     * @param busId     Id of the bus
     * @param is24Hours If the reported time should be in 12 or 24 hour format.
     *
     * @return A list of times for one bus
     */
    public List<Times> getDataForList(int busId, boolean is24Hours) {
        Cursor cursor = busDao.getBusDetailCursor(busId);
        List<Times> array = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                for (int i = 1; i < cursor.getColumnCount(); i++) {
                    if (cursor.getString(i) != null) {
                        Times times = new Times();
                        switch (cursor.getColumnName(i)) {
                            case "Langstone Campus (for Departures only)":
                                times.setDestination("Langstone Campus");
                                break;
                            case "Langstone Campus (for Arrivals only)":
                                times.setDestination("Langstone Campus");
                                break;
                            case "Cambridge Road (adj Student Union for Arrivals only)":
                                times.setDestination("Cambridge Road (Student Union)");
                                break;
                            default:
                                times.setDestination(cursor.getColumnName(i));
                                break;
                        }
                        times.setTime(DatabaseUtils.formatTime(cursor.getString(i), is24Hours));
                        array.add(times);
                    }
                }
            }
        }
        return array;
    }

    /**
     * Uses a cursor to retrieve a list of Times objects representing all the buses that will alight at a
     * particular stop for the day.
     *
     * @param stop      Id of the stop
     * @param is24Hours If the reported time should be in 12 or 24 hour format.
     *
     * @return A list of times for one stop
     */
    @SuppressWarnings("unchecked")
    public List<Times> getListOfTimesForStop(int stop, boolean is24Hours) {
        if (stop == -1){
            stop++;
        }
        ArrayList<Times> array = new ArrayList();
        Cursor cursor = busDao.getAllCursor();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(stop) != null) {
                        Times times = DatabaseUtils.createTime(stop, cursor, is24Hours);
                        if (cursor.getInt(0) == 48 && stop >= 7) {
                            //Checks to see if last row (Which should only be seen for return bus). Hardcoded value.
                            array.add(times);
                        } else {
                            array.add(times);
                        }
                    }
                } while (cursor.moveToNext());
            }
        } else {
            return null;
        }
        return array;
    }

    private final class TimeRunnable implements Runnable {

        private List<Integer> list;
        private WeakReference<MainRepository> parent;
        private int timeReport;

        TimeRunnable(MainRepository parent, List<Integer> list, int timeReport) {
            this.list = list;
            this.parent = new WeakReference<>(parent);
            this.timeReport = timeReport;
        }

        protected void setList(@NonNull List<Integer> list, int timeReport) {
            this.list = list;
            this.timeReport = timeReport;
            MainRepository repository = parent.get();
            String time;
            if (timeReport == NOT_TIMETABLE) {
                time = DatabaseUtils.getTimeToStop(list);
            } else {
                if (timeReport == TWELVE_HOUR){
                    time = DatabaseUtils.getBestBus(list, false);
                } else {
                    time = DatabaseUtils.getBestBus(list, true);
                }
            }
            repository.focusedCountdown.setValue(time);
        }

        @Override
        public void run() {
            if (list != null && list.size() > 0) {
                MainRepository repository = parent.get();
                String time;
                if (timeReport == NOT_TIMETABLE) {
                    time = DatabaseUtils.getTimeToStop(list);
                } else {
                    if (timeReport == TWELVE_HOUR){
                        time = DatabaseUtils.getBestBus(list, false);
                    } else {
                        time = DatabaseUtils.getBestBus(list, true);
                    }
                }
                handler.postDelayed(this, 1000);
                repository.focusedCountdown.setValue(time);
            }
        }

    }


    private final class InstantRunnable implements Runnable {

        private List<Integer> list;
        private WeakReference<MainRepository> parent;
        private String location;
        private StopAndTime stopAndTime = new StopAndTime();
        private boolean isRunning = false;


        InstantRunnable(MainRepository parent, List<Integer> list, String location) {
            this.list = list;
            this.parent = new WeakReference<>(parent);
            this.location = location;
        }

        void setList(@NonNull List<Integer> list, @NonNull String location) {
            this.location = location;
            this.list = list;
        }

        boolean isRunning() {
            return isRunning;
        }

        void setRunning(boolean running) {
            isRunning = running;
        }

        @Override
        public void run() {
            MainRepository repository = parent.get();
            stopAndTime.setTime(DatabaseUtils.getTimeToStop(list));
            stopAndTime.setStop(location);
            repository.instantCountdown.setValue(stopAndTime);
            handler.postDelayed(this, 8000);
        }
    }


}
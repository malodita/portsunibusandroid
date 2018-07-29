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

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainRepository {

    public static final int NOT_TIMETABLE = 0;
    public static final int TWELVE_HOUR = 12;
    public static final int TWENTYFOUR_HOUR = 24;


    private static final String TAG = "MainRepository";
    private static MainRepository instance;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final MutableLiveData<String> focusedCountdown = new MutableLiveData<>();
    private final MutableLiveData<StopAndTime> instantCountdown = new MutableLiveData<>();
    private final MutableLiveData<String> timetableCountdown = new MutableLiveData<>();
    private final MutableLiveData<List<Times>> liveBusList = new MutableLiveData<>();
    private final LocationRepository locationRepository;
    private List<Bus> buses;
    private Bus.BusDao busDao;
    private TimeRunnable focusedStop;
    private TimeRunnable timetableStop;
    private InstantRunnable instantStop;
    private MediatorLiveData<Location> locationLiveData;
    private android.arch.lifecycle.Observer<Location> locationObserver;


    private MainRepository(final BusDatabase database, Application application) {
        busDao = database.busDao();

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

    public LiveData<String> getCountdown() {
        return focusedCountdown;
    }

    public LiveData<String> getTimetableCountdown() {
        return timetableCountdown;
    }

    public void fetchListForInstantCountdown() {
        Single<List<Bus>> single = Single.fromCallable(() -> busDao.getAll());
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Bus>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Bus> buses) {
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
                        }
                        locationRepository.getClosestStop().observeForever(locationObserver);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void fetchListForTimetableCountdown(int stop, int hours) {
        Single<List<Bus>> single = Single.fromCallable(() -> busDao.getAll());
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Bus>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Bus> buses) {
                        if (stop < 0) {
                            timetableCountdown.setValue("-1");
                            if (timetableStop != null) {
                                handler.removeCallbacks(timetableStop);
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
                        if (timetableStop == null) {
                            timetableStop = new TimeRunnable(MainRepository.this, list, hours);
                            handler.post(timetableStop);
                        } else {
                            timetableStop.setList(list, hours);
                            handler.post(timetableStop);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    public void fetchListForFocusedCountdown(int stop, int hours) {
        //Single<List<Bus>> single = Single.just(busDao.getAll());
        Single<List<Bus>> single = Single.fromCallable(() -> busDao.getAll());
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Bus>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Bus> buses) {
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
                            focusedStop = new TimeRunnable(MainRepository.this, list, hours);
                            handler.post(focusedStop);
                        } else {
                            focusedStop.setList(list, hours);
                            handler.post(focusedStop);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

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

    public void stopObservingTimetableStop() {
        if (timetableStop != null) {
            handler.removeCallbacks(timetableStop);
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
        Single<Cursor> cursorSingle = Single.fromCallable(() -> busDao.getBusDetailCursor(busId));
        List<Times> array = new ArrayList<>();
        cursorSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Cursor>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onSuccess(Cursor listCursor) {
                        if (listCursor != null) {
                            if (listCursor.moveToFirst()) {
                                for (int i = 1; i < listCursor.getColumnCount(); i++) {
                                    if (listCursor.getString(i) != null) {
                                        Times times = new Times();
                                        switch (listCursor.getColumnName(i)) {
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
                                                times.setDestination(listCursor.getColumnName(i));
                                                break;
                                        }
                                        times.setTime(DatabaseUtils.formatTime(listCursor.getString(i), is24Hours));
                                        array.add(times);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
        return array;
        //Cursor listCursor = busDao.getBusDetailCursor(busId);
        //List<Times> array = new ArrayList<>();
/*        if (listCursor != null) {
            if (listCursor.moveToFirst()) {
                for (int i = 1; i < listCursor.getColumnCount(); i++) {
                    if (listCursor.getString(i) != null) {
                        Times times = new Times();
                        switch (listCursor.getColumnName(i)) {
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
                                times.setDestination(listCursor.getColumnName(i));
                                break;
                        }
                        times.setTime(DatabaseUtils.formatTime(listCursor.getString(i), is24Hours));
                        array.add(times);
                    }
                }
            }
        }
        return array;*/
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
    public LiveData<List<Times>> findListOfTimesForStop(int stop, boolean is24Hours) {
        if (stop == -1){
            stop++;
        }
        Single<Cursor> single = Single.fromCallable(() -> busDao.getAllCursor());
        int finalStop = stop;
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Cursor>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(Cursor stopCursor) {
                        ArrayList<Times> array = new ArrayList();
                        if (stopCursor != null) {
                            if (stopCursor.moveToFirst()) {
                                do {
                                    if (stopCursor.getString(finalStop) != null) {
                                        Times times = DatabaseUtils.createTime(finalStop, stopCursor, is24Hours);
                                        if (stopCursor.getInt(0) == 48 && finalStop >= 7) {
                                            //Checks to see if last row (Which should only be seen for return bus). Hardcoded value.
                                            array.add(times);
                                        } else {
                                            array.add(times);
                                        }
                                    }
                                } while (stopCursor.moveToNext());
                            }
                            liveBusList.postValue(array);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
        return liveBusList;
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

        void setList(@NonNull List<Integer> list, int timeReport) {
            this.list = list;
            this.timeReport = timeReport;
            MainRepository repository = parent.get();
            String time;
            if (timeReport == NOT_TIMETABLE) {
                time = DatabaseUtils.getTimeToStop(list);
                repository.focusedCountdown.setValue(time);
            } else {
                if (timeReport == TWELVE_HOUR){
                    time = DatabaseUtils.getBestBus(list, false);
                } else {
                    time = DatabaseUtils.getBestBus(list, true);
                }
                repository.timetableCountdown.setValue(time);
            }
        }

        @Override
        public void run() {
            if (list != null && list.size() > 0) {
                MainRepository repository = parent.get();
                String time;
                if (timeReport == NOT_TIMETABLE) {
                    time = DatabaseUtils.getTimeToStop(list);
                    repository.focusedCountdown.setValue(time);
                } else {
                    if (timeReport == TWELVE_HOUR){
                        time = DatabaseUtils.getBestBus(list, false);
                    } else {
                        time = DatabaseUtils.getBestBus(list, true);
                    }
                    repository.timetableCountdown.setValue(time);

                }
                repository.handler.postDelayed(this, 1000);
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
            repository.handler.postDelayed(this, 8000);
        }
    }


}
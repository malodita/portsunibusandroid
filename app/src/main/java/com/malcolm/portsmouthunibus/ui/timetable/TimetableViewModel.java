package com.malcolm.portsmouthunibus.ui.timetable;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.malcolm.portsmouthunibus.App;
import com.malcolm.unibusutilities.entity.Times;
import com.malcolm.unibusutilities.repository.MainRepository;

import java.util.List;

/**
 * The ViewModel
*/
public class TimetableViewModel extends AndroidViewModel {

    private final MediatorLiveData<String> countdown;
    private MediatorLiveData<List<Times>> timesLiveData;
    private MainRepository repository;

    private TimetableViewModel(@NonNull Application application, int stop, boolean is24Hours) {
        super(application);
        repository = ((App) application).getMainRepository();
        timesLiveData = new MediatorLiveData<>();
        timesLiveData.addSource(repository.findListOfTimesForStop(stop, is24Hours), timesLiveData::setValue);
        countdown = new MediatorLiveData<>();
        countdown.addSource(repository.getTimetableCountdown(), countdown::setValue);
        if (is24Hours) {
            repository.fetchListForTimetableCountdown(stop - 1, MainRepository.TWENTYFOUR_HOUR);
        } else {
            repository.fetchListForTimetableCountdown(stop - 1, MainRepository.TWELVE_HOUR);
        }
    }

    /**
     * Fetches a new list of times for an individual bus stop.
     * @param stop Id of stop
     * @param is24Hours If should be reported in 12 or 24 hour format
     */
    void changeListOfStops(int stop, boolean is24Hours){
        repository.findListOfTimesForStop(stop, is24Hours);
    }

    /**
     * Exposes the LiveData for monitoring the current list of stop times.
     * @return An array of Times suitable for use in a recyclerview
     */
    LiveData<List<Times>> getData() {
        return timesLiveData;
    }

    /**
     * Exposes the current countdown until the next bus for observation
     *
     * @return The LiveData representing the countdown. A single string representing the time
     */
    LiveData<String> getCurrentCountdown() {
        return countdown;
    }

    /**
     * Causes the {@link MainRepository} to fetch the times for a new stop. The LiveData exposed in
     * {@link #getCurrentCountdown()} is updated automatically.
     * @param stop The new stop to observe. This will always -1 to make it work correctly due to
     * the manual get method starting from 0 in the database (Representing eastney)
     */
    void updateStopList(int stop, boolean is24Hours){
        if (is24Hours) {
            repository.fetchListForTimetableCountdown(stop - 1, MainRepository.TWENTYFOUR_HOUR);
        } else {
            repository.fetchListForTimetableCountdown(stop - 1, MainRepository.TWELVE_HOUR);
        }    }

    @Override
    protected void onCleared() {
        repository.stopObservingTimetableStop();
        super.onCleared();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory{

        @NonNull
        private final Application application;
        private final boolean is24Hours;
        private final int stop;

        Factory(@NonNull Application application, boolean is24Hours, int stop) {
            this.application = application;
            this.is24Hours = is24Hours;
            this.stop = stop;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new TimetableViewModel(application, stop, is24Hours);
        }

    }
}

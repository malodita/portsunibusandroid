package com.malcolm.portsmouthunibus.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.malcolm.portsmouthunibus.App;
import com.malcolm.unibusutilities.entity.DirectionsApi;
import com.malcolm.unibusutilities.model.StopAndTime;
import com.malcolm.unibusutilities.repository.LocationRepository;
import com.malcolm.unibusutilities.repository.MainRepository;

class HomeViewModel extends AndroidViewModel {

    private final LocationRepository locationRepository;
    private final MainRepository mainRepository;
    private final MediatorLiveData<String> countdown;
    private final MediatorLiveData<StopAndTime> instantCountdown;
    private boolean instantActive = false;
    private final Observer<String> countdownObserver;
    private Observer<StopAndTime> instantCountdownObserver;



    private HomeViewModel(@NonNull Application application, int stop) {
        super(application);
        locationRepository = ((App) application).getLocationRepository();
        mainRepository = ((App) application).getMainRepository();
        countdown = new MediatorLiveData<>();
        countdownObserver = countdown::setValue;
        instantCountdown = new MediatorLiveData<>();
        countdown.addSource(mainRepository.getCountdown(), countdownObserver);
        mainRepository.fetchListForFocusedCountdown(stop, MainRepository.NOT_TIMETABLE);
    }

    void beginInstantFetching(){
        if (!instantActive) {
            mainRepository.fetchListForInstantCountdown();
            if (instantCountdownObserver == null){
                instantCountdownObserver = instantCountdown::setValue;
            }
            instantCountdown.addSource(mainRepository.getInstantCountdown(), instantCountdownObserver);
            instantActive = true;
        }
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
     * @param stop The new stop to observe
     */
    void updateStopList(int stop){
        mainRepository.fetchListForFocusedCountdown(stop, MainRepository.NOT_TIMETABLE);
    }

    /**
     * Retrieves LiveData for the countdown attributed for the nearest stop to the location
     *
     * @return LiveData of {@link StopAndTime} containing two string fields for closest stop and time to report
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    LiveData<StopAndTime> getInstantCountdown(){
        return instantCountdown;
    }

    /**
     * Required to stop instant stop monitoring.
     */
    void stopInstantUpdates(){
        mainRepository.stopObservingInstantStop();
        instantActive = false;
    }

    @Override
    protected void onCleared() {
        mainRepository.stopObservingFocusedStop();
        if (instantActive){
            mainRepository.stopObservingInstantStop();
            instantCountdown.removeSource(mainRepository.getInstantCountdown());
            instantActive = false;
        }
        super.onCleared();
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    LiveData<DirectionsApi> getDirectionsLiveData() {
        return locationRepository.getDirections();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory{

        @NonNull
        private final Application application;
        private final int stop;

        public Factory(@NonNull Application application, int stop) {
            this.application = application;
            this.stop = stop;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new HomeViewModel(application, stop);
        }

    }

}

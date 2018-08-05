package com.malcolm.unibusutilities.repository;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import android.content.Context;
import android.location.Location;

import com.malcolm.unibusutilities.BuildConfig;
import com.malcolm.unibusutilities.entity.DirectionsApi;
import com.malcolm.unibusutilities.helper.BoundLocationReporter;
import com.malcolm.unibusutilities.helper.BusStopUtils;
import com.malcolm.unibusutilities.helper.CacheUtils;
import com.malcolm.unibusutilities.model.LocationObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationRepository {

    private static final String TAG = "LocationRepository";
    @SuppressLint("StaticFieldLeak")
    private static LocationRepository instance;
    private final Context applicationContext;
    private DirectionsApi.Dao service;
    private BoundLocationReporter liveLocation;
    private MediatorLiveData<Location> closestStopLiveData;
    private Observer<LocationObject> currentLocationObserver;

    private BoundLocationReporter lastLocation;
    private MediatorLiveData<DirectionsApi> directionsLiveData;

    private Observer<LocationObject> lastLocationObserver;

    private LocationRepository(final Application application) {
        applicationContext = application.getApplicationContext();
        liveLocation = new BoundLocationReporter(applicationContext, true);
        lastLocation = new BoundLocationReporter(applicationContext, false);
    }

    public static synchronized LocationRepository getInstance(Application application) {
        if (instance == null) {
            instance = new LocationRepository(application);
        }
        return instance;
    }

    /**
     * Obtains LiveData representing the closest stop.
     *
     * @return The location of the closest stop if less than 45 metres out or null.
     */
    LiveData<Location> getClosestStop() {
        if (closestStopLiveData == null) {
            closestStopLiveData = new MediatorLiveData<>();
            currentLocationObserver = locationObject -> {
                if (locationObject != null && locationObject.isSuccessful() != null) {
                    Location current = locationObject.getLocation();
                    Location closest = BusStopUtils.getClosestStop(locationObject.getLocation());
                    if (closest != null && current.distanceTo(closest) <= 45) {
                        closestStopLiveData.setValue(closest);
                    } else {
                        closestStopLiveData.setValue(new Location("null"));
                    }
                }
            };
        }
        closestStopLiveData.addSource(liveLocation, currentLocationObserver);
        return closestStopLiveData;
    }


    void removeObserver() {
        if (currentLocationObserver != null) {
            closestStopLiveData.removeSource(liveLocation);
        }
    }

    private void makeRequest(Location currentLocation, Location targetLocation) {
        String current = String.valueOf(currentLocation.getLatitude()
                + "," + currentLocation.getLongitude());
        String target = String.valueOf(targetLocation.getLatitude()
                + "," + targetLocation.getLongitude());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(DirectionsApi.Dao.class);
        service.getResponse(current, target, "walking", BuildConfig.API_KEY)
                .enqueue(new Callback<DirectionsApi>() {
                    @Override
                    public void onResponse(Call<DirectionsApi> call, Response<DirectionsApi> response) {
                        if (response.isSuccessful()) {
                            if (!response.body().getStatus().equals("OK")) {
                                DirectionsApi model = response.body();
                                model.setError(true);
                                model.setErrorCode(1);
                                directionsLiveData.postValue(model);
                            } else {
                                DirectionsApi responseBody = response.body();
                                responseBody.setStartLocation(currentLocation);
                                responseBody.setEndLocation(targetLocation);
                                responseBody.setCacheTime(System.currentTimeMillis());
                                directionsLiveData.postValue(responseBody);
                                CacheUtils.cacheResponse(responseBody, applicationContext);
                            }
                        } else {
                            DirectionsApi model = response.body();
                            model.setError(true);
                            model.setErrorCode(-1);
                            directionsLiveData.postValue(model);
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsApi> call, Throwable t) {
                        DirectionsApi model = new DirectionsApi();
                        model.setError(true);
                        model.setErrorCode(2);
                        directionsLiveData.postValue(model);
                    }
                });
        if (lastLocation.getAttempt() > 2 || lastLocation.wasSuccessful()) {
            lastLocation.removeObserver(lastLocationObserver);
        }

    }

    private Observer<LocationObject> makeLastLocationObserver() {
        return locationObject -> {
            if (locationObject != null && locationObject.isSuccessful()) {
                Location currentLocation = locationObject.getLocation();
                Location targetLocation = BusStopUtils.getClosestStop(currentLocation);
                if (targetLocation != null) {
                    switch (CacheUtils.shouldMakeApiRequest(currentLocation, targetLocation, applicationContext)) {
                        case CacheUtils.MAKE_REQUEST:
                            makeRequest(currentLocation, targetLocation);
                            break;
                        case CacheUtils.USE_CACHE:
                            directionsLiveData.postValue(CacheUtils.retrieveResponseFromCache(applicationContext));
                            break;
                        case CacheUtils.NEAR_STOP:
                            DirectionsApi model = new DirectionsApi();
                            model.setNearStop(true);
                            model.setStartLocation(currentLocation);
                            model.setEndLocation(targetLocation);
                            directionsLiveData.postValue(model);
                            break;
                    }
                } else {
                    DirectionsApi model = new DirectionsApi();
                    model.setError(true);
                    model.setErrorCode(4);
                    directionsLiveData.postValue(model);
                }
            }
        };
    }

    public LiveData<DirectionsApi> getDirections() {
        if (!lastLocation.hasActiveObservers()) {
            if (directionsLiveData == null) {
                directionsLiveData = new MediatorLiveData<>();
                lastLocationObserver = makeLastLocationObserver();
            } else {
                if (lastLocationObserver == null){
                    directionsLiveData = new MediatorLiveData<>();
                    lastLocationObserver = makeLastLocationObserver();
                }
            }
            directionsLiveData.addSource(lastLocation, lastLocationObserver);
        }
        return directionsLiveData;
    }
}



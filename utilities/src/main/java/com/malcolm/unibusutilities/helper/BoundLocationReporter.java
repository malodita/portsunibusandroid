package com.malcolm.unibusutilities.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.malcolm.unibusutilities.model.LocationObject;

public class BoundLocationReporter extends LiveData<LocationObject> implements OnSuccessListener<Location> {

    private static final String TAG = "BoundLocationReporter";
    private FusedLocationProviderClient client;
    private LocationObject locationObject;
    private LocationCallback callback;
    private Context context;
    private boolean isRepeating;
    private int attempt = 1;
    private boolean wasSuccess = false;


    public BoundLocationReporter(Context context, boolean isRepeating) {
        this.context = context;
        this.isRepeating = isRepeating;
        client = LocationServices.getFusedLocationProviderClient(context);
    }


    @Override
    protected void onActive() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            setValue(new LocationObject(false, null));
            return;
        }
        client.getLastLocation().addOnSuccessListener(this);
        super.onActive();
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            locationObject = new LocationObject(true, location);
            if (isRepeating) {
                startLocationUpdates();
            }
            wasSuccess = true;
        } else {
            locationObject = new LocationObject(false, null);
            attempt++;
        }
        setValue(locationObject);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        LocationRequest request = LocationRequest.create()
                .setFastestInterval(6000)
                .setInterval(12000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    locationObject.setLocation(location);
                    locationObject.setSuccess(true);
                    setValue(locationObject); //Todo: Move to background thread if needed
                }
                super.onLocationResult(locationResult);
            }
        };
        client.requestLocationUpdates(request, callback, null);
    }

    @Override
    protected void onInactive() {
        if (callback != null) {
            client.removeLocationUpdates(callback);
        }
        super.onInactive();

    }

    public int getAttempt() {
        return attempt;
    }

    public boolean wasSuccessful() {
        return wasSuccess;
    }
}

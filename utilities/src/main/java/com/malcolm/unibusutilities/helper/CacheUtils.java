package com.malcolm.unibusutilities.helper;

import android.content.Context;
import android.location.Location;
import android.os.Process;
import androidx.annotation.Nullable;

import com.malcolm.unibusutilities.entity.DirectionsApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public final class CacheUtils {

    public static final int MAKE_REQUEST = 0;
    public static final int USE_CACHE = 1;
    public static final int NEAR_STOP = 2;


    public static int shouldMakeApiRequest(Location currentLocation, Location targetLocation, Context applicationContext){
        //If the distance between the last location and the closest stop < 180 metres, ~3 minutes to stop on average
        if (currentLocation.distanceTo(targetLocation) < 180) {
            //If so a near warning is sent
            return NEAR_STOP;
        }
        long cacheDate = 0;
        Location cachedLocation = null;
        DirectionsApi model = retrieveResponseFromCache(applicationContext);
        if (model != null) {
            cacheDate = model.getCacheTime();
            cachedLocation = model.getRoutes().get(0).getLegs().get(0).getStartLocation().getLocation();
        }
        //If the cache exists
        if (cachedLocation == null) {
            return MAKE_REQUEST;
        }
        long cacheTime = System.currentTimeMillis() - cacheDate;
            //If the distance between the cached location and last location < 100 metres
            if (cachedLocation.distanceTo(currentLocation) < 100) {
                //The cached location is sent
                return USE_CACHE;
            } else {
                if (cacheTime > 1800000) {
                    //If the cached item has expired (30 minutes)
                    return MAKE_REQUEST;
                } else {
                    return USE_CACHE;
                }
            }

    }


    /**
     * Attempts to return a previous response from the cache directory.
     *
     * @return A new ResponseSchema object with the cached data, or null if no such file exists.
     */
    @Nullable
    public static DirectionsApi retrieveResponseFromCache(Context applicationContext) {
        DirectionsApi model = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(new File(applicationContext.getCacheDir(), "") + "response.srl")));
            model = (DirectionsApi) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            if (e instanceof FileNotFoundException) {
                return model;
            }
            e.printStackTrace();
        }
        return model;
    }

    /**
     * Caches the response to internal storage. Since it is the apps private directory, write to
     * storage permissions are not needed. Every time a new file is cached, the old one is
     * overwritten. It is done on a background thread.
     *
     * @param response The response to cache
     */
    public static void cacheResponse(final DirectionsApi response, Context applicationContext) {
        new Thread(() -> {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            ObjectOutput out;
            try {
                out = new ObjectOutputStream(new FileOutputStream(new File(applicationContext.getCacheDir().getAbsolutePath()) + "response.srl"));
                out.writeObject(response);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}

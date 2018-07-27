package com.malcolm.portsmouthunibus;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.unibusutilities.database.BusDatabase;
import com.malcolm.unibusutilities.repository.LocationRepository;
import com.malcolm.unibusutilities.repository.MainRepository;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private RefWatcher refWatcher;
    private static FirebaseAnalytics firebase;

    @Override
    public void onCreate() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher = LeakCanary.install(this);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        String nightMode = sharedPreferences.getString(getString(R.string.preferences_night_mode_new), "0");
        boolean analytics = sharedPreferences.getBoolean(getString(R.string.preferences_analytics), false);
        firebase = FirebaseAnalytics.getInstance(this);
        if (!BuildConfig.DEBUG) {// TODO: 14/07/2018 remove when testing. Should have crash reporting enabled
            if (analytics) {
                Fabric.with(this, new Crashlytics());
                firebase.setAnalyticsCollectionEnabled(true);
            } else {
                firebase.setAnalyticsCollectionEnabled(false);
            }
        } else {
            firebase.setAnalyticsCollectionEnabled(true);
        }
        nightModeSwitching(Integer.valueOf(nightMode));
        super.onCreate();
    }

    public MainRepository getMainRepository() {
        return MainRepository.getInstance(this, getDatabase());
    }

    public LocationRepository getLocationRepository(){
        return LocationRepository.getInstance(this);
    }

    public BusDatabase getDatabase(){
        return BusDatabase.getInstance(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        App app = (App) context.getApplicationContext();
        return app.refWatcher;
    }

    public static void analyticsSwitching(boolean enabled){
        if (enabled){
            firebase.setAnalyticsCollectionEnabled(true);
        } else {
            firebase.setAnalyticsCollectionEnabled(true);
        }
    }

    /**
     * Static method to enable DayNight
     */
    public static void nightModeSwitching(int mode) {
        switch (mode){
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            break;
        }
    }

}

package com.malcolm.portsmouthunibus;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.malcolm.portsmouthunibus.adapters.HomeFragmentAdapter;
import com.malcolm.portsmouthunibus.models.ResponseSchema;
import com.malcolm.portsmouthunibus.utilities.BusStopUtils;
import com.malcolm.portsmouthunibus.utilities.NetworkRequest;
import com.malcolm.unibusutilities.BusStops;
import com.malcolm.unibusutilities.DatabaseHelper;
import com.malcolm.unibusutilities.TermDates;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment implements Callback<ResponseSchema>, SharedPreferences.OnSharedPreferenceChangeListener,
        OnSuccessListener<Location>{
    private static final int DEFAULT_VALUE = 0;
    public static final String TAG = "Home Fragment";
    private final Handler handler = new Handler();
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.top_fragment_coordinator)
    CoordinatorLayout layout;
    private int stopToShow;
    private int currentNightMode;
    private Runnable homeRunnable;
    private HomeFragmentAdapter adapter;
    private ArrayList<Integer> stopTimes;
    private Unbinder unbinder;
    private DatabaseHelper databaseHelper;
    private Boolean isInstantCardDisplayed = false;
    private String[] busStops;
    private Call<ResponseSchema> call;
    private InstantCard instantCard;
    private SharedPreferences sharedPreferences;
    private Location closest;
    private final CurrentLocationListener listener = new CurrentLocationListener(this);
    private boolean requestingLocationUpdates = false;
    private FusedLocationProviderClient client;



    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAnalytics.getInstance(getContext());
        currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        busStops = getResources().getStringArray(R.array.bus_stops_home_selected);
        databaseHelper = DatabaseHelper.getInstance(getContext());
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        client = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        stopToShow = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), DEFAULT_VALUE);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Sets up the recyclerview and cards
        ArrayList<Object> homeCard = makeHomeCard(stopToShow, busStops);
        ArrayList<Object> mapsCard = makeMapCard();
        setupRecyclerView(recyclerView, homeCard, mapsCard);
    }


    /**
     * Sets up the home card. Info is placed in an array of type object in following order <p> 0 -
     * Boolean: If the stop to set to 'No stop selected' meaning no stop info needed </p> 1 -
     * String: The identity of the selected stop including no stop selected <p> 2 - String:
     * (OPTIONAL) The string with the time to report taken from the array of stop times. </p>
     * <p>
     * 3 - Boolean: If the term date is a holiday
     * </p>
     * <p>
     * 4 - Boolean: If it is the weekend during the holiday
     * </p>
     * Array is a field as this is subsequently used to set the initial state of the card, it does
     * not take care of updating it {@link HomeCardTask which is left to a runnable task}.
     *
     * @return An array list suitable for use as an initial payload for the home card
     */
    private ArrayList<Object> makeHomeCard(int stopToShow, String[] stopList) {
        ArrayList<Object> array = new ArrayList<>();
        if (stopToShow == 0) {
            array.add(false);
            array.add(stopList[stopToShow]);
        } else {
            stopTimes = databaseHelper.getTimesForArray(stopToShow);
            String stop = getTimeToStop(stopTimes);
            array.add(true);
            array.add(stopList[stopToShow]);
            array.add(stop);
            array.add(TermDates.isBankHoliday());
            array.add(TermDates.isWeekendInHoliday());
        }
        return array;
    }

    /**
     * Sets up the map card. Info is placed in an array of type object in following order <p> 0 -
     * int: If google play services is available </p> 1 - int: If play services error is resolvable
     * or not <p> 2 - int: If location permission has been granted <p> 3 - int: If device GPS is on
     * or not</p> </p> 4 - MapStyleOptions: The layout to be used by the googlemap should night mode
     * be enabled. <p> 5 - int: If night mode is enabled so that night resources may be loaded for
     * the polyline <p> This method is only called once to help determine if the map requires
     * setting up or not. Updating the map is done by a chain of methods starting from {@link
     * #startLocationUpdates()} in the case that the current location is obtainable</P> </p>
     *
     * @return An array list suitable to use as an initial payload for the maps card
     */
    private ArrayList<Object> makeMapCard() {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(sharedPreferences.getBoolean(getString(R.string.preferences_maps_card), true));
        arrayList.add(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()));
        arrayList.add(isGooglePlayServicesAvailable(getActivity()));
        int permission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        arrayList.add(permission);
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            arrayList.add(0);
        } else {
            arrayList.add(1);
        }
        if (permission != PackageManager.PERMISSION_GRANTED || !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return arrayList;
        }
        arrayList.add(setNightModeLayout());
        arrayList.add(currentNightMode);

        return arrayList;
    }

    /**
     * Sets up the recyclerview with the data required for the two cards
     *
     * @param recyclerView The recyclerview to be set up
     * @param homeCard     The initial home card payload
     * @param mapCard      The initial map card payload
     */
    private void setupRecyclerView(RecyclerView recyclerView
            , List<Object> homeCard, List<Object> mapCard) {
        if (adapter == null) {
            adapter = new HomeFragmentAdapter(homeCard, mapCard);
            adapter.hasStableIds();
        }
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        recyclerView.setAdapter(adapter);
        this.recyclerView = recyclerView;
    }


    /**
     * Takes the input from the arraylist, after which it is run through a comparison to find the
     * time closest to the current clock time and shows the difference in minutes up to an hour. It
     * the calls a further method to format the string ready for display
     *
     * @param times The arraylist to be searched
     *
     * @return A string with the formatted final time
     */
    private String getTimeToStop(List<Integer> times) {
        int currentTime = getCurrentTime();
        //int nearest = -1;
        int bestSoFar = Integer.MAX_VALUE;
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i) == currentTime) {
                bestSoFar = 0;
            } else {
                int d = Math.abs(currentTime - times.get(i));
                if (d < bestSoFar && currentTime <= times.get(i)) {
                    bestSoFar = d;
                    //nearest = times.get(i); In case we ever need to track the bus in question.
                }
            }
        }
        if (bestSoFar != Integer.MAX_VALUE) {
            return BusStopUtils.formatTime(bestSoFar);
        } else {
            return String.valueOf(bestSoFar);
        }
    }

    /**
     * Play services check. This is used in determining if the mapview should be set up
     *
     * @param activity The activity
     *
     * @return An int depending on if play services is available or not.
     */
    private int isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                return 1;
            } else {
                return 2;
            }
        }
        return 0;
    }

    /**
     * Sets the layout resource for the google map depending on if night mode is active or not.
     * Done in the fragment as to allow for a context needing method to work
     *
     * @return The layout to be displayed by the googlemap
     */
    private MapStyleOptions setNightModeLayout() {
        MapStyleOptions layout;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            layout = MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.night_json);
        } else {
            layout = MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.day_json);
        }
        return layout;
    }

    /**
     * Calculates the current clock time in seconds
     *
     * @return The current time in seconds
     */
    private int getCurrentTime() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        return ((hour * 60) + minute) * 60;
    }


    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        startLocationUpdates();
        int currentHome = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), DEFAULT_VALUE);
        //Checks the previous value of the stop to show
        if (stopToShow != 0) {
            //Checks if this value has been changed
            if (currentHome == 0) {
                //If the value was removed, it nullifies the timehero update handler and clears the layout
                handler.removeCallbacksAndMessages(null);
                adapter.invalidateHomeCard();
            } else {
                updateTimeHero();
            }
        }
        if (!sharedPreferences.getBoolean(getString(R.string.preferences_maps_card), true)) {
            adapter.hideMapsCard();
        }
        //If the instantCard was previously displayed, it will restart. Used if fragment was hidden but not destroyed
        if (isInstantCardDisplayed && instantCard != null) {
            handler.post(instantCard);
        }
    }

    /**
     * Starts the update for the timehero. If previously paused (meaning the runnable is non Null
     * such as on rotation) it will post the runnable rather than create a new one.
     */
    public void updateTimeHero() {
        if (TermDates.isWeekendInHoliday()) {
            adapter.weekendInHoliday();
            return;
        }
        if (TermDates.isBankHoliday()) {
            adapter.bankHoliday();
            return;
        }
        if (homeRunnable == null) {
            homeRunnable = new HomeCardTask(this);
        }
        handler.post(homeRunnable);
    }

    @Override
    public void onSuccess(Location location) {
        if (location == null) {
            adapter.noConnection();
            return;
        }
        closest = BusStops.getClosestStop(location);
        final boolean mapCardAllowed = sharedPreferences.getBoolean(getString(R.string.preferences_maps_card), true);
        if (mapCardAllowed) {
            adapter.showMapsCard();
            getDirections(location);
        } else {
            adapter.hideMapsCard();
        }
        instantCardCheck(location);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext()
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (isGooglePlayServicesAvailable(getActivity()) == 0){
            client.getLastLocation().addOnSuccessListener(this);
            if (!requestingLocationUpdates) {
                LocationRequest request = LocationRequest.create()
                        .setFastestInterval(15000)
                        .setInterval(60000)
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                client.requestLocationUpdates(request, listener, null);
                requestingLocationUpdates = true;
            }
        } else {
            adapter.playServices(isGooglePlayServicesAvailable(getActivity()));
        }

    }


    private void stopLocationUpdates() {
        if (requestingLocationUpdates) {
            client.removeLocationUpdates(listener);
            requestingLocationUpdates = false;
        }
    }

    /**
     * The method determines if a request to the Directions API should be sent. It parses a
     * previously cached response to see if the added tag for the cache date is expired. If it
     * has expired, the recorded start position is greater than 600 metres from the latest
     * position reported OR the cache file does not exist, it will send a request. Otherwise it uses
     * the cached response to build the googlemap.<p> It also controls if the instant card should be
     * set up or removed.</p>
     *
     * @param lastLocation The last location recorded
     */
    private void getDirections(@NonNull Location lastLocation) {
        Location closest = BusStops.getClosestStop(lastLocation);
        if (closest == null) {
            adapter.notInPortsmouth();
            return;
        }
        long cacheDate = 0;
        Location cachedLocation = null;
        ResponseSchema responseSchema = retrieveResponseFromCache();
        if (responseSchema != null) {
            cacheDate = responseSchema.getTime();
            cachedLocation = responseSchema.getRoutes()
                    .get(0).getLegs().get(0).getStartLocation().getLocation();
        }
        long cacheTime = System.currentTimeMillis() - cacheDate;
        //If the cache exists
        if (cachedLocation == null) {
            sendDirectionsRequest(lastLocation, closest);
            return;
        }
        //If the distance between the last location and  the closest stop < 120 metres
        if (lastLocation.distanceTo(closest) < 150) {
            //If so a near warning is sent
            sendMapInfoToAdapter(closest);
        } else {
            //If the distance between the cached location and last location < 100 metres
            if (cachedLocation.distanceTo(lastLocation) < 80) {
                //The cached location is sent
                sendCachedLocationToAdapter(responseSchema, closest);
            } else {
                //If the distance is grater than 150 metres from stop and cached location is more than 80 metres away
                if (cacheTime > 180000) {
                    //If the cached item has expired
                    sendDirectionsRequest(lastLocation, closest);
                } else {
                    sendCachedLocationToAdapter(responseSchema, closest);
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.preferences_maps_card))){
            if (sharedPreferences.getBoolean(s, true)){
                adapter.showMapsCard();
                startLocationUpdates();
            }
        }
    }

    private void sendCachedLocationToAdapter(ResponseSchema responseSchema, Location closest) {
        String summary = responseSchema.getRoutes().get(0).getSummary();
        String polyline = responseSchema.getRoutes().get(0).getOverviewPolyline().getPoints();
        double time = responseSchema.getRoutes().get(0).getLegs().get(0).getDuration().getValue();
        ArrayList<Object> list = new ArrayList<>();
            /*Packs into locationReady() in this order
                0 - String: The name of the closest stop
                1 - Double: Time to closest stop
                2 - String: Polyline
                3 - String: Route summary (Not currently used)
            */
        list.add(closest.getProvider());
        list.add(time);
        list.add(polyline);
        list.add(summary);
        adapter.locationReady(list);
    }

    private void instantCardCheck(Location location) {
        boolean instant = sharedPreferences.getBoolean(getString(R.string.preferences_instant_card), true);
        if (!instant) {
            removeInstantCard();
            return;
        }
        if (ActivityCompat.checkSelfPermission(getContext()
                , Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location closest = BusStops.getClosestStop(location);
        if (closest != null) {
            float distance = location.distanceTo(closest);
            if (distance <= 45) {
                if (isInstantCardDisplayed && instantCard != null) {
                    handler.post(instantCard);
                } else {
                    isInstantCardDisplayed = setupInstantCard(closest);
                }
            } else {
                removeInstantCard();
            }
        } else {
            removeInstantCard();
        }
    }

    /**
     * Attempts to return a previous response from the cache directory.
     *
     * @return A new ResponseSchema object with the cached data, or null if no such file exists.
     */
    @Nullable
    private ResponseSchema retrieveResponseFromCache() {
        ResponseSchema responseSchema = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(new File(getContext().getCacheDir(), "") + "response.srl")));
            responseSchema = (ResponseSchema) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            if (e instanceof FileNotFoundException) {
                return responseSchema;
            }
            e.printStackTrace();
        }
        return responseSchema;
    }

    /**
     * If the device is fairly close to the nearest stop, it will manually add the information to
     * the maps card instead of sending an API request for directions
     *
     * @param closest The closest stop
     */
    private void sendMapInfoToAdapter(Location closest) {
        ArrayList<Object> list = new ArrayList<>();
        list.add(closest.getProvider());
        double time = -1;
        list.add(time);
        list.add(closest);
        adapter.closeToStop(list);
    }

    /**
     * This method sends an API request using retrofit. Two callbacks are implemented by the class
     * depending on if a response is successful
     */
    private void sendDirectionsRequest(Location currentLocation, Location targetLocation) {
        String current = String.valueOf(currentLocation.getLatitude()
                + "," + currentLocation.getLongitude());
        String target = String.valueOf(targetLocation.getLatitude()
                + "," + targetLocation.getLongitude());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NetworkRequest networkRequest = retrofit.create(NetworkRequest.class);
        call = networkRequest.getResponse(current, target, "walking", BuildConfig.API_KEY);
        call.enqueue(this);
    }

    /**
     * Sets up the instantCard card and associated handler, {@link InstantCardTask most of the work
     * is done in the asynctask associated with the runnable} {@link InstantCard}. Info is placed in
     * an array of type object in following order <p> 0: timeHero </p> 1: stopHero <p> This method
     * will only be called if the user is within 60 metres of a stop. </p> It also checks if the
     * closest stop is IMS Eastney (if the date is not a weekday) as well as if it is a bank
     * holiday
     *
     * @return A boolean indicating success establishing the instant card
     */
    private boolean setupInstantCard(Location closest) {
        if (TermDates.isBankHoliday()) {
            return false;
        }
        if (closest.getProvider().equals("IMS Eastney (Departures)")) {
            if (TermDates.isHoliday() || TermDates.isWeekend()) {
                return false;
            }
        }
        String search;
        if (closest.getProvider().equals("Langstone")) {
            search = "Langstone Campus (for Departures only)";
        } else {
            search = closest.getProvider();
        }
        ArrayList<Integer> arrayList = databaseHelper.getTimesForArray(search);
        if (arrayList == null) {
            Snackbar snackbar = Snackbar.make(layout, "Error displaying the closest stop card", Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_dark));
            snackbar.show();
            FirebaseCrash.report(new Throwable("No result was returned by DatabaseHelper to set up the Instant Card"));
            return false;
        }
        String time = getTimeToStop(arrayList);
        ArrayList<Object> list = new ArrayList<>();
        list.add(time);
        list.add(closest.getProvider());
        adapter.instantCard(list);
        if (instantCard == null) {
            instantCard = new InstantCard(this, arrayList);
            handler.post(instantCard);
        }
        return true;
    }

    /**
     * Removes the instantCard card and disables the runnable associated with it if it is displayed.
     * If it is not displayed, it will do nothing
     */
    private void removeInstantCard() {
        if (isInstantCardDisplayed) {
            adapter.removeInstant();
            handler.removeCallbacks(instantCard);
            isInstantCardDisplayed = false;
        }
    }

    @Override
    public void onResponse(Call<ResponseSchema> call, Response<ResponseSchema> response) {
        if (!response.isSuccessful()) {
            //If the response is not code 200-300
            Snackbar snackbar = Snackbar.make(layout, getString(R.string.error_server_response), Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_dark));
            snackbar.show();
            adapter.noConnection();
            return;
        }
        ResponseSchema responseSchema = response.body();
        if (!responseSchema.getStatus().equals("OK")) {
            //If the API is not returning the required response for any reason
            adapter.noConnection();
            FirebaseCrash.log(TAG + "API request not returned correctly");
            return;
        }
        //Adds the current time to the response body which is used for caching purposes
        responseSchema.setTime(System.currentTimeMillis());
        cacheResponse(responseSchema);
        String summary = responseSchema.getRoutes().get(0).getSummary();
        String polyline = responseSchema.getRoutes().get(0).getOverviewPolyline().getPoints();
        double time = responseSchema.getRoutes().get(0).getLegs().get(0).getDuration().getValue();
        ArrayList<Object> list = new ArrayList<>();
        /*Packs into locationReady() method in this order
        0 - String: The name of the closest stop
        1 - Double: Time to closest stop
        2 - String: Polyline
        3 - String: Route summary (Not currently used)*/
        list.add(closest.getProvider());
        list.add(time);
        list.add(polyline);
        list.add(summary);
        adapter.locationReady(list);
    }

    /**
     * Caches the response to internal storage. Since it is the apps private directory, write to
     * storage permissions are not needed. Every time a new file is cached, the old one is
     * overwritten. It is done on a background thread.
     *
     * @param response The response to cache
     */
    private void cacheResponse(final ResponseSchema response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                ObjectOutput out;
                try {
                    out = new ObjectOutputStream(new FileOutputStream(new File(getContext().getCacheDir().getAbsolutePath()) + "response.srl"));
                    out.writeObject(response);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    FirebaseCrash.log(TAG + "JSON write to cache failure");
                    FirebaseCrash.report(e);
                }
            }
        }).start();
    }

    @Override
    public void onFailure(Call<ResponseSchema> call, Throwable t) {
        adapter.noInternet();
        if (getContext() != null) {
            Snackbar snackbar = Snackbar.make(layout,
                    getString(R.string.error_server_response), Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_dark));
            snackbar.show();
            FirebaseCrash.log(TAG + ": Error making API request");
        }

    }

    /**
     * Changes the home card according to the new home stop selected.
     * It removes the callbacks for the runnable and restarts it so that it may update immediately
     * <p>
     * Note: this should only be called from {@link HomeActivity}
     * </p>
     */
    public void changeHomeCard() {
        stopToShow = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), DEFAULT_VALUE);
        stopTimes = databaseHelper.getTimesForArray(stopToShow);
        if (homeRunnable != null) {
            handler.removeCallbacks(homeRunnable);
            handler.post(homeRunnable);
        } else {
            updateTimeHero();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        adapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        stopLocationUpdates();
        handler.removeCallbacksAndMessages(null);
        if (call != null) {
            call.cancel();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        recyclerView.setAdapter(null);
        unbinder.unbind();
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    /**
     * This class implements the LocationListener interface instead of the HomeFragment as this led
     * to constant memory leaks
     */
    private static class CurrentLocationListener extends LocationCallback {

        private WeakReference<HomeFragment> reference;

        CurrentLocationListener(HomeFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                HomeFragment fragment = reference.get();
                fragment.instantCardCheck(location);
            }
            super.onLocationResult(locationResult);
        }

    }

    /**
     * This class manipulates the home card to act as a countdown until the next bus. Only one
     * instance is needed so it is static as to prevent resource leaking
     */
    private static class HomeCardTask implements Runnable {

        private final WeakReference<HomeFragment> weakReference;

        HomeCardTask(HomeFragment parent) {
            weakReference = new WeakReference<>(parent);
        }

        @Override
        public void run() {
            final HomeFragment homeFragment = weakReference.get();
            if (homeFragment != null) {
                homeStopRunnableTask(homeFragment);
            }
        }

        /**
         * Repeating every 2 seconds, it updates the home card with the countdown. It packs its
         * information into an array which is sent to the adapter
         *
         * @param parent The top fragment instance
         */
        void homeStopRunnableTask(HomeFragment parent) {
            String time = parent.getTimeToStop(parent.stopTimes);
            ArrayList<String> array = new ArrayList<>();
            array.add(time);
            array.add(parent.busStops[parent.stopToShow]);
            parent.adapter.updateTimeHero(array);
            parent.handler.postDelayed(parent.homeRunnable, 2000);
        }

    }

    /**
     * This class is a holder for the {@link InstantCardTask} asynctask so that it may repeat.
     */
    private static class InstantCard implements Runnable {

        WeakReference<HomeFragment> parent;
        ArrayList<Integer> list;


        InstantCard(HomeFragment homeFragment, List<Integer> list) {
            parent = new WeakReference<>(homeFragment);
            this.list = (ArrayList<Integer>) list;
        }

        @SuppressLint("SwitchIntDef")
        @Override
        public void run() {
            if (parent != null) {
                new InstantCardTask(parent, list).execute();
                parent.get().handler.postDelayed(this, 5000);
            }
        }
    }

    /**
     * A class extending asynctask for manipulating the instantCard card, it takes in the
     * array of stop times to be searched and a weak reference to the topFragment to access its
     * methods without possibly leaking the fragment
     */
    private static class InstantCardTask extends AsyncTask<Void, Void, ArrayList<Object>> {

        HomeFragment fragment;
        ArrayList<Integer> array;

        /**
         * Creates the task
         *
         * @param parent The topFragment instance
         * @param array  An array of stop times to be searched
         */
        InstantCardTask(WeakReference<HomeFragment> parent, ArrayList<Integer> array) {
            fragment = parent.get();
            this.array = array;
        }

        @Override
        protected ArrayList<Object> doInBackground(Void... params) {
            ArrayList<Object> list = new ArrayList<>();
            String time = fragment.getTimeToStop(array);
            list.add(time);
            if (fragment.closest.getProvider().equals("IMS Eastney (Departures)")) {
                list.add("IMS Eastney (To university)");
            } else {
                list.add(fragment.closest.getProvider());
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> objects) {
            super.onPostExecute(objects);
            fragment.adapter.instantCard(objects);
        }
    }

}
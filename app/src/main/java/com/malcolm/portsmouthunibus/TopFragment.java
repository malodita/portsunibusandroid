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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.crash.FirebaseCrash;
import com.malcolm.portsmouthunibus.adapters.TopFragmentAdapter;
import com.malcolm.portsmouthunibus.models.ResponseSchema;
import com.malcolm.portsmouthunibus.models.Route;
import com.malcolm.portsmouthunibus.utilities.BusStopUtils;
import com.malcolm.portsmouthunibus.utilities.ResponseParser;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TopFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, Callback<ResponseSchema> {
    public final int DEFAULT_VALUE = 0;
    public final String TAG = "Top Fragment";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.top_fragment_coordinator)
    CoordinatorLayout layout;
    private GoogleApiClient googleApiClient;
    private int stopToShow;
    private int currentNightMode;
    private Runnable homeRunnable;
    private final Handler handler = new Handler();
    private TopFragmentAdapter adapter;
    private ArrayList<Integer> stopTimes;
    private Unbinder unbinder;
    private Location closest;
    private DatabaseHelper databaseHelper;
    private Boolean isInstantCardDisplayed = false;
    private String[] busStops;
    private Call<ResponseSchema> call;
    private InstantCard instantCard;

    public TopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        busStops = getResources().getStringArray(R.array.bus_stops_home_selected);
        databaseHelper = DatabaseHelper.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_top, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        SharedPreferences currentHomeStopPref = getActivity().getSharedPreferences(getString(R.string.preferences_home_bus_stop_key), 0);
        stopToShow = currentHomeStopPref.getInt(getString(R.string.preferences_home_bus_stop_key), DEFAULT_VALUE);
        googleApiClient = new GoogleApiClient.Builder(getContext().getApplicationContext())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();
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
     * (OPTIONAL) The string with the time to report taken from the array of stop times. </p> This
     * array is a field as this is subsequently used to set the initial state of the card, it does
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
     * setting up or not. Updating the map is done by a chain pf methods starting from {@link
     * #onConnected(Bundle)} in the case that the current location is obtainable</P> </p>
     *
     * @return An array list suitable to use as an initial payload for the maps card
     */
    private ArrayList<Object> makeMapCard() {
        ArrayList<Object> arrayList = new ArrayList<>();
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
            , ArrayList<Object> homeCard, ArrayList<Object> mapCard) {
        if (adapter == null) {
            adapter = new TopFragmentAdapter(homeCard, mapCard);
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
    private String getTimeToStop(ArrayList<Integer> times) {
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences home = getActivity().getSharedPreferences(getString(R.string.preferences_home_bus_stop_key), 0);
        int currentHome = home.getInt(getString(R.string.preferences_home_bus_stop_key), DEFAULT_VALUE);
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
        //If the instantCard was previously displayed, it will restart as well
        if (isInstantCardDisplayed && instantCard != null) {
            handler.post(instantCard);
        }
    }

    /**
     * Starts the update for the timehero. If previously paused (meaning the runnable ins nonNull
     * such as on rotation) it will post the runnable rather than create a new one.
     */
    public void updateTimeHero() {
        if (homeRunnable == null) {
            homeRunnable = new HomeCardTask(this);
        }
        handler.post(homeRunnable);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                closest = BusStops.getClosestStop(lastLocation);
                getDirections(lastLocation, closest);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Location permission not granted!", Toast.LENGTH_SHORT).show();
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
     * @param closest      The location and tag of the closest stop
     */
    private void getDirections(@NonNull Location lastLocation, Location closest) {
        if (closest == null) {
            adapter.notInPortsmouth();
            return;
        }
        long cacheDate = 0;
        Location location = null;
        ResponseSchema responseSchema = retrieveResponseFromCache();
        if (responseSchema != null) {
            cacheDate = responseSchema.getTime();
            location = responseSchema.getLocation();
        }
        long cacheTime = System.currentTimeMillis() - cacheDate;
        if (lastLocation.distanceTo(closest) < 90) {
            sendMapInfoToAdapter(closest.getProvider());
        } else if (cacheTime > 120000 || cacheDate == 0) {
            sendDirectionsRequest(lastLocation, closest);
        } else if (location != null && lastLocation.distanceTo(location) > 600) {
            sendDirectionsRequest(lastLocation, closest);
        } else {
            Route route = responseSchema.getRoutes().get(0);
            String summary = route.getSummary();
            String polyline = route.getOverviewPolyline().getPoints();
            double time = route.getLegs().get(0).getDuration().getValue();
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
        float distance = lastLocation.distanceTo(closest);
        if (distance <= 30) {
            setupInstantCard();
        } else {
            removeInstantCard();
        }
    }

    private void sendMapInfoToAdapter(String provider) {
        ArrayList<Object> list = new ArrayList<>();
        list.add(provider);
        double time = -1;
        list.add(time);
        list.add(closest);
        adapter.closeToStop(list);
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
        ResponseParser responseParser = retrofit.create(ResponseParser.class);
        call = responseParser.getResponse(current, target, "walking", BuildConfig.API_KEY);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<ResponseSchema> call, Response<ResponseSchema> response) {
        ResponseSchema responseSchema = response.body();
        if (!responseSchema.getStatus().equals("OK")) {
            //If the API is not returning the required response for any reason
            adapter.noConnection();
            return;
        }
        //Adds the current time to the response body which is used for caching purposes
        responseSchema.setTime(System.currentTimeMillis());
        cacheResponse(responseSchema);
        Route route = responseSchema.getRoutes().get(0);
        String summary = route.getSummary();
        String polyline = route.getOverviewPolyline().getPoints();
        Double time = route.getLegs().get(0).getDuration().getValue();
        ArrayList<Object> list = new ArrayList<>();
                   /*Packs into locationReady() method in this order
                0 - String: The name of the closest stop
                1 - Double: Time to closest stop
                2 - String:Polyline
                3 - String: Route summary (Not currently used)
                 */
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
                    out = new ObjectOutputStream(new FileOutputStream(new File(getContext().getCacheDir().getAbsolutePath()) + "/response.srl"));
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
                    "Error getting response from server", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_dark));
            snackbar.show();
            FirebaseCrash.log(TAG + ": Error making API request");
        }

    }

    /**
     * Sets up the instantCard card and associated handler, {@link InstantCardTask most of the work
     * is done in the asynctask associated with the runnable} {@link InstantCard}. Info is placed in
     * an array of type object in following order <p> 0: timeHero </p> 1: stopHero <p> This method
     * will only be called if the user is within 60 metres of a stop. </p>
     */
    private void setupInstantCard() {
        if (closest.getProvider().equals("IMS Eastney (Departures)")) {
            if (TermDates.isItAHoliday() || TermDates.isItTheWeekend()) {
                return;
            }
        }
        ArrayList<Integer> arrayList = databaseHelper.getTimesForArray("[" + closest.getProvider() + "]");
        if (arrayList == null) {
            Snackbar snackbar = Snackbar.make(layout, "Error displaying the closest stop card", Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_dark));
            snackbar.show();
            FirebaseCrash.report(new Throwable("No result was returned by DatabaseHelper to set up the Instant Card"));
            return;
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
        isInstantCardDisplayed = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        adapter.noConnection();
    }

    /**
     * Removes the instantCard card and disables any runnables associated with it
     */
    private void removeInstantCard() {
        if (isInstantCardDisplayed) {
            adapter.removeInstant();
            handler.removeCallbacks(instantCard);
            isInstantCardDisplayed = false;
        }

    }

    /**
     * Changes stopToShow allowing for the home card to be changed according to the new home stop
     * selected. It removes the callbacks for the runnable and restarts it so that it may update
     * immediately
     */
    public void changeHomeCard() {
        SharedPreferences currentHomeStopPref = getActivity().getSharedPreferences(getString(R.string.preferences_home_bus_stop_key), 0);
        stopToShow = currentHomeStopPref.getInt(getString(R.string.preferences_home_bus_stop_key), DEFAULT_VALUE);
        stopTimes = databaseHelper.getTimesForArray(stopToShow);
        if (homeRunnable != null) {
            handler.removeCallbacks(homeRunnable);
            handler.post(homeRunnable);
        } else {
            updateTimeHero();
        }
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onStop() {
        if (googleApiClient != null) {
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Nullifies callbacks for the handler
        handler.removeCallbacksAndMessages(null);
        if (call != null) {
            call.cancel();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        unbinder.unbind();
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);

    }

    /**
     * This class manipulates the home card to act as a countdown until the next bus. Only one
     * instance is needed so it is static as to prevent resource leaking
     */
    private static class HomeCardTask implements Runnable {

        private final WeakReference<TopFragment> weakReference;

        HomeCardTask(TopFragment parent) {
            weakReference = new WeakReference<>(parent);
        }

        @Override
        public void run() {
            if (weakReference != null) {
                final TopFragment topFragment = weakReference.get();
                if (topFragment != null) {
                    homeStopRunnableTask(topFragment);
                }
            }
        }

        /**
         * Repeating every 2 seconds, it updates the home card with the countdown. It packs its
         * information into an array which is sent to the adapter
         *
         * @param parent The top fragment instance
         */
        void homeStopRunnableTask(TopFragment parent) {
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
     * Unlike the {@link HomeCardTask} it is not static as to be able to create new instances
     * of the asynctask so that the same one is not completed twice which will throw an exception.
     */
    private class InstantCard implements Runnable {

        WeakReference<TopFragment> parent;
        ArrayList<Integer> list;


        InstantCard(TopFragment topFragment, ArrayList<Integer> list) {
            parent = new WeakReference<>(topFragment);
            this.list = list;
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
    private class InstantCardTask extends AsyncTask<Void, Void, ArrayList<Object>> {

        TopFragment fragment;
        ArrayList<Integer> array;

        /**
         * Creates the task
         *
         * @param parent The topFragment instance
         * @param array  An array of stop times to be searched
         */
        InstantCardTask(WeakReference<TopFragment> parent, ArrayList<Integer> array) {
            fragment = parent.get();
            this.array = array;
        }

        @Override
        protected ArrayList<Object> doInBackground(Void... params) {
            ArrayList<Object> list = new ArrayList<Object>();
            String time = fragment.getTimeToStop(array);
            list.add(time);
            if (fragment.closest.getProvider().equals("IMS Eastney (Departures)")){
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

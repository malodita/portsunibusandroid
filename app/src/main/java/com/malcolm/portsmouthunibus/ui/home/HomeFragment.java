package com.malcolm.portsmouthunibus.ui.home;


import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.EpoxyRecyclerView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.App;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.ui.HomeActivity;
import com.malcolm.unibusutilities.entity.DirectionsApi;
import com.malcolm.unibusutilities.helper.TermDateUtils;
import com.malcolm.unibusutilities.model.StopAndTime;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = "Home Fragment";
    private static final int DEFAULT_VALUE = -2;
    private static final String HOMEKEY = "home";
    private static final String MAPKEY = "map";
    private static final String INSTANTKEY = "instant";
    private final HomeEpoxyController controller = new HomeEpoxyController();
    private final HashMap<String, Object> controllerItems = new HashMap<>(3);
    private final Observer<StopAndTime> instantObserver = makeInstantObserver();
    @BindView(R.id.recycler_view)
    EpoxyRecyclerView recyclerView;
    @BindView(R.id.top_fragment_coordinator)
    CoordinatorLayout layout;
    /**
     * When conducting database searches with this value, always +1 to make it work correctly due to
     * the manual get method starting from 0 in the database (Representing eastney)
     */
    private int stopToShow;
    private int currentNightMode;
    private Unbinder unbinder;
    private String[] busStops;
    private final Observer<String> homeObserver = makeHomeObserver();
    private SharedPreferences sharedPreferences;
    private HomeViewModel viewModel;
    private Observer<DirectionsApi> mapObserver;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAnalytics.getInstance(getContext());
        currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        busStops = getResources().getStringArray(R.array.bus_stops_home);
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        mapObserver = makeMapObserver();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        stopToShow = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), DEFAULT_VALUE);
        HomeViewModel.Factory factory = new HomeViewModel.Factory(getActivity().getApplication(), stopToShow + 1);
        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setController(controller);
        boolean instant = sharedPreferences.getBoolean(getString(R.string.preferences_instant_card), true);
        viewModel.getCurrentCountdown().observe(this, homeObserver);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            viewModel.getDirectionsLiveData().observe(this, mapObserver);
            if (!TermDateUtils.isBankHoliday() && !TermDateUtils.isWeekendInHoliday() && instant) {
                viewModel.beginInstantFetching();
                viewModel.getInstantCountdown().observe(this, instantObserver);
            }
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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

    private Observer<String> makeHomeObserver(){
        return s -> {
            ArrayList<Object> array = new ArrayList<>();
            if (stopToShow == DEFAULT_VALUE) {
                array.add(false);
                array.add(getString(R.string.error_no_stop_selected));
                array.add(s);
            } else {
                if (Integer.valueOf(s) == -1){
                    array.add(false);
                    array.add(getString(R.string.error_no_stop_selected));
                    array.add(s);
                } else {
                    array.add(true);
                    array.add(busStops[stopToShow]);
                    array.add(s);
                    array.add(TermDateUtils.isBankHoliday());
                    array.add(TermDateUtils.isWeekendInHoliday());
                    array.add(stopToShow);
                }
            }
            controllerItems.put(HOMEKEY, array);
            controller.setData(controllerItems);
        };//Output as one string
    }

    private Observer<DirectionsApi> makeMapObserver(){
        int permission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return directionsApi -> {
            if (directionsApi != null) {
                ArrayList<Object> array = new ArrayList<>();
                array.add(sharedPreferences.getBoolean(getString(R.string.preferences_maps_card), true));
                array.add(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()));
                array.add(isGooglePlayServicesAvailable(getActivity()));
                array.add(permission);
                if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    array.add(0);
                } else {
                    array.add(1);
                }
                if (permission != PackageManager.PERMISSION_GRANTED || !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    controllerItems.put(MAPKEY, array);
                    controller.setData(controllerItems);
                    return;
                }
                array.add(setNightModeLayout());
                array.add(currentNightMode);
                array.add(directionsApi);
                controllerItems.put(MAPKEY, array);
                controller.setData(controllerItems);
            }
        }; //Outputs directly as API response
    }

    private Observer<StopAndTime> makeInstantObserver(){
        return stopAndTime -> {
            ArrayList<Object> array = new ArrayList<>();
            if (stopAndTime != null) {
                String stop = stopAndTime.getStop();
                if (stop.equals("IMS Eastney")) {
                    if (TermDateUtils.isHoliday() || TermDateUtils.isWeekend()) {
                        array.add(false);
                        controllerItems.put(INSTANTKEY, array);
                        controller.setData(controllerItems);
                        return;
                    }
                }
                array.add(true);
                array.add(stop);
                array.add(stopAndTime.getTime());
                controllerItems.put(INSTANTKEY, array);
                controller.setData(controllerItems);
            } else {
                array.add(false);
                controllerItems.put(INSTANTKEY, array);
                controller.setData(controllerItems);
            }
        };
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals(getString(R.string.preferences_maps_card))) {
            if (sharedPreferences.getBoolean(s, true)) {
                if (ActivityCompat.checkSelfPermission(getContext()
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(getContext()
                        , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (!viewModel.getDirectionsLiveData().hasActiveObservers()){
                        viewModel.getDirectionsLiveData().observe(this, mapObserver);
                    }
                }
            } else {
                viewModel.getDirectionsLiveData().removeObserver(mapObserver);
                controllerItems.remove(MAPKEY);
                controller.setData(controllerItems);
            }
        }
        if (s.equals(getString(R.string.preferences_instant_card))){
            if (sharedPreferences.getBoolean(s, true)) {
                if (ActivityCompat.checkSelfPermission(getContext()
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(getContext()
                        , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (!viewModel.getInstantCountdown().hasActiveObservers()) {
                        viewModel.beginInstantFetching();
                        viewModel.getInstantCountdown().observe(this, instantObserver);
                    }
                }
            } else {
                viewModel.getInstantCountdown().removeObserver(instantObserver);
                viewModel.stopInstantUpdates();
                controllerItems.remove(INSTANTKEY);
                controller.setData(controllerItems);
            }
        }
        if (s.equals(getString(R.string.preferences_home_bus_stop))){
            if (sharedPreferences.getInt(s, DEFAULT_VALUE) == DEFAULT_VALUE){
                if (stopToShow != DEFAULT_VALUE){
                    viewModel.updateStopList(DEFAULT_VALUE);
                }
            }
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
        viewModel.updateStopList(stopToShow + 1);
    }

    public CoordinatorLayout getLayout() {
        return layout;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        controller.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            controller.onSaveInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onDestroyView() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        recyclerView.setAdapter(null);
        recyclerView.getRecycledViewPool().clear();
        unbinder.unbind();
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);
        super.onDestroyView();
    }

//Before code removal ~850-900 lines including static classes, after 323 lines!!!
}

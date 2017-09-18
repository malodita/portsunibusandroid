package com.malcolm.portsmouthunibus;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.unibusutilities.BusStops;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Intent.ACTION_VIEW;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapsFragment";
    private static final LatLng UPPERBOUNDS = new LatLng(50.810093, -1.040783);
    private static final LatLng LOWERBOUNDS = new LatLng(50.779265, -1.112208);
    Unbinder unbinder;
    @BindView(R.id.map_container)
    LinearLayout container;
    @BindView(R.id.error_hint)
    TextView errorHint;
    private GoogleMap googleMap;
    private String[] stopList;
    private FirebaseAnalytics firebaseAnalytics;
    private int currentNightMode;
    private SupportMapFragment fragment;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        stopList = getResources().getStringArray(R.array.bus_stops_markers);
        if (!isGooglePlayServicesAvailable(getActivity()) || !onNoNetwork() || !onNoGps()){
            return rootView;
        }
        GoogleMapOptions options = new GoogleMapOptions();
        options.camera(CameraPosition.builder().target(new LatLng(50.795261, -1.093634)).zoom(14).build());
        fragment = SupportMapFragment.newInstance(options);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, fragment, "Map").commit();
        fragment.getMapAsync(this);
        return rootView;
    }

    /**
     * Called if the phone GPS isn't available
     */
    private boolean onNoGps() {
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                errorHint.setText(getText(R.string.error_GPS_off));
                errorHint.setVisibility(View.VISIBLE);
                return false;
            } else {
                return true;
            }
        } else {
            errorHint.setText("This device does not have GPS");
            errorHint.setVisibility(View.VISIBLE);
            return false;
        }
    }

/**
     * Called if the internet connection isn't available
     */
    private boolean onNoNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (activeNetworkInfo != null && !activeNetworkInfo.isConnected()) {
            errorHint.setText(getString(R.string.error_no_connection));
            errorHint.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<LatLng> list = BusStops.makeArrayOfStops();
        for (int i = 0; i < list.size(); i++){
            googleMap.addMarker(new MarkerOptions().position(list.get(i))
                    .title(stopList[i])).setTag(i);
            googleMap.setOnInfoWindowClickListener(this);

        }
        googleMap.setLatLngBoundsForCameraTarget(new LatLngBounds(LOWERBOUNDS, UPPERBOUNDS));
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.night_json));
        } else {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.day_json));
        }
        setUpMarkers(googleMap, BusStops.makeArrayOfStops());
        googleMap.setOnInfoWindowClickListener(this);
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
    }

    /**
     * Sets up the markers from the arraylist of stops
     *
     * @param googleMap
     */
    private void setUpMarkers(GoogleMap googleMap, ArrayList<LatLng> list) {
        for (int i = 0; i < list.size(); i++) {
            googleMap.addMarker(new MarkerOptions().position(list.get(i))
                    .title(stopList[i])).setTag(i);
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnInfoWindowClickListener(this);
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (isPackageInstalled("com.google.android.apps.maps", getContext().getPackageManager())) {
            Uri nav = Uri.parse("geo:0,0?q="
                    + marker.getPosition().latitude + "," + marker.getPosition().longitude +
                    "(" + marker.getTitle() + ")");
            Intent i = new Intent(ACTION_VIEW, nav);
            if (i.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(i);
            }
        } else {
            Uri nav = Uri.parse("geo:" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
            Intent i = new Intent(ACTION_VIEW, nav);
            if (i.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(i);
            }
        }
    }

/*    */
    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks the google play services status.
     * @param activity
     * @return
     */
    private boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                errorHint.setText(getString(R.string.error_play_services_update));
            } else {
                errorHint.setText(getString(R.string.error_play_services_needed));
            }
            errorHint.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        if (googleMap != null){
            googleMap.clear();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            googleMap.setOnMarkerClickListener(null);
            googleMap.setOnInfoWindowClickListener(null);
            try{
                googleMap.setMyLocationEnabled(false);
            } catch(SecurityException e){
                Log.w(TAG, "onDestroy: Security exception with location permission. Ignoring...");
            } finally {
                googleMap = null;
            }
        }
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        unbinder.unbind();
        super.onDestroyView();
        refWatcher.watch(this);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.firebase_property_stop_id), marker.getTitle());
        firebaseAnalytics.logEvent(getString(R.string.firebase_event_map_marker_click), bundle);
        return false;
    }
}

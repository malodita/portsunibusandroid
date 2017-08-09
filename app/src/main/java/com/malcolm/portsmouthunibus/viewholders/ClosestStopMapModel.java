package com.malcolm.portsmouthunibus.viewholders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.PolyUtil;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.utilities.BusStopUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * The model for the map card
 */

public class ClosestStopMapModel extends EpoxyModelWithHolder<ClosestStopMapModel.MapViewHolder> implements OnMapReadyCallback {
    @EpoxyAttribute
    String timeHero;
    @EpoxyAttribute
    String stopHero;
    @EpoxyAttribute
    int playServicesError;
    @EpoxyAttribute
    int locationPermission;
    @EpoxyAttribute
    int playServices;
    @EpoxyAttribute
    MapStyleOptions mapStyleOptions;
    @EpoxyAttribute
    int nightMode;
    @EpoxyAttribute
    int gps;
    private MapViewHolder holder;
    private String provider;
    private ArrayList<Object> mapObjects;
    private WeakReference<Context> weakContext;
    private GoogleMap googleMap;

    @Override
    protected MapViewHolder createNewHolder() {
        return new MapViewHolder();
    }


    @Override
    public void bind(MapViewHolder holder) {
        super.bind(holder);
        this.holder = holder;
        weakContext = new WeakReference<>(holder.mapView.getContext());
        if (playServices != ConnectionResult.SUCCESS) {
            if (playServicesError == 1) {
                holder.error.setText(R.string.play_services_update);
            } else if (playServicesError == 2) {
                holder.error.setText(R.string.play_services_needed);
            }
            holder.error.setVisibility(View.VISIBLE);
            holder.progressBar.hide();
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            holder.error.setText(R.string.location_permission_hint);
            holder.error.setVisibility(View.VISIBLE);
            holder.progressBar.hide();
        }
        if (gps == 0) {
            holder.error.setText(R.string.error_GPS_off);
            holder.error.setVisibility(View.VISIBLE);
            holder.progressBar.hide();
        }
        if (googleMap != null){
            setUpGoogleMap(googleMap);
        }
    }


    public void noInternet() {
        if (holder != null) {
            holder.error.setText(R.string.error_no_connection);
            holder.error.setVisibility(View.VISIBLE);
            holder.progressBar.hide();
        }
    }


    @Override
    @SuppressWarnings("unchecked") //I know what is being cast because its built into the design!
    public void bind(MapViewHolder holder, List<Object> payloads) {
        super.bind(holder, payloads);
        mapObjects = (ArrayList<Object>) payloads.get(0);
        provider = (String) mapObjects.get(0);
        if (playServices != ConnectionResult.SUCCESS) {
            if (playServicesError == 1) {
                holder.error.setText(R.string.play_services_update);
            } else if (playServicesError == 2) {
                holder.error.setText(R.string.play_services_needed);
            }
            holder.error.setVisibility(View.VISIBLE);
            holder.progressBar.hide();
            return;
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            holder.error.setText(R.string.location_permission_hint);
            holder.error.setVisibility(View.VISIBLE);
            holder.progressBar.hide();
            return;
        }
        if (provider == null) {
            holder.error.setText(R.string.error_not_in_portsmouth);
            holder.error.setVisibility(View.VISIBLE);
            holder.progressBar.hide();
            return;
        }
        if (gps == 0) {
            holder.error.setText(R.string.error_GPS_off);
            holder.error.setVisibility(View.VISIBLE);
            holder.progressBar.hide();
            return;
        }
        setUpMap(holder.mapView, mapObjects);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        setUpGoogleMap(googleMap);
    }

    private void setUpGoogleMap(GoogleMap googleMap){
        final Context context = weakContext.get();
        holder.progressBar.hide();
        holder.layout.setVisibility(View.VISIBLE);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException s) {
            s.printStackTrace();
            return;
        }
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapStyle(mapStyleOptions);
        Double time = (Double) mapObjects.get(1);
        final LatLng targetLocation;
        holder.closestStop.setText(provider);
        if (time == -1) {
            Location location = (Location) mapObjects.get(2);
            targetLocation = new LatLng(location.getLatitude(), location.getLongitude());
            BusStopUtils.drawPolyline(googleMap, nightMode, null, null, targetLocation);
            holder.timeHero.setText(BusStopUtils.setTimeAndDistanceToClosestStop(time));
        } else {
            String polyline = (String) mapObjects.get(2);
            final List<LatLng> decodedList = PolyUtil.decode(polyline);
            BusStopUtils.drawPolyline(googleMap, nightMode, time, decodedList, null);
            holder.timeHero.setText(BusStopUtils.setTimeAndDistanceToClosestStop(time));
            int last = (decodedList.size() - 1);
            targetLocation = new LatLng(decodedList.get(last).latitude, decodedList.get(last).longitude);
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String string = "geo:0,0?q=" + targetLocation.latitude +
                        "," + targetLocation.longitude + "(Nearest Stop)";
                Uri uri = Uri.parse(string);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(i);

            }
        });
        this.googleMap = googleMap;
    }

    private void setUpMap(MapView mapView, @Nullable ArrayList list) {
        if (list == null) {
            return;
        }
        mapView.onCreate(null);
        mapView.getMapAsync(this);
        mapView.onResume();
    }


    @Override
    public void unbind(MapViewHolder holder) {
        if (googleMap != null) {
            googleMap.clear();
            googleMap.setOnMapClickListener(null);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            try {
                googleMap.setMyLocationEnabled(false);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        super.unbind(holder);
    }

    @Override
    public boolean shouldSaveViewState() {
        return true;
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.viewgroup_closest_stop_card;
    }

    public void notInPortsmouth() {
        if (holder != null) {
            holder.error.setText(R.string.error_not_in_portsmouth);
            holder.error.setVisibility(View.VISIBLE);
            holder.progressBar.hide();
        }
    }

    public void noConnection() {
        holder.error.setText(R.string.error_route_display);
        holder.error.setVisibility(View.VISIBLE);
        holder.progressBar.hide();
    }

    static class MapViewHolder extends BaseModel {
        @BindView(R.id.map_view)
        MapView mapView;
        @BindView(R.id.closest_stop_time_hero)
        TextView timeHero;
        @BindView(R.id.closest_stop_stop_hero)
        TextView closestStop;
        @BindView(R.id.closest_stop_error_text)
        TextView error;
        @BindView(R.id.closest_stop_layout)
        LinearLayout layout;
        @BindView(R.id.closest_stop_progress_bar)
        ContentLoadingProgressBar progressBar;

    }
}

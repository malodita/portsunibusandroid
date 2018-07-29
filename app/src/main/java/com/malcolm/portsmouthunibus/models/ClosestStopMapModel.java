package com.malcolm.portsmouthunibus.models;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
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
import com.malcolm.portsmouthunibus.utilities.TimeUtils;
import com.malcolm.unibusutilities.entity.DirectionsApi;

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
    @EpoxyAttribute
    DirectionsApi directionsApi;

    private MapViewHolder holder;
    private GoogleMap googleMap;

    @Override
    protected MapViewHolder createNewHolder() {
        return new MapViewHolder();
    }


    @Override
    public void bind(@NonNull MapViewHolder holder) {
        super.bind(holder);
        this.holder = holder;
        if (playServices != ConnectionResult.SUCCESS) {
            if (playServicesError == 1) {
                holder.error.setText(R.string.error_play_services_update);
            } else if (playServicesError == 2) {
                holder.error.setText(R.string.error_play_services_needed);
            }
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
            return;
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            holder.error.setText(R.string.location_permission_hint);
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
            return;
        }
        if (gps == 0) {
            holder.error.setText(R.string.error_GPS_off);
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
            return;
        }
        switch (directionsApi.getErrorCode()) {
            case DirectionsApi.ERROR_NO_RESPONSE:
                noConnection();
                break;
            case DirectionsApi.ERROR_NOT_OK_RESPONSE:
                noConnection();
                break;
            case DirectionsApi.ERROR_NO_NETWORK:
                noInternet();
                break;
            case DirectionsApi.ERROR_PERMISSION:
                holder.error.setText(R.string.location_permission_hint);
                holder.error.setVisibility(View.VISIBLE);
                break;
            case DirectionsApi.ERROR_NOT_CLOSE:
                notInPortsmouth();
                break;
                default:
                    holder.layout.setVisibility(View.VISIBLE);
                    holder.mapView.onCreate(null);
                    if (googleMap != null) {
                        setUpGoogleMap(googleMap);
                    } else {
                        holder.mapView.getMapAsync(this);
                    }
                    holder.mapView.onResume();
                    break;
        }
    }

    private void noConnection() {
        if (holder != null) {
            holder.error.setText(R.string.error_route_display);
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
        }
    }

    private void noInternet() {
        if (holder != null) {
            holder.error.setText(R.string.error_no_connection);
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
        }
    }

    private void notInPortsmouth() {
        if (holder != null) {
            holder.error.setText(R.string.error_not_in_portsmouth);
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
        }
    }

    private void setUpGoogleMap(GoogleMap googleMap){
        holder.layout.setVisibility(View.VISIBLE);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapStyle(mapStyleOptions);
        final LatLng targetLocation;
        holder.closestStop.setText(directionsApi.getEndLocation().getProvider());
        if (directionsApi.isNearStop()) {
            Location end = directionsApi.getEndLocation();
            Location start = directionsApi.getStartLocation();
            targetLocation = new LatLng(end.getLatitude(), end.getLongitude());
            TimeUtils.markStop(googleMap, null, null, targetLocation);
            start.distanceTo(end);
            holder.timeHero.setText(TimeUtils.setTimeAndDistanceToClosestStop(-1.0, start.distanceTo(end)));
            holder.navigateButton.setVisibility(View.GONE);
        } else {
            String summary = directionsApi.getRoutes().get(0).getSummary(); //Not currently used. Gets route summary
            String polyline = directionsApi.getRoutes().get(0).getOverviewPolyline().getPoints();
            double time = directionsApi.getRoutes().get(0).getLegs().get(0).getDuration().getValue();
            final List<LatLng> decodedList = PolyUtil.decode(polyline);
            TimeUtils.markStop(googleMap, time, decodedList, null);
            holder.timeHero.setText(TimeUtils.setTimeAndDistanceToClosestStop(time, -1));
            int last = (decodedList.size() - 1);
            targetLocation = new LatLng(decodedList.get(last).latitude, decodedList.get(last).longitude);

            holder.navigateButton.setOnClickListener(l -> {
                String string = "geo:0,0?q=" + targetLocation.latitude +
                        "," + targetLocation.longitude + "(Nearest Stop)";
                Uri uri = Uri.parse(string);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                holder.timeHero.getContext().getApplicationContext().startActivity(i);
            });
        }
        this.googleMap = googleMap;
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        setUpGoogleMap(googleMap);
    }


    @Override
    public void unbind(@NonNull MapViewHolder holder) {
        if (googleMap != null) {
            googleMap.clear();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            holder.mapView.onPause();
            holder.mapView.onStop();
            holder.mapView.onDestroy();
        }
        super.unbind(holder);
    }

    @Override
    public boolean shouldSaveViewState() {
        return false;
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.viewgroup_closest_stop_card;
    }


    static class MapViewHolder extends BaseHolder {
        @BindView(R.id.map_view)
        MapView mapView;
        @BindView(R.id.closest_stop_time_hero)
        TextView timeHero;
        @BindView(R.id.closest_stop_stop_hero)
        TextView closestStop;
        @BindView(R.id.closest_stop_error_text)
        TextView error;
        @BindView(R.id.closest_stop_layout)
        ConstraintLayout layout;
        @BindView(R.id.closest_stop_button_nav)
        Button navigateButton;
    }
}

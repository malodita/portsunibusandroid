package com.malcolm.portsmouthunibus.adapters;

import android.support.annotation.Nullable;

import com.airbnb.epoxy.EpoxyAdapter;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.malcolm.portsmouthunibus.viewholders.ClosestStopMapModel;
import com.malcolm.portsmouthunibus.viewholders.ClosestStopMapModel_;
import com.malcolm.portsmouthunibus.viewholders.ClosestStopModel;
import com.malcolm.portsmouthunibus.viewholders.HomeStopModel;
import com.malcolm.portsmouthunibus.viewholders.HomeStopModel_;

import java.util.ArrayList;

/**
 * This class represents the adapter responsible for displaying and updating the recyclerview in
 * TopFragment
 */

public class TopFragmentAdapter extends EpoxyAdapter {
    private static final String TAG = "TopFragmentAdapter";
    private final HomeStopModel home;
    private final ClosestStopMapModel map;
    private final ClosestStopModel instant = new ClosestStopModel();

    public TopFragmentAdapter(ArrayList<Object> homeCard, @Nullable ArrayList<Object> mapCard) {
        enableDiffing();
        if (homeCard != null && !Boolean.parseBoolean(homeCard.get(0).toString())) {
            home = new HomeStopModel_()
                    .visibility(false)
                    .stopHero(homeCard.get(1).toString());
        } else {
            home = new HomeStopModel_().visibility(true)
                    .stopHero(homeCard.get(1).toString())
                    .timeHero(homeCard.get(2).toString());
        }
        int playServices = (int) mapCard.get(0);
        int resolvable = (int) mapCard.get(1);
        int location = (int) mapCard.get(2);
        int gps = (int) mapCard.get(3);
        if (mapCard.size() > 4) {
            MapStyleOptions options = (MapStyleOptions) mapCard.get(4);
            int nightMode = (int) mapCard.get(5);
            map = new ClosestStopMapModel_()
                    .playServices(playServices)
                    .playServicesError(resolvable)
                    .locationPermission(location)
                    .gps(gps)
                    .mapStyleOptions(options)
                    .nightMode(nightMode);
        } else {
            map = new ClosestStopMapModel_()
                    .playServices(playServices)
                    .playServicesError(resolvable)
                    .locationPermission(location)
                    .gps(gps);
        }
        addModels(home, map);
    }

    public void invalidateHomeCard(){
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(true);
        notifyModelChanged(home, list);
    }

    public void locationReady(ArrayList<Object> list){
        notifyModelChanged(map, list);
    }

    public void updateTimeHero(ArrayList newInfo){
        notifyModelChanged(home, newInfo);
    }


    public void instantCard(ArrayList<Object> array){
        //Check to ensure that reinsert doesnt happen if already displayed
        if (models.size() < 3) {
            insertModelBefore(instant, map);
        } else {
            notifyModelChanged(instant, array);
        }
    }

    public void removeInstant(){
        removeModel(instant);
    }

    public void noInternet() {
        map.noInternet();
    }

    public void notInPortsmouth() {
        map.notInPortsmouth();
    }

    public void noConnection() {
        map.noConnection();
    }

    public void closeToStop(ArrayList info) {
        notifyModelChanged(map, info);
    }
}

package com.malcolm.portsmouthunibus.adapters;

import com.airbnb.epoxy.EpoxyAdapter;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.malcolm.portsmouthunibus.viewholders.ClosestStopMapModel;
import com.malcolm.portsmouthunibus.viewholders.ClosestStopMapModel_;
import com.malcolm.portsmouthunibus.viewholders.ClosestStopModel;
import com.malcolm.portsmouthunibus.viewholders.HomeStopModel;
import com.malcolm.portsmouthunibus.viewholders.HomeStopModel_;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the adapter responsible for displaying and updating the recyclerview in
 * TopFragment
 */

public class TopFragmentAdapter extends EpoxyAdapter {
    private static final String TAG = "TopFragmentAdapter";
    private final HomeStopModel home;
    private final ClosestStopMapModel map;
    private final ClosestStopModel instant = new ClosestStopModel();

    public TopFragmentAdapter(List<Object> homeCard, List<Object> mapCard) {
        enableDiffing();
        if (homeCard != null && !Boolean.parseBoolean(homeCard.get(0).toString())) {
            home = new HomeStopModel_()
                    .visibility(false)
                    .stopHero(homeCard.get(1).toString());
        } else {
            home = new HomeStopModel_().visibility(true)
                    .stopHero(homeCard.get(1).toString())
                    .timeHero(homeCard.get(2).toString())
                    .isHoliday(Boolean.parseBoolean(homeCard.get(3).toString()))
                    .isWeekendInHoliday(Boolean.parseBoolean(homeCard.get(4).toString()));
        }
        boolean mapsCardAllowed = (Boolean) mapCard.get(0);
        int playServices = (int) mapCard.get(1);
        int resolvable = (int) mapCard.get(2);
        int location = (int) mapCard.get(3);
        int gps = (int) mapCard.get(4);
        if (mapCard.size() > 5) {
            MapStyleOptions options = (MapStyleOptions) mapCard.get(5);
            int nightMode = (int) mapCard.get(6);
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
        if (mapsCardAllowed) {
            addModels(home, map);
        } else {
            addModel(home);
        }
    }

    public void invalidateHomeCard() {
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(true);
        notifyModelChanged(home, list);
    }

    public void locationReady(List<Object> list) {
        if (!map.isShown()) {
            addModel(map);
        }
        notifyModelChanged(map, list);
    }

    public void updateTimeHero(List newInfo) {
        notifyModelChanged(home, newInfo);
    }


    public void instantCard(List<Object> array) {
        //Check to see if instant card is already added
        if (!models.contains(instant)) {
            //If it is not added, check to see where to insert
            if (map.isShown()) {
                insertModelAfter(instant, home);
            } else {
                addModel(instant);
            }
        } else {
            notifyModelChanged(instant, array);
        }
    }

    public void removeInstant() {
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

    public void closeToStop(List info) {
        notifyModelChanged(map, info);
    }

    public void hideMapsCard() {
        if (map.isShown()) {
            removeModel(map);
        }
    }

    public void weekendInHoliday() {
        home.weekendInHoliday();
    }

    public void bankHoliday() {
        home.bankHoliday();
    }
}

package com.malcolm.portsmouthunibus.ui.home;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.TypedEpoxyController;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.malcolm.portsmouthunibus.models.ClosestStopMapModel_;
import com.malcolm.portsmouthunibus.models.ClosestStopModel_;
import com.malcolm.portsmouthunibus.models.HomeStopModel_;
import com.malcolm.unibusutilities.entity.DirectionsApi;

import java.util.HashMap;
import java.util.List;
 class HomeEpoxyController extends TypedEpoxyController<HashMap<String, Object>> {

    @AutoModel HomeStopModel_ homeModel;
    @AutoModel ClosestStopMapModel_ mapModel;
    @AutoModel ClosestStopModel_ closestModel;

    private static final String TAG = "HomeFragmentController";
    private static final String HOMEKEY = "home";
    private static final String MAPKEY = "map";
    private static final String INSTANTKEY = "instant";

    @SuppressWarnings({"unchecked"})
    @Override
    protected void buildModels(HashMap<String, Object> data) {
        if (data.get(HOMEKEY) != null){
            List<Object> home = (List<Object>) data.get(HOMEKEY);
            if ((boolean) home.get(0)) {
                homeModel.visibility(true)
                        .stopHero((String) home.get(1))
                        .timeHero((String) home.get(2))
                        .isHoliday((boolean) home.get(3))
                        .isWeekendInHoliday((boolean) home.get(4));
                if (home.get(5) != null){
                    homeModel.stopNumber((int) home.get(5));
                }
            } else {
                homeModel.visibility(false)
                        .stopHero((String) home.get(1));
            }
            homeModel.addTo(this);
        }
        if (data.get(INSTANTKEY) != null){
            List<Object> closest = (List<Object>) data.get(INSTANTKEY);
            boolean shouldDisplay = (boolean) closest.get(0);
            if (shouldDisplay){
                closestModel.stopHero((String) closest.get(1));
                closestModel.timeHero((String) closest.get(2));
                closestModel.addTo(this);
            }
        }
        if (data.get(MAPKEY) != null){
            List<Object> map = (List<Object>) data.get(MAPKEY);
            boolean mapsCardAllowed = (Boolean) map.get(0);
            if (mapsCardAllowed){
                mapModel.playServices((int) map.get(1))
                        .playServicesError((int) map.get(2))
                        .locationPermission((int) map.get(3))
                        .gps((int) map.get(4));
                if (map.size() > 5){
                    mapModel.mapStyleOptions((MapStyleOptions) map.get(5))
                            .nightMode((int) map.get(6))
                            .directionsApi((DirectionsApi) map.get(7));
                }
                mapModel.addTo(this);
            }
        }
    }
}

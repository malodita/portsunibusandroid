package com.malcolm.portsmouthunibus.utilities;


import android.content.Context;
import android.content.res.Configuration;

import com.malcolm.portsmouthunibus.R;
import com.malcolm.unibusutilities.helper.BusStopUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public final class ImageGenerator {


    public static RequestCreator generateKeyImage(Context context, int stop) {
        int nightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            switch (stop) {
                case 0:
                    return Picasso.with(context).load(R.drawable.langstone_night);
                case 1:
                    return Picasso.with(context).load(R.drawable.locksway_night);
                case 2:
                    return Picasso.with(context).load(R.drawable.adj_lidl_night);
                case 3:
                    return Picasso.with(context).load(R.drawable.fawcett_night);
                case 4:
                    return Picasso.with(context).load(R.drawable.ibis_night);
                default:
                    return Picasso.with(context).load(R.drawable.generic_night);
            }
        } else {
            switch (stop) {
                case 0:
                    return Picasso.with(context).load(R.drawable.langstone);
                case 1:
                    return Picasso.with(context).load(R.drawable.locksway_key);
                case 2:
                    return Picasso.with(context).load(R.drawable.lidl_key);
                case 3:
                    return Picasso.with(context).load(R.drawable.fawcett_key);
                case 4:
                    return Picasso.with(context).load(R.drawable.winston_key);
                default:
                    return Picasso.with(context).load(R.drawable.generic_day);
            }
        }
    }

    public static RequestCreator generateStopImage(Context context, String stop) {
        switch (stop) {
            case BusStopUtils.LANGSTONESTOPTAG:
                return Picasso.with(context).load(R.drawable.langstone);
            case BusStopUtils.LOCKSWAYSTOPTAG:
                return Picasso.with(context).load(R.drawable.locksway_key);
            case BusStopUtils.POMPEYSTOPTAG:
                return Picasso.with(context).load(R.drawable.lidl_key);
            case BusStopUtils.BRIDGESTOPTAG:
                return Picasso.with(context).load(R.drawable.fawcett_key);
            case BusStopUtils.WINSTONSTOPTAG:
                return Picasso.with(context).load(R.drawable.winston_key);
            case BusStopUtils.UNIONSTOPTAG:
                return Picasso.with(context).load(R.drawable.nuffield_key);
            case BusStopUtils.MILTONSTOPTAG:
                return Picasso.with(context).load(R.drawable.milton_key);
            case BusStopUtils.IMSSTOPTAG:
                return Picasso.with(context).load(R.drawable.eastney);
            default:
                return Picasso.with(context).load(R.drawable.generic_day);
        }
    }

    public static RequestCreator generateImage(Context context, String provider) {
        int nightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            switch (provider) {
                case BusStopUtils.ADJLIDLTAG:
                    return Picasso.with(context).load(R.drawable.adj_lidl_night);
                case BusStopUtils.EASTNEYTAG:
                    return Picasso.with(context).load(R.drawable.eastney_night);
                case BusStopUtils.FAWCETTTAG:
                    return Picasso.with(context).load(R.drawable.fawcett_night);
                case BusStopUtils.FRATTONTAG:
                    return Picasso.with(context).load(R.drawable.fratton_night);
                case BusStopUtils.IBISTAG:
                    return Picasso.with(context).load(R.drawable.ibis_night);
                case BusStopUtils.LANGSTONETAG:
                    return Picasso.with(context).load(R.drawable.langstone_night);
                case BusStopUtils.LAWTAG:
                    return Picasso.with(context).load(R.drawable.law_night);
                case BusStopUtils.LOCKSWAYTAG:
                    return Picasso.with(context).load(R.drawable.locksway_night);
                case BusStopUtils.MILTONTAG:
                    return Picasso.with(context).load(R.drawable.milton_night);
                case BusStopUtils.NUFFIELDTAG:
                    return Picasso.with(context).load(R.drawable.nuffield_night);
                case BusStopUtils.OPPLIDLTAG:
                    return Picasso.with(context).load(R.drawable.opp_lidl_night);
                default:
                    return Picasso.with(context).load(R.drawable.generic_night);
            }
        } else {
            switch (provider) {
                case BusStopUtils.ADJLIDLTAG:
                    return Picasso.with(context).load(R.drawable.adj_lidl);
                case BusStopUtils.EASTNEYTAG:
                    return Picasso.with(context).load(R.drawable.eastney);
                case BusStopUtils.FAWCETTTAG:
                    return Picasso.with(context).load(R.drawable.fawcett);
                case BusStopUtils.FRATTONTAG:
                    return Picasso.with(context).load(R.drawable.fratton);
                case BusStopUtils.IBISTAG:
                    return Picasso.with(context).load(R.drawable.ibis);
                case BusStopUtils.LANGSTONETAG:
                    return Picasso.with(context).load(R.drawable.langstone);
                case BusStopUtils.LAWTAG:
                    return Picasso.with(context).load(R.drawable.law);
                case BusStopUtils.LOCKSWAYTAG:
                    return Picasso.with(context).load(R.drawable.locksway);
                case BusStopUtils.MILTONTAG:
                    return Picasso.with(context).load(R.drawable.milton);
                case BusStopUtils.NUFFIELDTAG:
                    return Picasso.with(context).load(R.drawable.nuffield);
                case BusStopUtils.OPPLIDLTAG:
                    return Picasso.with(context).load(R.drawable.opp_lidl);
                default:
                    return Picasso.with(context).load(R.drawable.generic_day);
            }
        }
    }

    public static RequestCreator generateImage(Context context, int stop) {
        int nightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            switch (stop) {
                case 1:
                    return Picasso.with(context).load(R.drawable.eastney_night);
                case 2:
                    return Picasso.with(context).load(R.drawable.langstone_night);
                case 3:
                    return Picasso.with(context).load(R.drawable.locksway_night);
                case 4:
                    return Picasso.with(context).load(R.drawable.adj_lidl_night);
                case 5:
                    return Picasso.with(context).load(R.drawable.fawcett_night);
                case 6:
                    return Picasso.with(context).load(R.drawable.ibis_night);
                case 8:
                    return Picasso.with(context).load(R.drawable.nuffield_night);
                case 9:
                    return Picasso.with(context).load(R.drawable.law_night);
                case 10:
                    return Picasso.with(context).load(R.drawable.fratton_night);
                case 11:
                    return Picasso.with(context).load(R.drawable.opp_lidl_night);
                case 12:
                    return Picasso.with(context).load(R.drawable.milton_night);
                default:
                    return Picasso.with(context).load(R.drawable.generic_night);
            }
        } else {
            switch (stop) {
                case 1:
                    return Picasso.with(context).load(R.drawable.eastney);
                case 2:
                    return Picasso.with(context).load(R.drawable.langstone);
                case 3:
                    return Picasso.with(context).load(R.drawable.locksway);
                case 4:
                    return Picasso.with(context).load(R.drawable.adj_lidl);
                case 5:
                    return Picasso.with(context).load(R.drawable.fawcett);
                case 6:
                    return Picasso.with(context).load(R.drawable.ibis);
                case 8:
                    return Picasso.with(context).load(R.drawable.nuffield);
                case 9:
                    return Picasso.with(context).load(R.drawable.law);
                case 10:
                    return Picasso.with(context).load(R.drawable.fratton);
                case 11:
                    return Picasso.with(context).load(R.drawable.opp_lidl);
                case 12:
                    return Picasso.with(context).load(R.drawable.milton);
                default:
                    return Picasso.with(context).load(R.drawable.generic_day);
            }
        }
    }


}

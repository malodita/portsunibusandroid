package com.malcolm.portsmouthunibus.utilities;


import android.content.Context;
import android.content.res.Configuration;

import com.malcolm.portsmouthunibus.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public final class ImageGenerator {

    public static RequestCreator generateImage(Context context, int stop){
        int nightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES){
            switch (stop){
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
            switch (stop){
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

package com.malcolm.portsmouthunibus.utilities;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.malcolm.portsmouthunibus.R;

public final class ImageGenerator {

    public static Drawable generateImage(Context context, int stop){
        int nightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        Drawable drawable;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES){
            drawable = chooseNightPicture(context, stop);
        } else {
            drawable = chooseDayPicture(context, stop);
        }
        if (drawable != null){
            return drawable;
        } else {
            if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
                return ContextCompat.getDrawable(context, R.drawable.generic_night);
            } else {
               return  ContextCompat.getDrawable(context,R.drawable.generic_day);
            }
        }
    }

    private static Drawable chooseNightPicture(Context context, int stop){
        switch (stop){
            case 1:
                return ContextCompat.getDrawable(context,R.drawable.eastney_night);
            case 2:
                return ContextCompat.getDrawable(context, R.drawable.langstone_night);
            case 3:
                return ContextCompat.getDrawable(context, R.drawable.locksway_night);
            case 4:
                return ContextCompat.getDrawable(context, R.drawable.adj_lidl_night);
            case 5:
                return ContextCompat.getDrawable(context, R.drawable.fawcett_night);
            case 6:
                return ContextCompat.getDrawable(context, R.drawable.ibis_night);
            case 8:
                return ContextCompat.getDrawable(context, R.drawable.nuffield_night);
            case 9:
                return ContextCompat.getDrawable(context, R.drawable.law_night);
            case 10:
                return ContextCompat.getDrawable(context, R.drawable.fratton_night);
            case 11:
                return ContextCompat.getDrawable(context, R.drawable.opp_lidl_night);
            case 12:
                return ContextCompat.getDrawable(context, R.drawable.milton_night);
            default:
                return ContextCompat.getDrawable(context, R.drawable.generic_night);
        }
    }

    private static Drawable chooseDayPicture(Context context, int stop){
        switch (stop){
            case 1:
                return ContextCompat.getDrawable(context,R.drawable.eastney);
            case 2:
                return ContextCompat.getDrawable(context, R.drawable.langstone);
            case 3:
                return ContextCompat.getDrawable(context, R.drawable.locksway);
            case 4:
                return ContextCompat.getDrawable(context, R.drawable.adj_lidl);
            case 5:
                return ContextCompat.getDrawable(context, R.drawable.fawcett);
            case 6:
                return ContextCompat.getDrawable(context, R.drawable.ibis);
            case 8:
                return ContextCompat.getDrawable(context, R.drawable.nuffield);
            case 9:
                return ContextCompat.getDrawable(context, R.drawable.law);
            case 10:
                return ContextCompat.getDrawable(context, R.drawable.fratton);
            case 11:
                return ContextCompat.getDrawable(context, R.drawable.opp_lidl);
            case 12:
                return ContextCompat.getDrawable(context, R.drawable.milton);
            default:
                return ContextCompat.getDrawable(context, R.drawable.generic_day);
        }
    }

}

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
        String item;
        String[] stops = context.getResources().getStringArray(R.array.bus_stops_spinner);
        if (nightMode == Configuration.UI_MODE_NIGHT_YES){
            item = "R.drawable." + stops[stop] + "_night";
        } else {
            item = "R.drawable." + stops[stop];
        }
        return ContextCompat.getDrawable(context, context.getResources().getIdentifier(item, "drawable", context.getPackageName()));
    }

}

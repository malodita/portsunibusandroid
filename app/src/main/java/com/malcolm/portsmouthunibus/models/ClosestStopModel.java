package com.malcolm.portsmouthunibus.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.utilities.ImageGenerator;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindString;
import butterknife.BindView;

/**
 * The model for the closest stop aka InstantCard
 */

public class ClosestStopModel extends EpoxyModelWithHolder<ClosestStopModel.ClosestStopHolder> {

    @EpoxyAttribute
    String timeHero;
    @EpoxyAttribute
    String stopHero;


    @Override
    protected int getDefaultLayout() {
        return R.layout.viewgroup_closest_stop_departure;
    }

    @Override
    public void bind(@NonNull ClosestStopHolder holder) {
        holder.stopHero.setText(String.format(holder.nextDepartureStop, stopHero));
        if (Integer.valueOf(timeHero) < 60){
            ImageGenerator.generateImage(holder.timeHero.getContext(), stopHero)
                    .into(holder.image);
            holder.error.setVisibility(View.GONE);
            holder.layout.setVisibility(View.VISIBLE);
            switch(Integer.valueOf(timeHero)){
                case 0:
                    holder.timeHero.setText(R.string.arrival_now);
                    break;
                case 1:
                    holder.timeHero.setText(String.format(holder.nextDeparture, timeHero, holder.oneMinute));
                    break;
                default:
                    holder.timeHero.setText(String.format(holder.nextDeparture, timeHero, holder.minutes));
            }
        } else if (Integer.valueOf(timeHero) == Integer.MAX_VALUE) {
            holder.image.setImageDrawable(null);
            holder.error.setText(holder.stringError);
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
        } else {
            ImageGenerator.generateImage(holder.timeHero.getContext(), stopHero)
                    .into(holder.image);
            holder.error.setVisibility(View.GONE);
            holder.layout.setVisibility(View.VISIBLE);
            holder.timeHero.setText(R.string.time_hour_plus_text);
        }
        super.bind(holder);
    }



    @Override
    public boolean shouldSaveViewState() {
        return false;
    }

    @Override
    protected ClosestStopHolder createNewHolder() {
        return new ClosestStopHolder();
    }

    static class ClosestStopHolder extends BaseHolder {
        @BindView(R.id.mini_time_hero)
        TextView timeHero;
        @BindView(R.id.mini_stop_hero)
        TextView stopHero;
        @BindView(R.id.mini_title)
        TextView title;
        @BindView(R.id.mini_error_text)
        TextView error;
        @BindView(R.id.mini_nearest_stop_image)
        ImageView image;
        @BindView(R.id.instant_stop_layout)
        ConstraintLayout layout;

        @BindString(R.string.one_minute)
        String oneMinute;
        @BindString(R.string.minutes)
        String minutes;
        @BindString(R.string.stop_next_departure)
        String nextDeparture;
        @BindString(R.string.home_closest_stop_next_departure_from)
        String nextDepartureStop;
        @BindString(R.string.error_no_buses_from_here)
        String stringError;


    }
}

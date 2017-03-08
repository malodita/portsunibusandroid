package com.malcolm.portsmouthunibus.viewholders;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.malcolm.portsmouthunibus.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Malcolm on 28/01/2017.
 */

public class ClosestStopModel extends EpoxyModelWithHolder<ClosestStopModel.ClosestStopHolder> {

    @Override
    protected int getDefaultLayout() {
        return R.layout.viewgroup_closest_stop_departure;
    }

    @Override
    public void bind(final ClosestStopHolder holder, List<Object> payloads) {
        ArrayList<String> array = (ArrayList<String>) payloads.get(0);
        String timeHero = array.get(0);
        String stopHero = array.get(1);
        holder.stopHero.setText(stopHero);
        if (Integer.valueOf(timeHero) < 60){
            switch(Integer.valueOf(timeHero)){
                case 0:
                    holder.minutes.setVisibility(View.GONE);
                    holder.timeHero.setTextSize(34);
                    holder.timeHero.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.timeHero.setText(R.string.arrival_now);
                    break;
                case 1:
                    holder.timeHero.setText(timeHero);
                    holder.minutes.setText(R.string.one_minute);
                    break;
                default:
                    holder.minutes.setText(R.string.minutes);
                    holder.minutes.setVisibility(View.VISIBLE);
                    holder.timeHero.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                    holder.timeHero.setTextSize(TypedValue.COMPLEX_UNIT_SP, 84);
                    holder.timeHero.setText(timeHero);
            }
        } else {
            holder.minutes.setVisibility(View.VISIBLE);
            holder.timeHero.setText(R.string.hour_plus);

        }
    }

    @Override
    public boolean shouldSaveViewState() {
        return true;
    }

    @Override
    protected ClosestStopHolder createNewHolder() {
        return new ClosestStopHolder();
    }

    static class ClosestStopHolder extends BaseModel {
        @BindView(R.id.mini_time_hero)
        TextView timeHero;
        @BindView(R.id.mini_time_hero_minutes)
        TextView minutes;
        @BindView(R.id.mini_stop_hero) TextView stopHero;


    }
}

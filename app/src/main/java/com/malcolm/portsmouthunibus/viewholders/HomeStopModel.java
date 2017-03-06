package com.malcolm.portsmouthunibus.viewholders;

import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.malcolm.portsmouthunibus.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Malcolm on 28/01/2017.
 */

public class HomeStopModel extends EpoxyModelWithHolder<HomeStopModel.HomeStopHolder> {
    private static final String TAG = "HomeStopModel";
    @EpoxyAttribute
    String timeHero;
    @EpoxyAttribute
    String stopHero;
    @EpoxyAttribute
    Boolean visibility;



    @Override
    public void bind(final HomeStopHolder holder) {
        super.bind(holder);
        if (!visibility) {
            holder.intro.setVisibility(View.GONE);
            holder.timeHero.setVisibility(View.GONE);
            holder.minutes.setVisibility(View.GONE);
            holder.error.setVisibility(View.VISIBLE);
            holder.error.setText(stopHero);
        } else {
            holder.stopHero.setText(stopHero);
            updateViewHolder(holder);
        }
    }

    @Override
    public void bind(final HomeStopHolder holder, List<Object> payloads) {
        ArrayList<Object> array = (ArrayList<Object>) payloads.get(0);
        String payload = array.get(0).toString();
        if (payload.equals(Boolean.TRUE.toString())){
            holder.stopHero.setVisibility(View.GONE);
            holder.intro.setVisibility(View.GONE);
            holder.timeHero.setVisibility(View.GONE);
            holder.minutes.setVisibility(View.GONE);
            holder.error.setVisibility(View.VISIBLE);
            holder.error.setText(R.string.error_no_stop_selected);
            return;
        }
        stopHero = array.get(1).toString();
        timeHero = payload;
        holder.stopHero.setText(stopHero);
        holder.intro.setVisibility(View.VISIBLE);
        holder.timeHero.setVisibility(View.VISIBLE);
        holder.minutes.setVisibility(View.VISIBLE);
        holder.error.setVisibility(View.GONE);
        updateViewHolder(holder, payload);
    }

    private void updateViewHolder(HomeStopHolder holder){
        if (Integer.valueOf(timeHero) < 60) {
            switch (Integer.valueOf(timeHero)) {
                case 0:
                    holder.minutes.setVisibility(View.GONE);
                    holder.timeHero.setTextSize(34);
                    holder.timeHero.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.timeHero.setText(R.string.arrival_now);
                    break;
                case 1:
                    holder.minutes.setText(R.string.one_minute);
                    holder.timeHero.setText(timeHero);
                    break;
                default:
                    holder.minutes.setText(R.string.minutes);
                    holder.timeHero.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                    holder.timeHero.setTextSize(TypedValue.COMPLEX_UNIT_SP, 84);
                    holder.timeHero.setText(timeHero);
                    break;
            }
        } else {
            holder.intro.setVisibility(View.VISIBLE);
            holder.timeHero.setVisibility(View.VISIBLE);
            holder.minutes.setVisibility(View.VISIBLE);
            holder.error.setVisibility(View.GONE);
            holder.timeHero.setText(R.string.hour_plus);
            holder.minutes.setText(R.string.minutes);
        }
    }

    private void updateViewHolder(final HomeStopHolder holder, final String payload) {
        if (holder.stopHero.getVisibility() != View.VISIBLE){
            holder.stopHero.setVisibility(View.VISIBLE);
            holder.error.setVisibility(View.GONE);
            holder.timeHero.setVisibility(View.VISIBLE);
            holder.minutes.setVisibility(View.VISIBLE);
        }
        if (Integer.valueOf(payload) < 60) {
            holder.stopHero.setText(stopHero);
            switch (Integer.valueOf(payload)) {
                case 0:
                    holder.minutes.setVisibility(View.GONE);
                    holder.timeHero.setTextSize(34);
                    holder.timeHero.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.timeHero.setText(R.string.arrival_now);
                    break;
                case 1:
                    holder.timeHero.setText(payload);
                    holder.minutes.setText(R.string.one_minute);
                    break;
                default:
                    holder.intro.setVisibility(View.VISIBLE);
                    holder.timeHero.setVisibility(View.VISIBLE);
                    holder.minutes.setVisibility(View.VISIBLE);
                    holder.error.setVisibility(View.GONE);
                    holder.timeHero.setText(payload);
                    holder.timeHero.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                    holder.timeHero.setTextSize(TypedValue.COMPLEX_UNIT_SP, 84);
                    holder.minutes.setText(R.string.minutes);
            }
        } else {
            holder.intro.setVisibility(View.VISIBLE);
            holder.timeHero.setVisibility(View.VISIBLE);
            holder.minutes.setVisibility(View.VISIBLE);
            holder.error.setVisibility(View.GONE);
            holder.timeHero.setText(R.string.hour_plus);
            holder.minutes.setText(R.string.minutes);
        }
    }

    @Override
    public void unbind(HomeStopHolder holder) {
        super.unbind(holder);
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.viewgroup_home_stop_card;
    }

    @Override
    protected HomeStopHolder createNewHolder() {
        return new HomeStopHolder();
    }

    static class HomeStopHolder extends BaseModel {
        @BindView(R.id.home_time_hero)
        TextView timeHero;
        @BindView(R.id.home_bus_stop)
        TextView stopHero;
        @BindView(R.id.home_time_hero_minutes)
        TextView minutes;
        @BindView(R.id.home_layout)
        ConstraintLayout layout;
        @BindView(R.id.home_error_text)
        TextView error;
        @BindView(R.id.home_time_hero_intro)
        TextView intro;

        @Override
        protected void bindView(View itemView) {
            super.bindView(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

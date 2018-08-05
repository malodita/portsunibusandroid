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
import butterknife.ButterKnife;


public class HomeStopModel extends EpoxyModelWithHolder<HomeStopModel.HomeStopHolder> {
    private static final String TAG = "HomeStopModel";
    private HomeStopHolder holder;
    @EpoxyAttribute
    String timeHero;
    @EpoxyAttribute
    String stopHero;
    @EpoxyAttribute
    boolean visibility;
    @EpoxyAttribute
    boolean isHoliday;
    @EpoxyAttribute
    boolean isWeekendInHoliday;
    @EpoxyAttribute
    int stopNumber;



    @Override
    public void bind(@NonNull HomeStopHolder holder) {
        super.bind(holder);
        this.holder = holder;
        if (!visibility) {
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
            holder.error.setText(stopHero);
        } else if (isHoliday) {
            bankHoliday();
        } else if (isWeekendInHoliday){
            weekendInHoliday();
        } else {
            if (stopHero.equals("Winston Churchill Avenue")){
                holder.intro.setText(R.string.home_next_bus_union);
            } else {
                holder.intro.setText(R.string.home_next_bus_uni);
            }
            holder.stopHero.setText(String.format(holder.nextDepartureStop, stopHero));
            holder.layout.setVisibility(View.VISIBLE);
            holder.error.setVisibility(View.GONE);
            updateViewHolder(holder);
        }
    }


    private void updateViewHolder(HomeStopHolder holder) {
        if (Integer.valueOf(timeHero) < 60) {
            ImageGenerator.generateKeyImage(holder.timeHero.getContext(), stopNumber)
                    .into(holder.image);
            holder.error.setVisibility(View.GONE);
            holder.layout.setVisibility(View.VISIBLE);
            switch (Integer.valueOf(timeHero)) {
                case 0:
                    holder.timeHero.setText(holder.arrivalNow);
                    break;
                case 1:
                    holder.timeHero.setText(String.format(holder.nextDeparture, timeHero, holder.oneMinute));
                    break;
                default:
                    holder.timeHero.setText(String.format(holder.nextDeparture, timeHero, holder.multipleMinutes));
                    break;
            }
        } else if (Integer.valueOf(timeHero) == Integer.MAX_VALUE) {
            holder.image.setImageDrawable(null);
            holder.error.setText(holder.stringError);
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
        } else {
            ImageGenerator.generateKeyImage(holder.timeHero.getContext(), stopNumber)
                    .into(holder.image);
            holder.error.setVisibility(View.GONE);
            holder.layout.setVisibility(View.VISIBLE);
            holder.timeHero.setText(holder.hourPlus);
        }
    }

    @Override
    public void unbind(@NonNull HomeStopHolder holder) {
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

    private void weekendInHoliday() {
        if (holder != null) {
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
            holder.error.setText(R.string.error_weekend_in_holiday);
        }
    }

    private void bankHoliday(){
        if (holder != null) {
            holder.error.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
            holder.error.setText(R.string.error_bank_holiday);
        }
    }

    static class HomeStopHolder extends BaseHolder {
        @BindView(R.id.home_time_hero)
        TextView timeHero;
        @BindView(R.id.home_bus_stop)
        TextView stopHero;
        @BindView(R.id.home_layout)
        ConstraintLayout layout;
        @BindView(R.id.home_error_text)
        TextView error;
        @BindView(R.id.home_time_hero_intro)
        TextView intro;
        @BindView(R.id.home_image)
        ImageView image;

        @BindString(R.string.one_minute)
        String oneMinute;
        @BindString(R.string.minutes)
        String multipleMinutes;
        @BindString(R.string.stop_next_departure)
        String nextDeparture;
        @BindString(R.string.home_closest_stop_next_departure_from)
        String nextDepartureStop;
        @BindString(R.string.error_no_buses_home)
        String stringError;
        @BindString(R.string.arrival_now)
        String arrivalNow;
        @BindString(R.string.time_hour_plus_text)
        String hourPlus;

        @Override
        protected void bindView(View itemView) {
            super.bindView(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

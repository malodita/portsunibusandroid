package com.malcolm.portsmouthunibus.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.detail.DetailActivity;
import com.malcolm.portsmouthunibus.viewholders.TimetableItemViewHolder;
import com.malcolm.unibusutilities.Times;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Malcolm on 24/08/2016. Thanks to https://www.youtube.com/watch?v=_RBnC2mMY8U
 */
public class TimetableFragmentAdapter extends RecyclerView.Adapter<TimetableItemViewHolder>{
    private static final String TAG = "TimesAdaptor";
    private List<Times> times = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private FirebaseAnalytics analytics;
    private int currentStopId;

    public TimetableFragmentAdapter(Context context, ArrayList<Times> times, int currentStopId){
        this.context = context;
        analytics = FirebaseAnalytics.getInstance(context);
        inflater = LayoutInflater.from(context);
        this.times = times;
        this.currentStopId = currentStopId;
    }




    @Override
    public TimetableItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_timetable, parent, false);
        return new TimetableItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final TimetableItemViewHolder holder, int position) {
        final int holderPosition = position;
        holder.time.setText(times.get(holderPosition).getTime());
        holder.destination.setText(times.get(holderPosition).getDestination());
        holder.position = times.get(holderPosition).getId();
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("listPosition", holderPosition);
                bundle.putString("stop", holder.destination.getText().toString());
                bundle.putString("time", holder.time.getText().toString());
                i.putExtra(context.getString(R.string.intent_list_position), holder.position);
                i.putExtra(context.getString(R.string.intent_stop), holder.destination.getText());
                i.putExtra(context.getString(R.string.intent_stop_time), holder.time.getText());
                i.putExtra(context.getString(R.string.intent_stop_viewed), currentStopId);
                analytics.logEvent(context.getString(R.string.firebase_timetable_detail_request), bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeClipRevealAnimation(v, 0, 0
                            , v.getMeasuredWidth(), v.getMeasuredHeight());
                    context.startActivity(i, options.toBundle());
                } else {
                    context.startActivity(i);
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return times.size();
    }

    public void clearData(){
        times.clear();
        notifyDataSetChanged();
    }

    public void swapData(List<Times> list, int newPosition){
        if (times != null){
            times.clear();
            times.addAll(list);
        } else {
            times = list;
        }
        currentStopId = newPosition;
        notifyDataSetChanged();
    }

}

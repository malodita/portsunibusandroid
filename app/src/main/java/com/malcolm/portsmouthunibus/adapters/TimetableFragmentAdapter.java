package com.malcolm.portsmouthunibus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.malcolm.portsmouthunibus.R;
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
    private int currentStopId;

    public TimetableFragmentAdapter(Context context, ArrayList<Times> times, int currentStopId){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.times = times;
        this.currentStopId = currentStopId;
    }




    @Override
    public TimetableItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        return new TimetableItemViewHolder(inflater.inflate(R.layout.list_item_timetable, parent, false), true);
    }


    @Override
    public void onBindViewHolder(final TimetableItemViewHolder holder, int position) {
        holder.time.setText(times.get(position).getTime());
        holder.destination.setText(times.get(position).getDestination());
        holder.position = times.get(position).getId();
        holder.currentStopId = this.currentStopId;
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

    public void refreshData(List<Times> list) {
        if (times != null){
            times.clear();
            times.addAll(list);
        } else {
            times = list;
        }
        notifyDataSetChanged();
    }
}

package com.malcolm.portsmouthunibus.ui.timetable;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.viewholders.TimetableItemViewHolder;
import com.malcolm.unibusutilities.entity.Times;

import java.util.List;

/**
 * Created by Malcolm on 24/08/2016. Thanks to https://www.youtube.com/watch?v=_RBnC2mMY8U
 */
public class TimetableFragmentAdapter extends RecyclerView.Adapter<TimetableItemViewHolder>{
    private static final String TAG = "TimesAdaptor";
    private List<Times> times;
    private LayoutInflater inflater;
    private int currentStopId;

    TimetableFragmentAdapter(Context context, List<Times> times, int currentStopId){
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
        holder.via.setText(times.get(position).getVia());
    }


    @Override
    public int getItemCount() {
        return times.size();
    }

    void clearData(){
        times.clear();
        notifyDataSetChanged();
    }

    void swapData(List<Times> list, int newPosition){
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

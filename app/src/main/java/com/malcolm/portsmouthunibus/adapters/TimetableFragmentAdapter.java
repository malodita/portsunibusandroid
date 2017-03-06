package com.malcolm.portsmouthunibus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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

    public TimetableFragmentAdapter(Context context, ArrayList<Times> times)   {
        LayoutInflater inflator = LayoutInflater.from(context);
        this.times = times;

    }




    @Override
    public TimetableItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_timetable, parent,false);
        return new TimetableItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TimetableItemViewHolder holder, final int position) {
        holder.timeTextView.setText(times.get(position).getTime());
        holder.destinationTextView.setText(times.get(position).getDestination());
        holder.position = times.get(position).getId();
    }


    @Override
    public int getItemCount() {
        return times.size();

    }

    public void swapData(List<Times> list){
        if (times != null){
            times.clear();
            times.addAll(list);
        } else {
            times = list;
        }
        notifyDataSetChanged();
    }

}

package com.malcolm.portsmouthunibus.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.viewholders.DetailViewHolder;
import com.malcolm.unibusutilities.Times;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Malcolm on 26/08/2016.
 */
public class DetailActivityAdapter extends RecyclerView.Adapter<DetailViewHolder> {
    private static final String TAG = "TimesDetailAdaptor";
    private Activity activity;
    private LayoutInflater inflator;
    List<Times> times = Collections.emptyList();
    public DetailActivityAdapter(Context context, ArrayList<Times> times) {
        inflator = LayoutInflater.from(context);
        this.times = times;
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_route_detail, parent,false);
        DetailViewHolder holder = new DetailViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position) {
        holder.timeTextView.setText(times.get(position).getTime());
        holder.destinationTextView.setText(times.get(position).getDestination());
        holder.position = times.get(position).getId();

    }

    @Override
    public int getItemCount() {
        return times.size();
    }
}

package com.malcolm.portsmouthunibus.ui.detail;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.viewholders.TimetableItemViewHolder;
import com.malcolm.unibusutilities.entity.Times;

import java.util.Collections;
import java.util.List;

/**
 * Created by Malcolm on 26/08/2016.
 */
public class DetailActivityAdapter extends RecyclerView.Adapter<TimetableItemViewHolder> {
    private static final String TAG = "TimesDetailAdaptor";
    private List<Times> times = Collections.emptyList();

    public DetailActivityAdapter(Context context, List<Times> times) {
        LayoutInflater.from(context);
        this.times = times;
    }

    @Override
    public TimetableItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TimetableItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_detail, parent,false), false);
    }


    @Override
    public void onBindViewHolder(TimetableItemViewHolder holder, int position) {
        holder.time.setText(times.get(position).getTime());
        holder.destination.setText(times.get(position).getDestination());
        holder.position = times.get(position).getId();
        if (holder.via != null) {
            holder.via.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return times.size();
    }
}

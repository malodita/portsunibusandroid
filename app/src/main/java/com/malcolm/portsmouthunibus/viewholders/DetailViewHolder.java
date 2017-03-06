package com.malcolm.portsmouthunibus.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.malcolm.portsmouthunibus.R;

/**
 * Created by Malcolm on 26/08/2016.
 */
public class DetailViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "SteViewHolder";
    public TextView timeTextView;
    public TextView destinationTextView;
    public int position;
    private final Context context;

    public DetailViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        timeTextView = (TextView) itemView.findViewById(R.id.depart_time_detail);
        destinationTextView = (TextView) itemView.findViewById(R.id.destination_detail);
    }
}

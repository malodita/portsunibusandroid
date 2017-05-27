package com.malcolm.portsmouthunibus.viewholders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.malcolm.portsmouthunibus.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimetableItemViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.time) public TextView time;
    @BindView(R.id.destination) public TextView destination;
    @BindView(R.id.layout) public ConstraintLayout layout;
    public int position;


    public TimetableItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}

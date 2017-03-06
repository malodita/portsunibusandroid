package com.malcolm.portsmouthunibus.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.detail.DetailActivity;

/**
 * Created by Malcolm on 24/08/2016. This class is what the RecyclerViewAdapter populates the
 * recycler view with. It contains all the information that is to be displayed or manipulated
 */
public class TimetableItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "TimetableItemViewHolder";
    private Context context;
    //Do not set these as access private
    public TextView timeTextView;
    public TextView destinationTextView;
    public int position;


    /**
     * Sets the views in each list item of the recyclerview. Also sets the behaviour of the list
     * item with an onClickListener
     *
     * @param itemView each item the adapter fills with content
     */
    public TimetableItemViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        timeTextView = (TextView) itemView.findViewById(R.id.depart_time_recycler);
        destinationTextView = (TextView) itemView.findViewById(R.id.destination_recycler);
        LinearLayout layout = (LinearLayout) itemView.findViewById(R.id.inner_linear_layout_recyclerview);
        layout.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        Intent i = new Intent(view.getContext(), DetailActivity.class);
        Activity activity = (Activity) view.getContext();
      /*  ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                destinationTextView, "default");*/
        i.putExtra("listPosition", position);
        i.putExtra("stop", destinationTextView.getText());
        i.putExtra("time", timeTextView.getText());
        context.startActivity(i);
        //context.startActivity(i, options.toBundle());
        //Todo: When transitions are fixed, reenable


    }
}

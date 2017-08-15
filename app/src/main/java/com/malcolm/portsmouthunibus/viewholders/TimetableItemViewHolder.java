package com.malcolm.portsmouthunibus.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.detail.DetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimetableItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @BindView(R.id.time) public TextView time;
    @BindView(R.id.destination) public TextView destination;
    @BindView(R.id.layout) public ConstraintLayout layout;
    public int position;
    public int currentStopId;
    private Context context;
    private FirebaseAnalytics analytics;



    public TimetableItemViewHolder(View itemView, boolean isClickable) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        if (isClickable) {
            context = itemView.getContext();
            analytics = FirebaseAnalytics.getInstance(context);
            itemView.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("listPosition", getAdapterPosition());
        bundle.putString("stop", destination.getText().toString());
        bundle.putString("time", time.getText().toString());
        i.putExtra(context.getString(R.string.intent_list_position), position);
        i.putExtra(context.getString(R.string.intent_stop), destination.getText());
        i.putExtra(context.getString(R.string.intent_stop_time), time.getText());
        i.putExtra(context.getString(R.string.intent_stop_viewed), currentStopId);
        analytics.logEvent(context.getString(R.string.firebase_timetable_detail_request), bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeClipRevealAnimation(view, 0, 0
                    , view.getMeasuredWidth(), view.getMeasuredHeight());
            context.startActivity(i, options.toBundle());
        } else {
            context.startActivity(i);
            Activity activity = (Activity) context;
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}

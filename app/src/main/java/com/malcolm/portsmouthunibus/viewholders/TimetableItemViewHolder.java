package com.malcolm.portsmouthunibus.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.detail.DetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Malcolm on 24/08/2016. This class is what the RecyclerViewAdapter populates the
 * recycler view with. It contains all the information that is to be displayed or manipulated
 */
public class TimetableItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "TimetableItemViewHolder";
    private Context context;
    @BindView(R.id.depart_time_recycler) public TextView timeTextView;
    @BindView(R.id.destination_recycler) public TextView destinationTextView;
    @BindView(R.id.inner_linear_layout_recyclerview) LinearLayout layout;
    @BindView(R.id.card_view_recyclerview) CardView cardView;
    public int position;
    private FirebaseAnalytics analytics;


    /**
     * Sets the views in each list item of the recyclerview. Also sets the behaviour of the list
     * item with an onClickListener
     *
     * @param itemView each item the adapter fills with content
     */
    public TimetableItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
        layout.setOnClickListener(this);
        analytics = FirebaseAnalytics.getInstance(context);
    }


    @Override
    public void onClick(View view) {
        Intent i = new Intent(view.getContext(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("listPosition", position);
        bundle.putString("stop", destinationTextView.getText().toString());
        bundle.putString("time", timeTextView.getText().toString());
        i.putExtra(context.getString(R.string.intent_list_position), position);
        i.putExtra(context.getString(R.string.intent_stop), destinationTextView.getText());
        i.putExtra(context.getString(R.string.intent_stop_time), timeTextView.getText());
        //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, cardView, ViewCompat.getTransitionName(cardView));
        //This can be played with later to create shared element transition
        analytics.logEvent(context.getString(R.string.firebase_timetable_detail_request), bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeClipRevealAnimation(view, 0, 0
                    , view.getMeasuredWidth(), view.getMeasuredHeight());
            context.startActivity(i, options.toBundle());
        } else {
            context.startActivity(i);
            Activity activity = (Activity) context;
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}

package com.malcolm.portsmouthunibus.detail;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.adapters.DetailActivityAdapter;
import com.malcolm.unibusutilities.DatabaseHelper;
import com.malcolm.unibusutilities.Times;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    @NonNull
    @BindView(R.id.detail_recycler_view) RecyclerView recyclerView;
    @NonNull
    @BindView(R.id.title_text_view) TextView titleTextView;
    @Nullable @BindView(R.id.app_bar_detail) Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        FirebaseAnalytics.getInstance(this);
        Intent i = getIntent();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*
         * This gets the position that was clicked from the intent to be used to
         * display the correct timetable
         */
        int listPosition = i.getIntExtra(getString(R.string.intent_list_position), 1);
        setActivityTitle(i);
        boolean timeFormat = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE)
                .getBoolean(getString(R.string.preferences_24hourclock), true);
        ArrayList<Times> array = DatabaseHelper.getInstance(this).getDataForList(listPosition, timeFormat);
        setUpRecyclerView(recyclerView, array);
    }


    protected void setActivityTitle(Intent intent){
        String stop = intent.getCharSequenceExtra(getString(R.string.intent_stop)).toString();
        String time = intent.getCharSequenceExtra(getString(R.string.intent_stop_time)).toString();
        titleTextView.setText(getString(R.string.card_title, time, stop));
    }


    protected void setUpRecyclerView(final RecyclerView recyclerView, ArrayList<Times> times){
        DetailActivityAdapter adapter = new DetailActivityAdapter(this, times);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            //getWindow().getSharedElementEnterTransition();
            recyclerView.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                                View v = recyclerView.getChildAt(i);
                                v.setAlpha(0.0f);
                                recyclerView.setVisibility(View.VISIBLE);
                                v.animate().alpha(1.0f)
                                        .setDuration(300)
                                        .setStartDelay(i * 50)
                                        .start();
                            }
                            return true;
                        }
                    });
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

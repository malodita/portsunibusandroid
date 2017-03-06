package com.malcolm.portsmouthunibus.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
    @BindView(R.id.detail_recycler_view) RecyclerView mRecyclerView;
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
        /**
         * This gets the position that was clicked from the intent to be used to
         * display the correct timetable
         */
        int valueToGet = i.getIntExtra(getString(R.string.intent_listPosition), 1);
        setActivityTitle(i);
        ArrayList<Times> array = DatabaseHelper.getInstance(this).getDataForList(valueToGet);
        setUpRecyclerView(mRecyclerView, array);
    }


    protected void setActivityTitle(Intent intent){
        String stop = intent.getCharSequenceExtra("stop").toString();
        String time = intent.getCharSequenceExtra("time").toString();
        titleTextView.setText("The " + time + " at " + stop);
    }


    protected void setUpRecyclerView(RecyclerView recyclerView, ArrayList<Times> times){
        DetailActivityAdapter adapter = new DetailActivityAdapter(this, times);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}

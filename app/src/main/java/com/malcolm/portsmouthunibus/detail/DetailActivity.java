package com.malcolm.portsmouthunibus.detail;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.adapters.DetailActivityAdapter;
import com.malcolm.unibusutilities.DatabaseHelper;
import com.malcolm.unibusutilities.Times;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements Palette.PaletteAsyncListener {
    private static final String TAG = "DetailActivity";
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_wrapper)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.image_view)
    ImageView imageView;

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
        setupToolbar(i);
        boolean timeFormat = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE)
                .getBoolean(getString(R.string.preferences_24hourclock), true);
        ArrayList<Times> array = DatabaseHelper.getInstance(this).getDataForList(listPosition, timeFormat);
        setUpRecyclerView(recyclerView, array);
    }


    protected void setupToolbar(Intent intent){
        String stop = intent.getCharSequenceExtra(getString(R.string.intent_stop)).toString();
        String time = intent.getCharSequenceExtra(getString(R.string.intent_stop_time)).toString();
        int viewedStop = intent.getIntExtra(getString(R.string.intent_stop_viewed), -1);
        toolbarLayout.setTitle(getString(R.string.card_title, time, stop));
        toolbarLayout.setExpandedTitleTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        setupImage(viewedStop);
    }

    private void setupImage(int stop){
        Drawable drawable;
        //drawable = ImageGenerator.generateImage(this, stop);
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO){
            drawable = ContextCompat.getDrawable(this, R.drawable.dsc03145);
        } else {
            drawable = ContextCompat.getDrawable(this, R.drawable.img_0145);
        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        new Palette.Builder(bitmap).maximumColorCount(16).generate(this);
        imageView.setImageDrawable(drawable);
    }

    @Override
    public void onGenerated(Palette palette) {
        Palette.Swatch swatch = palette.getVibrantSwatch();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(palette.getDarkVibrantColor(ContextCompat.getColor(this, R.color.primary_dark)));
        }
        toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.icons));
        if (swatch != null){
            toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.icons));
            toolbarLayout.setContentScrimColor(swatch.getRgb());
        } else {
            toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.icons));
            toolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.primary));
        }
    }

    protected void setUpRecyclerView(final RecyclerView recyclerView, ArrayList<Times> times){
        DetailActivityAdapter adapter = new DetailActivityAdapter(this, times);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
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

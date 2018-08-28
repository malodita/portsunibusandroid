package com.malcolm.portsmouthunibus.ui.detail;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.utilities.ImageGenerator;
import com.malcolm.unibusutilities.entity.Times;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements Palette.PaletteAsyncListener, Callback {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.image_placeholder));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*
         * This gets the position that was clicked from the intent to be used to
         * display the correct timetable
         */
        int listPosition = i.getIntExtra(getString(R.string.intent_list_position), 1);
        setupToolbar(i);
        boolean timeFormat = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE)
                .getBoolean(getString(R.string.preferences_24hourclock), true);
        DetailViewModel.Factory factory = new DetailViewModel.Factory(getApplication());
        DetailViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
        List<Times> array;
        if (viewModel.getList() == null){
            array = viewModel.fetchList(listPosition, timeFormat);
        } else {
            array = viewModel.getList();
        }
        setUpRecyclerView(array);
    }


    protected void setupToolbar(Intent intent){
        int viewedStop = intent.getIntExtra(getString(R.string.intent_stop_viewed), -1);
        setupImage(viewedStop);
        String stop = intent.getCharSequenceExtra(getString(R.string.intent_stop)).toString();
        String time = intent.getCharSequenceExtra(getString(R.string.intent_stop_time)).toString();
        toolbarLayout.setTitle(getString(R.string.detail_toolbar_title, time, stop));
        Typeface raleway = ResourcesCompat.getFont(this, R.font.raleway_semibold);
        toolbarLayout.setExpandedTitleTypeface(raleway);
        toolbarLayout.setCollapsedTitleTypeface(raleway);
    }

    private void setupImage(int stop){
        RequestCreator requestCreator = ImageGenerator.generateImage(this, stop);
        requestCreator.into(imageView, this);
    }

    @Override
    public void onSuccess() {
        Drawable drawable = imageView.getDrawable();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        new Palette.Builder(bitmap).maximumColorCount(16).generate(this);
    }

    @Override
    public void onError() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        Drawable drawable;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            drawable = ContextCompat.getDrawable(this, R.drawable.generic_night);
        } else {
            drawable = ContextCompat.getDrawable(this, R.drawable.generic_day);

        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        if (bitmapDrawable != null) {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            imageView.setImageDrawable(drawable);
            new Palette.Builder(bitmap).maximumColorCount(20).generate(this);
            Picasso.with(this).cancelRequest(imageView);
        }

    }

    @Override
    public void onGenerated(Palette palette) {
        Palette.Swatch swatch = palette.getVibrantSwatch();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(palette.getVibrantColor(ContextCompat.getColor(this, R.color.primary_dark)));
        }
        toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.icons));
        Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        if (swatch != null){
            toolbarLayout.setContentScrimColor(swatch.getRgb());
            toolbarLayout.setCollapsedTitleTextColor(swatch.getBodyTextColor());
            backArrow.setTint(swatch.getBodyTextColor());

        } else {
            toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.icons));
            toolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.primary));
            backArrow.setTint(ContextCompat.getColor(this, R.color.icons));
        }
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
    }

    protected void setUpRecyclerView(List<Times> times){
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
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}

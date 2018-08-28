package com.malcolm.portsmouthunibus.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.malcolm.portsmouthunibus.App;
import com.malcolm.portsmouthunibus.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            int placeholder = R.id.placeholder2;
            getSupportFragmentManager().beginTransaction()
                    .replace(placeholder, new PreferencesFragment())
                    .commit();
        }
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Toolbar toolbar = findViewById(R.id.app_bar_settings);
        TextView toolbarText = findViewById(R.id.app_bar_settings_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarText.setText(R.string.action_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        backArrow.setTint(ContextCompat.getColor(this, R.color.toolbar_text));
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.preferences_night_mode_new))) {
            App.nightModeSwitching(Integer.valueOf(sharedPreferences.getString(s, "0")));
        } else if (s.equals(getString(R.string.preferences_analytics))){
            App.analyticsSwitching(sharedPreferences.getBoolean(s, true));
        }
    }

    @Override
    protected void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}

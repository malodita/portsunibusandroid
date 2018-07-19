package com.malcolm.portsmouthunibus.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.malcolm.portsmouthunibus.App;
import com.malcolm.portsmouthunibus.R;

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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.preferences_night_mode_new))) {
            App.nightModeSwitching(Integer.valueOf(sharedPreferences.getString(s, "0")));
        } else if (s.equals(getString(R.string.preferences_analytics))){
            App.analyticsSwitching(sharedPreferences.getBoolean(s, false));
            Toast.makeText(this, "Crashlytics requires the app to restart to change settings"
                    , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}

package com.malcolm.portsmouthunibus.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.malcolm.portsmouthunibus.R;

public class SettingsActivity extends AppCompatActivity {

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

}

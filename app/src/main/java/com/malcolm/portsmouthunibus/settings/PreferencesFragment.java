package com.malcolm.portsmouthunibus.settings;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.malcolm.portsmouthunibus.R;

/**
 * Created by Malcolm on 26/12/2016. The settings fragment attached to the SettingsActivity
 */

public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String homeBusStopPref = "com.malcolm.portsmouthunibus.homebusstop";
    private final String shortcutsPref = "com.malcolm.portsmouthunibus.shortcuts";
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1){
            Preference shortcuts = getPreferenceScreen().findPreference(shortcutsPref);
            shortcuts.setEnabled(true);
            shortcuts.setSummary("Clears all extra shortcuts");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // FIXME: 03/02/2017 Shared prefs still broken
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;
        if (preference instanceof AlertDialogPreference) {
            // Create a new instance of AlertDialogPreference with the key of the related
            // Preference
            dialogFragment = AlertDialogPreferenceCompat
                    .newInstance(preference.getKey());
        }
        // If it was one of our custom Preferences, show its dialog
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(),
                    "android.support.v7.preference" +
                            ".PreferenceFragment.DIALOG");
        }
        // Could not be handled here. Try with the super method.
        else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}

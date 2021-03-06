package com.malcolm.portsmouthunibus.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malcolm.portsmouthunibus.R;

import java.util.Arrays;

/**
 * Created by Malcolm on 26/12/2016.
 * Purpose of this file is to deal with any choices that are made in this preference
 */

public class AlertDialogPreferenceCompat extends PreferenceDialogFragmentCompat {

    private final String homeBusStop = "com.malcolm.portsmouthunibus.homebusstop";
    private final String shortcuts = "com.malcolm.portsmouthunibus.shortcuts";
    private static final String TAG = "AlertDialogPreference";

    /**
     * Static factory method to create the fragment the dialog is displayed in
     *
     * @param key The key of the preference
     *
     * @return a new fragment instance displaying the dialog
     */
    public static AlertDialogPreferenceCompat newInstance(
            String key) {
        final AlertDialogPreferenceCompat
                fragment = new AlertDialogPreferenceCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AlertDialogPreference preference = (AlertDialogPreference) getPreference();
        String key = preference.getKey();
        switch (key) {
            case homeBusStop:
                preference.setDialogMessage("Are you sure you want to reset your home stop?");
                break;
            case shortcuts:
                preference.setDialogMessage("Are you sure you want to clear your shortcuts?");
                break;
            default:
                break;
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    protected View onCreateDialogView(Context context) {
        return super.onCreateDialogView(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

    }

    private void resetShortcut(String shortcut){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getActivity().getSystemService(ShortcutManager.class);
            shortcutManager.disableShortcuts(Arrays.asList(shortcut));
            shortcutManager.removeDynamicShortcuts(Arrays.asList(shortcut));
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        DialogPreference preference = getPreference();
        if (preference instanceof AlertDialogPreference) {
            AlertDialogPreference alert = (AlertDialogPreference) preference;
            switch (alert.getKey()) {
                case homeBusStop:
                    alert.setResult(positiveResult);
                    resetShortcut(getString(R.string.shortcut_home_timetable));
                    break;
                case shortcuts:
                    if (positiveResult) {
                        resetShortcut(getString(R.string.shortcut_specific_timetable));
                    }
                default:
                    break;
            }
        }
    }
}


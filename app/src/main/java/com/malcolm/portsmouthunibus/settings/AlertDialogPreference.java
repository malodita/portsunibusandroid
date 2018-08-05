package com.malcolm.portsmouthunibus.settings;

import android.content.Context;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceViewHolder;
import android.util.AttributeSet;

import com.malcolm.portsmouthunibus.R;

/**
 * Created by Malcolm on 26/12/2016.
 * Purpose of this file is to create a dialog style that can be used in the app_preferences.xml file
 * It extends DialogPreference (The base class for all preferences that involve showing a
 * dialog) and requires an further class to extend PreferenceFragmentCompat to work properly
 * as once a preference dialog is opened, the first method called is onDisplayPreferenceDialog().
 *
 * Also, if the custom preference will deal with any variables such as text or choices,
 * getter and setter methods must be set in this class.
 *
 * https://medium.com/@JakobUlbrich/building-a-settings-screen-for-android-part-3-ae9793fd31ec#.qa7t9tc4b
 */

public class AlertDialogPreference extends DialogPreference {

    //Used to set the layout of the dialog to this file
    private int layout = R.layout.dialog_reset_confirmation;

    public AlertDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public AlertDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AlertDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlertDialogPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {

        super.onBindViewHolder(holder);
    }

    public void setResult(boolean positiveResult) {
        if (positiveResult) {
            persistInt(-2);
        }
    }

    @Override
    public void setDialogMessage(CharSequence dialogMessage) {
        super.setDialogMessage(dialogMessage);
    }

    @Override
    public int getDialogLayoutResource() {
        return layout;
    }

}

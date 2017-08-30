package com.malcolm.portsmouthunibus;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.adapters.TimetableFragmentAdapter;
import com.malcolm.unibusutilities.DatabaseHelper;
import com.malcolm.unibusutilities.TermDates;
import com.malcolm.unibusutilities.Times;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TimetableFragment extends Fragment implements
        AdapterView.OnItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "TimetableFragment";
    Unbinder unbinder;
    int stopToSave;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.spinner)
    AppCompatSpinner spinner;
    @BindView(R.id.no_timetable)
    TextView noTimetable;
    @BindView(R.id.timetableFragment)
    CoordinatorLayout layout;
    @Nullable
    @BindView(R.id.timetable_fab)
    FloatingActionButton floatingActionButton;
    private TimetableFragmentAdapter adapter;
    private FirebaseAnalytics firebaseAnalytics;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private boolean spinnerReady = false;


    public TimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        databaseHelper = DatabaseHelper.getInstance(getContext());
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        //handler = new TimetableHandler(this);
        setUpSpinner(spinner);
        int stopToShow = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), 1);
        if (getArguments() != null) {
            stopToSave = getArguments().getInt(getString(R.string.shortcut_specific_timetable));
            spinner.setOnItemSelectedListener(this);
            spinner.setSelection(stopToSave, true);
        } else {
            setUpRecyclerView(recyclerView, stopToShow + 1, false);
            spinner.setSelection(stopToShow - 1, true);
            stopToSave = stopToShow - 1;
            spinner.setOnItemSelectedListener(this);
        }
        return rootView;
    }

    /**
     * Sets up the spinner and attaches the array of stop locations to show as selectable options
     *
     * @param spinner The spinner to be set up
     */
    private void setUpSpinner(AppCompatSpinner spinner) {
        ArrayAdapter<CharSequence> spinnerArray = ArrayAdapter.createFromResource(getContext(), R.array.bus_stops_spinner,
                R.layout.spinner_top);
        spinnerArray.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(spinnerArray);
    }

    /**
     * Sets up the recyclerview. Creating a new adaper and filling it with the array logic to set
     * the stops. If the device runs Android 7.1, it sets up a floating action bar to use to set
     * shortcuts. Also takes care of reset logic using swapData() if the spinner reports a change if
     * the adapter is already set up.
     *
     * @param recyclerView The recyclerview to be set up.
     * @param stop         The stop to use for the database search
     */
    private void setUpRecyclerView(RecyclerView recyclerView, int stop, boolean shortcutUsed) {
        if (stop == 0) {
            return;
        }
        if (!shortcutUsed) {
            boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
            ArrayList<Times> times = databaseHelper.getTimesArray(stop, timeFormat);
            if (adapter == null) {
                recyclerView.setHasFixedSize(true);
                adapter = new TimetableFragmentAdapter(getContext(), times, stop);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                adapter.swapData(times, stop);
                sharedPreferences.edit().putInt(getString(R.string.preference_last_viewed_stop), stop).apply();
            }
        }
        recyclerView.setVisibility(View.VISIBLE);
    }



    /**
     * <p>Callback method to be invoked when an item in this view has been selected. This callback
     * is invoked only when the newly selected position is different from the previously selected
     * position or if there was no selected item.</p>
     * <p>
     * Impelmenters can call getItemAtPosition(position) if they need to access the data associated
     * with the selected item.
     * <p>
     * This method instructs the recyclerview to update based on the value selected on the spinner.
     * The reason for the slightly complicated logic is that the string array position selected is
     * used for the SQLite database search. Since the database also happens to include stops that
     * are not to be reported (since nobody gets onto them), the number should be skipped in order
     * to return a correct value for the spinner.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (floatingActionButton != null && !floatingActionButton.isShown()) {
            floatingActionButton.show();
        }
        int newStopToShow;
        stopToSave = position;
        if (position == 10) {
            if (TermDates.isHoliday() || TermDates.isWeekend()) {
                recyclerView.setVisibility(View.GONE);
                noTimetable.setText(R.string.error_eastney);
                noTimetable.setVisibility(View.VISIBLE);
                adapter.clearData();
                return;
            }
            newStopToShow = 1;
        } else if (position < 5) {
            newStopToShow = position + 2;
        } else {
            newStopToShow = position + 3;
        }
        String[] array = getContext().getResources().getStringArray(R.array.bus_stops_spinner);
        Bundle bundle = new Bundle();
        setUpRecyclerView(recyclerView, newStopToShow, false);
        noTimetable.setVisibility(View.GONE);
        if (spinnerReady) {
            bundle.putString(getString(R.string.firebase_property_stop_id), array[position]);
            firebaseAnalytics.logEvent(getString(R.string.firebase_event_timetable_changed_stop), bundle);
        }
        spinnerReady = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            setUpFab();
        }
    }

    /**
     * Sets up the floating action button.
     * <p>
     * As it controls a feature that is dependent on API > 24, it will only set it up if the device
     * is above this version. Hence there are two different layout files for the
     * </p>
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void setUpFab() {
        floatingActionButton.show();
        floatingActionButton.setOnClickListener(v -> buildDialog());
    }

    /**
     * Creates a dialog asking if the user wishes to create a shortcut to see that stop timetable.
     * If positive, the shortcut is saved and added, and a snackbar is shown. Will only run on
     * Android 7.1+
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void buildDialog() {
        Boolean onboarding = sharedPreferences.getBoolean(getString(R.string.preferences_onboarding_3), false);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Add Shortcut")
                .setMessage(R.string.dialog_create_shortcut)
                .setPositiveButton("Okay", (dialog, which) -> {
                    saveShortcut();
                    showConfirmAnimation();
                })
                .setNegativeButton("Cancel", null)
                .show();
        if (!onboarding) {
            onboarding3(alertDialog);
        }
    }

    /**
     * Saves the shortcut chosen by the user
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void saveShortcut() {
        Log.d(TAG, "saveShortcut: stop is " + stopToSave);
        String[] busLong = getResources().getStringArray(R.array.bus_stops_shortcuts_long);
        String shortcutLong = busLong[stopToSave];
        String[] busShort = getResources().getStringArray(R.array.bus_stops_shortcuts_short);
        String shortcutShort = busShort[stopToSave];
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.firebase_property_stop_id), shortcutShort);
        firebaseAnalytics.logEvent(getString(R.string.firebase_event_shortcut_created), bundle);
        ShortcutManager manager = getContext().getSystemService(ShortcutManager.class);
        String packageName = getContext().getApplicationInfo().packageName;
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(getContext()
                , getString(R.string.shortcut_specific_timetable))
                .setShortLabel(shortcutShort)
                .setLongLabel(shortcutLong)
                .setIcon(Icon.createWithResource(getContext(), R.mipmap.ic_shortcut_timetable_blue))
                .setIntent(new Intent(getString(R.string.shortcut_specific_timetable_action))
                        .setPackage(packageName)
                        .setClass(getContext(), HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra(getString(R.string.shortcut_specific_timetable), stopToSave))
                .build();
        manager.addDynamicShortcuts(Arrays.asList(shortcutInfo));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager.isRequestPinShortcutSupported()) {
            manager.requestPinShortcut(shortcutInfo, null);
        }
    }

    /**
     * Animates the FAB and raises the snackbar.
     * <p>
     * When this method completes, the FAB is then hidden. This behavior is because the vector
     * animation cannot be reversed. This should be kept even after Android O is released as this is
     * a workaround for API 25 </P>
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void showConfirmAnimation() {
        Snackbar snackbar = Snackbar.make(layout, "Shortcut created", Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_dark));
        final AnimatedVectorDrawable vectorDrawable = (AnimatedVectorDrawable) floatingActionButton.getDrawable();
        final ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), getContext().getColor(R.color.accent)
                , getContext().getColor(R.color.fab_confirm));
        animator.addUpdateListener(animation -> {
            int color = (int) animator.getAnimatedValue();
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(color));
        });
        animator.start();
        vectorDrawable.start();
        snackbar.show();
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                floatingActionButton.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onHidden(FloatingActionButton fab) {
                        super.onHidden(fab);
                        vectorDrawable.reset();
                        animator.reverse();
                    }
                });
            }
        });
    }

    /**
     * If the user has not used this before, this will bring up a short onboarding sequence
     * explaining app shortcuts.
     *
     * @param dialog The dialog that was initiated in order to get the view for the positive button
     */
    private void onboarding3(AlertDialog dialog) {
        TapTargetView.showFor(dialog,
                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        , "App shortcuts", "Long press the app icon to access shortcuts\n" +
                                "Drag to your home screen for quick access")
                        .targetCircleColor(R.color.primary)
                        .outerCircleColor(R.color.onboarding_2_outer_circle)
                        .textColor(R.color.textview_inverse_color)
                        .transparentTarget(true)
                        .drawShadow(true), new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                        super.onTargetDismissed(view, userInitiated);
                        sharedPreferences.edit()
                                .putBoolean(getString(R.string.preferences_onboarding_3), true)
                                .apply();
                    }
                });

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.preferences_24hourclock))) {
            boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
            int stop = sharedPreferences.getInt(getString(R.string.preference_last_viewed_stop), 2);
            ArrayList<Times> array = databaseHelper.getTimesArray(stop, timeFormat);
            adapter.refreshData(array);
        }
    }

    /**
     * Callback method to be invoked when the selection disappears from this view. The selection can
     * disappear for instance when touch is activated or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        unbinder.unbind();
    }

}

package com.malcolm.portsmouthunibus.ui.timetable;


import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.BuildConfig;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.ui.HomeActivity;
import com.malcolm.unibusutilities.entity.Times;
import com.malcolm.unibusutilities.helper.TermDateUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TimetableFragment extends Fragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, StopPickerAdapter.StopSelectedListener, DialogInterface.OnClickListener {

    private static final String TAG = "TimetableFragment";
    Unbinder unbinder;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.no_timetable)
    TextView noTimetable;
    @BindView(R.id.timetableFragment)
    CoordinatorLayout layout;
    @BindView(R.id.top_layout_favourite)
    Button spinnerFavourite;
    @BindView(R.id.top_layout_next_bus)
    TextView spinnerNextBus;
    private final Observer<String> countdownObserver = makeCountdownObserver();
    @BindView(R.id.top_layout_stop)
    TextView topLayoutStop;
    @BindView(R.id.timetable_fab)
    FloatingActionButton fab;
    //@BindView(R.id.picker_recycler_view)
    //RecyclerView pickerList;
    //@BindView(R.id.scrim)
    //View scrim;
    // TODO: 11/07/2018 Enable with final components release
    private TimetableFragmentAdapter adapter;
    private FirebaseAnalytics firebaseAnalytics;
    private SharedPreferences sharedPreferences;
    private TimetableViewModel viewModel;
    private String[] stopsArray;
    /**
     * Ensures the correct stop is propagated to {@link com.malcolm.portsmouthunibus.ui.detail.DetailActivity}
     * to obtain the correct image
     */
    private int viewedStop;


    public TimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        if (BuildConfig.DEBUG) {
            firebaseAnalytics.setAnalyticsCollectionEnabled(false);

        }
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        stopsArray = getResources().getStringArray(R.array.bus_stops_spinner);
        setUpRecyclerView();
        setupFab();
        return rootView;
    }

    private void setUpRecyclerView() {
        int stop = stopNumberGenerator(getStartingStop());
        boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
        TimetableViewModel.Factory factory = new TimetableViewModel.Factory(getActivity().getApplication(), timeFormat, stop);
        viewModel = ViewModelProviders.of(this, factory).get(TimetableViewModel.class);
        Observer<List<Times>> observer = timesList -> {
            if (adapter == null) {
                recyclerView.setHasFixedSize(true);
                adapter = new TimetableFragmentAdapter(getContext(), timesList, stop);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setVisibility(View.VISIBLE);
                LayoutAnimationController animationController =
                        AnimationUtils.loadLayoutAnimation(getContext(), R.anim.home_appear_animation);
                recyclerView.setLayoutAnimation(animationController);
            } else {
                adapter.swapData(timesList, viewedStop);
                recyclerView.scheduleLayoutAnimation();
                sharedPreferences.edit().putInt(getString(R.string.preference_last_viewed_stop), viewedStop).apply();
            }
        };
        viewModel.getData().observe(this, observer);
        if (!TermDateUtils.isWeekendInHoliday() && !TermDateUtils.isBankHoliday()) {
            viewModel.getCurrentCountdown().observe(this, countdownObserver);
        } else {
            spinnerNextBus.setText(getString(R.string.error_no_buses_scheduled));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            spinnerFavourite.setVisibility(View.VISIBLE);
            spinnerFavourite.setOnClickListener(l -> buildDialog(stop));
        }
    }

    private void setupFab() {
        //StopPickerAdapter pickerAdapter = new StopPickerAdapter(this, stopsArray);
        //pickerList.setLayoutManager(new LinearLayoutManager(getContext()));
        //pickerList.setAdapter(pickerAdapter);
        fab.show();
        fab.setOnClickListener(l -> new AlertDialog.Builder(getContext())
                .setTitle("Select a stop")
                .setItems(R.array.bus_stops_spinner, this)
                .create()
                .show());
        // TODO: 19/06/2018 Enable this when Components stable and remove placeholder
        //fab.setOnClickListener(l -> fab.setExpanded(!fab.isExpanded()));
        //scrim.setOnClickListener(l -> fab.setExpanded(false));
    }

    @Override // TODO: 19/06/2018 Remove when Components stable
    public void onClick(DialogInterface dialogInterface, int position) {
        boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
        topLayoutStop.setText(stopsArray[position]);
        if (position == 10) {
            if (TermDateUtils.isHoliday() || TermDateUtils.isWeekend()) {
                recyclerView.setVisibility(View.GONE);
                noTimetable.setText(R.string.error_eastney);
                noTimetable.setVisibility(View.VISIBLE);
                adapter.clearData();
                viewModel.updateStopList(0, timeFormat);//Reports back as -1 to hide the next stop TextView
                return;
            }
        }
        int stop = stopNumberGenerator(position);
        viewedStop = stop;
        String[] array = getContext().getResources().getStringArray(R.array.bus_stops_spinner);
        viewModel.changeListOfStops(stop, timeFormat);
        noTimetable.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        if (!TermDateUtils.isWeekendInHoliday() && !TermDateUtils.isBankHoliday()) {
            if (viewModel.getCurrentCountdown().hasActiveObservers()) {
                viewModel.updateStopList(stop, timeFormat);
            } else {
                viewModel.getCurrentCountdown().observe(this, countdownObserver);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.firebase_property_stop_id), array[position]);
        firebaseAnalytics.logEvent(getString(R.string.firebase_event_timetable_changed_stop), bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            spinnerFavourite.setOnClickListener(null);
            spinnerFavourite.setOnClickListener(l -> buildDialog(stop));
        }
    }

    private int getStartingStop() {
        if (getArguments() != null) {
            return getArguments().getInt(getString(R.string.shortcut_specific_timetable));
        } else {
            int pref = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), 0);
            if (pref == -2) {//If no default stop set
                topLayoutStop.setText(stopsArray[0]);
                return 0;
            } else {
                topLayoutStop.setText(stopsArray[pref]);
                return pref;
            }
        }
    }

    /**
     * Creates a dialog asking if the user wishes to create a shortcut to see that stop timetable.
     * If positive, the shortcut is saved and added, and a snackbar is shown. Will only run on
     * Android 7.1+
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void buildDialog(int stop) {
        Boolean onboarding = sharedPreferences.getBoolean(getString(R.string.preferences_onboarding_3), false);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Add Shortcut")
                .setMessage(R.string.dialog_create_shortcut)
                .setPositiveButton("Okay", (dialog, which) -> {
                    saveShortcut(stop);
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
    @TargetApi(Build.VERSION_CODES.N_MR1)//Todo: Shortcuts currently disabled
    private void saveShortcut(int stopToSave) {
        String shortcutLong = getResources().getStringArray(R.array.bus_stops_shortcuts_long)[stopToSave];
        String shortcutShort = getResources().getStringArray(R.array.bus_stops_shortcuts_short)[stopToSave];
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.firebase_property_stop_id), shortcutShort);
        firebaseAnalytics.logEvent(getString(R.string.firebase_event_shortcut_created), bundle);
        ShortcutManager manager = Objects.requireNonNull(getContext()).getSystemService(ShortcutManager.class);
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
        Objects.requireNonNull(manager).addDynamicShortcuts(Arrays.asList(shortcutInfo));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager.isRequestPinShortcutSupported()) {
            manager.requestPinShortcut(shortcutInfo, null);
        }
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
    public void onStopSelected(int position) {
        boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
        topLayoutStop.setText(stopsArray[position]);
        if (position == 10) {
            if (TermDateUtils.isHoliday() || TermDateUtils.isWeekend()) {
                recyclerView.setVisibility(View.GONE);
                noTimetable.setText(R.string.error_eastney);
                noTimetable.setVisibility(View.VISIBLE);
                adapter.clearData();
                viewModel.updateStopList(0, timeFormat);//Reports back as -1 to hide the next stop TextView
                return;
            }
        }
        int stop = stopNumberGenerator(position);
        viewedStop = stop;
        String[] array = getContext().getResources().getStringArray(R.array.bus_stops_spinner);
        viewModel.changeListOfStops(stop, timeFormat);
        noTimetable.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        if (!TermDateUtils.isWeekendInHoliday() && !TermDateUtils.isBankHoliday()) {
            if (viewModel.getCurrentCountdown().hasActiveObservers()) {
                viewModel.updateStopList(stop, timeFormat);
            } else {
                viewModel.getCurrentCountdown().observe(this, countdownObserver);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.firebase_property_stop_id), array[position]);
        firebaseAnalytics.logEvent(getString(R.string.firebase_event_timetable_changed_stop), bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            spinnerFavourite.setOnClickListener(null);
            spinnerFavourite.setOnClickListener(l -> buildDialog(stop));
        }
        // TODO: 19/06/2018 When Components stable, uncomment
        // fab.setExpanded(false);
    }

    private int stopNumberGenerator(int position) {
        int newStopToShow;
        if (position == 10) {
            newStopToShow = 1;
        } else if (position < 5) {
            newStopToShow = position + 2;
        } else {
            newStopToShow = position + 3;
        }
        return newStopToShow;
    }

    private Observer<String> makeCountdownObserver() {
        return s -> {
            if (s.equals(String.valueOf(-1))) {
                spinnerNextBus.setText(getText(R.string.error_no_buses_scheduled));
                return;
            }
            spinnerNextBus.setVisibility(View.VISIBLE);
            if (s.equals(String.valueOf(Integer.MAX_VALUE))) {
                spinnerNextBus.setText(getText(R.string.error_no_buses));
            } else {
                spinnerNextBus.setText(getString(R.string.timetable_next_bus, s));
            }
        };
    }

    /*   /**
     * Animates the FAB and raises the snackbar.
     * <p>
     * When this method completes, the FAB is then hidden. This behavior is because the vector
     * animation cannot be reversed. This should be kept even after Android O is released as this is
     * a workaround for API 25 </P>
     *//*
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void showConfirmAnimation() {
        Snackbar snackbar = Snackbar.make(layout, "Shortcut created", Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
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
                if (floatingActionButton != null) {
                    floatingActionButton.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                        @Override
                        public void onHidden(FloatingActionButton fab) {
                            super.onHidden(fab);
                            vectorDrawable.reset();
                            animator.reverse();
                        }
                    });
                }
                super.onDismissed(transientBottomBar, event);
            }
        });
    }*/

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.preferences_24hourclock))) {
            boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
            int stop = sharedPreferences.getInt(getString(R.string.preference_last_viewed_stop), 2);
            viewModel.changeListOfStops(stop, timeFormat);
            if (!TermDateUtils.isWeekendInHoliday() && !TermDateUtils.isBankHoliday()) {
                if (viewModel.getCurrentCountdown().hasObservers()) {
                    viewModel.updateStopList(stop, timeFormat);
                }
            }
            viewedStop = stop;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        unbinder.unbind();
    }

    public CoordinatorLayout getLayout() {
        return layout;
    }
}

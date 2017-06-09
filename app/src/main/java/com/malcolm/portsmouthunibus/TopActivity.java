package com.malcolm.portsmouthunibus;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.intro.IntroActivity;
import com.malcolm.portsmouthunibus.settings.SettingsActivity;
import com.malcolm.portsmouthunibus.utilities.BottomSheet;
import com.malcolm.unibusutilities.TermDates;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TopActivity extends AppCompatActivity implements OnTabSelectListener, BottomSheet.DialogListener {
    //Initialise local variables
    private static final String TAG = "TopActivity";
    private static final String HOMESHORTCUT = "com.malcolm.portsmouthunibus.VIEW.TIMETABLE";
    private static final String SPECIFICSHORTCUT = "com.malcolm.portsmouthunibus.VIEW.TIMETABLE.SPECIFIC";
    private static final String TOPFRAGMENTTAG = "TopFragment";
    private static final String TIMETABLETAG = "TimetableFragment";
    private static final String MAPSTAG = "MapsFragment";
    private static final String BOTTOMSHEET = "BottomSheet";
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    @BindView(R.id.bottom_bar_view)
    BottomBar bottomBar;
    @BindView(R.id.placeholder)
    CoordinatorLayout layout;
    private FirebaseAnalytics firebaseAnalytics;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private boolean timeFormat;
    private boolean mapCardAllowed;

    /**
     * Static method to enable DayNight
     */
    static void nightModeSwitching(boolean mode) {
        if (mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        boolean onboarding = sharedPreferences.getBoolean(getString(R.string.preferences_onboarding), false);
        //If initial app onboarding has happened or not to set the content screen
        if (!onboarding) {
            startOnboarding();
        } else {
            boolean onboarding2 = sharedPreferences.getBoolean(getString(R.string.preferences_onboarding_2), false);
            nightMode = sharedPreferences.getBoolean(getString(R.string.preferences_night_mode), true);
            timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
            mapCardAllowed = sharedPreferences.getBoolean(getString(R.string.preferences_maps_card), true);
            nightModeSwitching(nightMode);
            setContentView(R.layout.activity_top);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.INTERNET}, 0);
            }
            if (savedInstanceState == null) {
                isGooglePlayServicesAvailable(this);
            }
            ButterKnife.bind(this);
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.icons));
            toolbar.inflateMenu(R.menu.action_bar_items);
            setSupportActionBar(toolbar);
            if (!onboarding2) {
                startOnboarding2(toolbar, bottomBar);
            }
            boolean shortcutUsed = shortcutCheck(getIntent().getAction(), bottomBar);
            if (!shortcutUsed && savedInstanceState == null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.placeholder, new TopFragment(), TOPFRAGMENTTAG)
                        .commit();
            }
            bottomBar.setOnTabSelectListener(this, false);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        bottomBar.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Opens the onboarding IntroActivity for first time use. May also be configured to show if new
     * features are added by changing shared preferences
     */
    void startOnboarding() {
        Intent view = new Intent(this, IntroActivity.class);
        startActivity(view);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    /**
     * Google play services check, if the app fails it will prompt the user to update or install
     * services. This is the master check, other checks in the fragments will simply not load the
     * elements affected.
     *
     * @param activity Calling activity context
     *
     * @return True if the correct version of play services is available. False otherwise
     */
    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        boolean newNightMode  = sharedPreferences.getBoolean(getString(R.string.preferences_night_mode), true);
        boolean newTimeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
        boolean newMapCardAllowed = sharedPreferences.getBoolean(getString(R.string.preferences_maps_card), true);
        if (newNightMode != nightMode || newTimeFormat != timeFormat || newMapCardAllowed != mapCardAllowed){
            nightModeSwitching(newNightMode);
            recreate();
        }
        super.onResume();
    }

    /**
     * Checks the use of a shortcut to open the app to set the correct fragment to display as well
     * as its arguments. Since shortcuts are only exposed to devices running N MR1 (the option to
     * create one isn't even exposed to non compatible devices), the usual path for this is to
     * simply set the tab position and therefore TopFragment.
     *
     * @param action The action accompanying the intent to open the app from a shortcut
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private boolean shortcutCheck(String action, BottomBar bottomBar) {
        switch (action) {
            case HOMESHORTCUT:
                ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
                shortcutManager.reportShortcutUsed(getString(R.string.shortcut_home_timetable));
                TimetableFragment fragment = new TimetableFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                manager.beginTransaction()
                        .replace(R.id.placeholder, fragment, TIMETABLETAG)
                        .commit();
                int stopToShow = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), 1);
                firebaseLog(getString(R.string.firebase_home_shortcut_used)
                        , getString(R.string.firebase_stop_id), String.valueOf(stopToShow));
                bottomBar.setDefaultTabPosition(0);
                return true;
            case SPECIFICSHORTCUT:
                ShortcutManager shortcutManager2 = getSystemService(ShortcutManager.class);
                shortcutManager2.reportShortcutUsed(getString(R.string.shortcut_specific_timetable));
                TimetableFragment fragment2 = new TimetableFragment();
                String args = getIntent().getExtras().getString(getString(R.string.shortcut_specific_timetable));
                firebaseLog(getString(R.string.firebase_specific_shortcut_used)
                        , getString(R.string.firebase_stop_id), args);
                fragment2.setArguments(getIntent().getExtras());
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.placeholder, fragment2, TIMETABLETAG)
                        .commit();
                bottomBar.setDefaultTabPosition(0);
                return true;
            default:
                bottomBar.setDefaultTabPosition(1);
                return false;
        }
    }

    /**
     * Starts the second onboarding process involving Tap Targets on important areas. Dont try and
     * access fragment views using this.
     *
     * @param toolbar   To get toolbar views
     * @param bottomBar To get bottomBar views (Thank goodness it extends View)
     */
    void startOnboarding2(Toolbar toolbar, BottomBar bottomBar) {
        TapTargetSequence sequence = new TapTargetSequence(this);
        List<TapTarget> targets = new ArrayList<>();
        targets.add(TapTarget.forToolbarMenuItem(toolbar, R.id.default_stop_icon,
                "Home is where the heart is", "Tap me to set your home stop")
                .targetCircleColor(R.color.primary)
                .outerCircleColor(R.color.onboarding_2_outer_circle)
                .textColor(R.color.textview_inverse_color)
                .transparentTarget(true)
                .drawShadow(true)
                .cancelable(false)
                .id(1));
        targets.add(TapTarget.forView(bottomBar.getTabWithId(R.id.tab_timetable),
                "Tap me", "To view timetables for all stops")
                .targetCircleColor(R.color.primary)
                .outerCircleColor(R.color.onboarding_2_outer_circle)
                .textColor(R.color.textview_inverse_color)
                .transparentTarget(true)
                .drawShadow(true)
                .cancelable(true)
                .id(2));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            targets.add(TapTarget.forView(bottomBar.getTabWithId(R.id.tab_find),
                    "If you want to see stop locations", "Enable the location permission for the app in your phone settings")
                    .targetCircleColor(R.color.primary)
                    .outerCircleColor(R.color.onboarding_2_outer_circle)
                    .textColor(R.color.textview_inverse_color)
                    .cancelable(true)
                    .transparentTarget(true)
                    .drawShadow(true)
                    .id(3));
        } else {
            targets.add(TapTarget.forView(bottomBar.getTabWithId(R.id.tab_find),
                    "Tap me", "To view all the stop locations")
                    .targetCircleColor(R.color.primary)
                    .outerCircleColor(R.color.onboarding_2_outer_circle)
                    .textColor(R.color.textview_inverse_color)
                    .transparentTarget(true)
                    .drawShadow(true)
                    .cancelable(false)
                    .id(4));
        }
        targets.add(TapTarget.forView(bottomBar.getTabWithId(R.id.tab_place),
                "A quick note", "Buses can sometimes run late, especially at rush hour!")
                .targetCircleColor(R.color.primary)
                .outerCircleColor(R.color.onboarding_2_outer_circle)
                .textColor(R.color.textview_inverse_color)
                .transparentTarget(true)
                .cancelable(true)
                .drawShadow(true)
                .id(5));
        sequence.targets(targets);
        sequence.listener(new TapTargetSequence.Listener() {
            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                //Probably should add a warning if target isn't tapped
            }
            @Override
            public void onSequenceFinish() {
                sharedPreferences.edit()
                        .putBoolean(getString(R.string.preferences_onboarding_2), true)
                        .apply();
            }
            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
            }
        });
        sequence.start();
    }

    /**
     * Method to make a firebase analytics event
     *
     * @param event
     * @param param
     * @param value
     */
    private void firebaseLog(String event, String param, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(param, value);
        firebaseAnalytics.logEvent(event, bundle);
    }

    /**
     * The method being called when currently visible BottomBarTab changes to change the fragment
     * viewed.
     *
     *
     * @param tabId the new visible BottomBarTab
     */
    @Override
    public void onTabSelected(@IdRes int tabId) {
        FragmentManager manager = getSupportFragmentManager();
        TopFragment topFragment = (TopFragment) manager.findFragmentByTag(TOPFRAGMENTTAG);
        TimetableFragment timetableFragment = (TimetableFragment) manager.findFragmentByTag(TIMETABLETAG);
        MapsFragment mapsFragment = (MapsFragment) manager.findFragmentByTag(MAPSTAG);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (tabId) {
            case R.id.tab_place:
                toolbar.setTitle(R.string.app_name);
                if (topFragment != null) {
                    topFragment = (TopFragment) getSupportFragmentManager().findFragmentByTag(TOPFRAGMENTTAG);
                    ft.attach(topFragment).commit();
                    return;
                } else {
                    topFragment = new TopFragment();
                    ft.replace(R.id.placeholder, topFragment, TOPFRAGMENTTAG);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
                break;
            case R.id.tab_find:
                toolbar.setTitle(R.string.local_map);
                if (mapsFragment != null) {
                    mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(MAPSTAG);
                    ft.attach(mapsFragment).commit();
                    return;
                } else {
                    mapsFragment = new MapsFragment();
                    ft.replace(R.id.placeholder, mapsFragment, MAPSTAG);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.commit();
                }
                break;
            case R.id.tab_timetable:
                toolbar.setTitle(TermDates.getTimetableName());
                if (timetableFragment != null) {
                    timetableFragment = (TimetableFragment) getSupportFragmentManager().findFragmentByTag(TIMETABLETAG);
                    ft.attach(timetableFragment).commit();
                    return;
                } else {
                    timetableFragment = new TimetableFragment();
                    ft.replace(R.id.placeholder, timetableFragment, TIMETABLETAG);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.commit();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        bottomBar.onSaveInstanceState();
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * Inflates the options menu with correct constituents
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_items, menu);
        return true;
    }

    @Override
    public void onItemSelected(int position) {
        Log.d(TAG, "onItemSelected: " + position);
        int current = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), 0);
        if (position == current) {
            return;
        }
        String[] array = getResources().getStringArray(R.array.bus_stops_home);
        firebaseLog(getString(R.string.firebase_home_stop_changed), getString(R.string.firebase_stop_id), array[position - 1]);
        sharedPreferences.edit().putInt(getString(R.string.preferences_home_bus_stop), position).apply();
        Snackbar snackbar = Snackbar.make(layout, "Home stop changed", Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_dark));
        snackbar.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createShortcut();
        }
        if (getSupportFragmentManager().findFragmentByTag(TOPFRAGMENTTAG) != null &&
                getSupportFragmentManager().findFragmentByTag(TOPFRAGMENTTAG).isVisible()) {
            TopFragment fragment = (TopFragment) getSupportFragmentManager().findFragmentByTag(TOPFRAGMENTTAG);
            fragment.changeHomeCard();
        }
    }

    /**
     * Sets a menu listener for if any of the options on the action bar are selected. If the change
     * stop action was selected, it will inflate a dialog asking which stop the user would like to
     * select to display on the timetable fragment.
     * <p>
     * If the change default stop action is selected, it inflates a dialog to select which stop the
     * user would take into uni, selecting this item will write their option to sharedPreferences
     * which will then save their choice and have it persist. This will then be able to be used by
     * the timetable fragment as the default timetable inflated whenever the app is restarted.
     * <p>
     * If TopFragment is the currently displayed fragment, it will cause the home stop card to
     * change its content to reflect the newly selected stop.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.default_stop_icon:
                new BottomSheet().show(getSupportFragmentManager(), BOTTOMSHEET);
                return true;
            case R.id.action_settings:
                Intent view = new Intent(this, SettingsActivity.class);
                startActivity(view);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates a shortcut for the newly selected home stop. Instead of adding all the time, it
     * overwrites the previous choice
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void createShortcut() {
        ShortcutManager manager = getSystemService(ShortcutManager.class);
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this, getString(R.string.shortcut_home_timetable))
                .setShortLabel(getString(R.string.shortcut_home_timetable_name_short))
                .setLongLabel(getString(R.string.shortcut_home_timetable_name_long))
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_shortcut_timetable))
                .setIntent(new Intent(getString(R.string.shortcut_home_timetable_action))
                        .setPackage(getApplicationInfo().packageName)
                        .setClass(this, TopActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP))
                .build();
        manager.addDynamicShortcuts(Arrays.asList(shortcutInfo));
    }

}


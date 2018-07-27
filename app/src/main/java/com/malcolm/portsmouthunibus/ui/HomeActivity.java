package com.malcolm.portsmouthunibus.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.BuildConfig;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.settings.SettingsActivity;
import com.malcolm.portsmouthunibus.ui.home.HomeFragment;
import com.malcolm.portsmouthunibus.ui.intro.IntroActivity;
import com.malcolm.portsmouthunibus.ui.maps.MapsFragment;
import com.malcolm.portsmouthunibus.ui.popup.QuickStartBottomSheet;
import com.malcolm.portsmouthunibus.ui.popup.StopBottomSheet;
import com.malcolm.portsmouthunibus.ui.timetable.TimetableFragment;
import com.malcolm.unibusutilities.helper.TermDateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        StopBottomSheet.DialogListener, QuickStartBottomSheet.DialogButtonSelectedListener {
    //Initialise local variables
    private static final String TAG = "HomeActivity";
    private static final String HOMESHORTCUT = "com.malcolm.portsmouthunibus.VIEW.TIMETABLE";
    private static final String SPECIFICSHORTCUT = "com.malcolm.portsmouthunibus.VIEW.TIMETABLE.SPECIFIC";
    private static final String HOMEFRAGMENTTAG = "HomeFragment";
    private static final String TIMETABLETAG = "TimetableFragment";
    private static final String MAPSTAG = "MapsFragment";
    private static final String BOTTOMSHEET = "StopBottomSheet";
    private static final String QUICKSTART = "QuickStart";
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    @BindView(R.id.bottom_bar_view)
    BottomNavigationView bottomBar;
    @BindView(R.id.placeholder)
    CoordinatorLayout layout;
    private FirebaseAnalytics firebaseAnalytics;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        boolean onboarding = sharedPreferences.getBoolean(getString(R.string.preferences_onboarding), false);
        //If initial app onboarding has happened or not to set the content screen
        if (!onboarding && !BuildConfig.DEBUG) {
            startOnboarding();
        } else {
            boolean onboarding2 = sharedPreferences.getBoolean(getString(R.string.preferences_onboarding_2), false);
            setContentView(R.layout.activity_home);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.INTERNET}, 0);
            }
            if (savedInstanceState == null) {
                isGooglePlayServicesAvailable(this);
            }
            ButterKnife.bind(this);
            toolbar.inflateMenu(R.menu.action_bar_items);
            setSupportActionBar(toolbar);
            if (!onboarding2) {
                QuickStartBottomSheet sheet = new QuickStartBottomSheet();
                sheet.setCancelable(false);
                sheet.show(getSupportFragmentManager(), QUICKSTART);
            }
            boolean shortcutUsed = shortcutCheck(getIntent(), bottomBar);
            if (!shortcutUsed && savedInstanceState == null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.placeholder, new HomeFragment(), HOMEFRAGMENTTAG)
                        .commitNow();
                bottomBar.setSelectedItemId(R.id.tab_place);
            }
            bottomBar.setOnNavigationItemSelectedListener(this);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Opens the onboarding IntroActivity for first time use. May also be configured to show if new
     * features are added by changing shared preferences
     */
    void startOnboarding() {
        Intent view = new Intent(this, IntroActivity.class);
        startActivity(view);
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


    /**
     * Checks the use of a shortcut to open the app to set the correct fragment to display as well
     * as its arguments. Since shortcuts are only exposed to devices running N MR1 (the option to
     * create one isn't even exposed to non compatible devices), the usual path for this is to
     * simply set the tab position and therefore HomeFragment.
     *
     * @param intent The intent to open the app from a shortcut.
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private boolean shortcutCheck(Intent intent, BottomNavigationView bottomBar) {
        if (intent.getAction() == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1){
            return false;
        }
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        switch (intent.getAction()) {
            case HOMESHORTCUT:
                shortcutManager.reportShortcutUsed(getString(R.string.shortcut_home_timetable));
                bottomBar.setSelectedItemId(R.id.tab_timetable);
                TimetableFragment fragment = new TimetableFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                manager.beginTransaction()
                        .replace(R.id.placeholder, fragment, TIMETABLETAG)
                        .commit();
                toolbar.setTitle(TermDateUtils.getTimetableName());
                int stopToShow = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), 1);
                firebaseLog(getString(R.string.firebase_event_home_shortcut_used)
                        , getString(R.string.firebase_property_stop_id), String.valueOf(stopToShow));
                return true;
            case SPECIFICSHORTCUT:
                shortcutManager = getSystemService(ShortcutManager.class);
                shortcutManager.reportShortcutUsed(getString(R.string.shortcut_specific_timetable));
                TimetableFragment fragment2 = new TimetableFragment();
                String args = getIntent().getExtras().getString(getString(R.string.shortcut_specific_timetable));
                firebaseLog(getString(R.string.firebase_event_specific_shortcut_used)
                        , getString(R.string.firebase_property_stop_id), args);
                fragment2.setArguments(getIntent().getExtras());
                bottomBar.setSelectedItemId(R.id.tab_timetable);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.placeholder, fragment2, TIMETABLETAG)
                        .commit();
                toolbar.setTitle(TermDateUtils.getTimetableName());
                return true;
            default:
                return false;
        }
    }


    private void startQuickStart(Toolbar toolbar) {
        TapTargetView.showFor(this, TapTarget.forToolbarMenuItem(toolbar, R.id.default_stop_icon
                , "Home is where the heart is", "Tap to set your home stop")
                .targetCircleColor(R.color.primary)
                .outerCircleColor(R.color.onboarding_2_outer_circle)
                .textColor(R.color.textview_inverse_color)
                .transparentTarget(true)
                .drawShadow(true)
                .cancelable(false), new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                StopBottomSheet sheet = new StopBottomSheet();
                sheet.setCancelable(false);
                sheet.show(getSupportFragmentManager(), BOTTOMSHEET);
                super.onTargetClick(view);
            }
        });
    }

    private void startOnboardingSequence(final BottomNavigationView bottomBar){
        TapTargetSequence sequence = new TapTargetSequence(this);
        List<TapTarget> targets = new ArrayList<>();
        targets.add(TapTarget.forView(bottomBar.findViewById(R.id.tab_timetable),
                "Timetable tab", "Views the timetables for all the stops for that day")
                .targetCircleColor(R.color.primary)
                .outerCircleColor(R.color.onboarding_2_outer_circle)
                .textColor(R.color.textview_inverse_color)
                .transparentTarget(true)
                .drawShadow(true)
                .cancelable(true)
                .id(1));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            targets.add(TapTarget.forView(bottomBar.findViewById(R.id.tab_find),
                    "If you want to see stop locations", "Enable the location permission for the app in your phone settings")
                    .targetCircleColor(R.color.primary)
                    .outerCircleColor(R.color.onboarding_2_outer_circle)
                    .textColor(R.color.textview_inverse_color)
                    .cancelable(true)
                    .transparentTarget(true)
                    .drawShadow(true)
                    .id(2));
        } else {
            targets.add(TapTarget.forView(bottomBar.findViewById(R.id.tab_find),
                    "Maps tab", "Views all the stop locations")
                    .targetCircleColor(R.color.primary)
                    .outerCircleColor(R.color.onboarding_2_outer_circle)
                    .textColor(R.color.textview_inverse_color)
                    .transparentTarget(true)
                    .drawShadow(true)
                    .cancelable(false)
                    .id(3));
        }
        targets.add(TapTarget.forView(bottomBar.findViewById(R.id.tab_place),
                "Don't forget", "Buses can sometimes run late, especially at rush hour!")
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
                if (targetClicked){
                    switch (lastTarget.id()){
                        case 1:
                            bottomBar.setSelectedItemId(R.id.tab_timetable);
                            break;
                        case 3:
                            bottomBar.setSelectedItemId(R.id.tab_find);
                            break;
                        case 4:
                            bottomBar.setSelectedItemId(R.id.tab_place);
                            break;
                        default:
                            break;
                    }
                }
            }
            @Override
            public void onSequenceFinish() {
                sharedPreferences.edit()
                        .putBoolean(getString(R.string.preferences_onboarding_2), true)
                        .apply();
            }
            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
                sharedPreferences.edit()
                        .putBoolean(getString(R.string.preferences_onboarding_2), true)
                        .apply();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();
        HomeFragment homeFragment = (HomeFragment) manager.findFragmentByTag(HOMEFRAGMENTTAG);
        TimetableFragment timetableFragment = (TimetableFragment) manager.findFragmentByTag(TIMETABLETAG);
        MapsFragment mapsFragment = (MapsFragment) manager.findFragmentByTag(MAPSTAG);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()){
            case R.id.tab_place:
                toolbar.setTitle(R.string.app_name);
                if (homeFragment != null) {
                    homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOMEFRAGMENTTAG);
                    ft.attach(homeFragment).commit();
                } else {
                    homeFragment = new HomeFragment();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.replace(R.id.placeholder, homeFragment, HOMEFRAGMENTTAG);
                    ft.commit();
                }
                return true;
            case R.id.tab_find:
                toolbar.setTitle(R.string.local_map);
                if (mapsFragment != null) {
                    mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(MAPSTAG);
                    ft.attach(mapsFragment).commit();
                } else {
                    mapsFragment = new MapsFragment();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    //ft.setCustomAnimations(R.anim.overshoot_enter_animation, R.anim.fade_out_animation);
                    ft.replace(R.id.placeholder, mapsFragment, MAPSTAG);
                    ft.commit();
                }
                return true;
            case R.id.tab_timetable:
                toolbar.setTitle(TermDateUtils.getTimetableName());
                if (timetableFragment != null) {
                    timetableFragment = (TimetableFragment) getSupportFragmentManager().findFragmentByTag(TIMETABLETAG);
                    ft.attach(timetableFragment).commit();
                } else {
                    timetableFragment = new TimetableFragment();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.replace(R.id.placeholder, timetableFragment, TIMETABLETAG);
                    ft.commit();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_items, menu);
        return true;
    }

    @Override
    public void onButtonSelected(boolean positive) {
        if (positive){
            startQuickStart(toolbar);
        } else {
            sharedPreferences.edit()
                    .putBoolean(getString(R.string.preferences_onboarding_2), true)
                    .apply();
        }
    }


    @Override
    public void onItemSelected(int position) {
        int current = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), -1);
        boolean quickstart = sharedPreferences.getBoolean(getString(R.string.preferences_onboarding_2), false);
        if (position == current) {
            return;
        }
        if (current == -1 && !quickstart){
            startOnboardingSequence(bottomBar);
        }
        String[] array = getResources().getStringArray(R.array.bus_stops_home);
        //firebaseLog(getString(R.string.firebase_event_home_stop_changed), getString(R.string.firebase_property_stop_id), array[position]);
        sharedPreferences.edit().putInt(getString(R.string.preferences_home_bus_stop), position).apply();
        if (getSupportFragmentManager().findFragmentByTag(TIMETABLETAG) != null &&
                getSupportFragmentManager().findFragmentByTag(TIMETABLETAG).isVisible()) {
            TimetableFragment fragment = (TimetableFragment) getSupportFragmentManager().findFragmentByTag(TIMETABLETAG);
            Snackbar snackbar = Snackbar.make(fragment.getLayout(), "Home stop changed", Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_dark));
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(layout, "Home stop changed", Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_dark));
            snackbar.show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createShortcut();
        }
        if (getSupportFragmentManager().findFragmentByTag(HOMEFRAGMENTTAG) != null &&
                getSupportFragmentManager().findFragmentByTag(HOMEFRAGMENTTAG).isVisible()) {
            HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOMEFRAGMENTTAG);
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
     * If HomeFragment is the currently displayed fragment, it will cause the home stop card to
     * change its content to reflect the newly selected stop.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable){
            ((Animatable) drawable).start();
        }
        switch (item.getItemId()) {
            case R.id.default_stop_icon:
                new StopBottomSheet().show(getSupportFragmentManager(), BOTTOMSHEET);
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
                        .setClass(this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP))
                .build();
        manager.addDynamicShortcuts(Collections.singletonList(shortcutInfo));
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(TIMETABLETAG) != null &&
                getSupportFragmentManager().findFragmentByTag(TIMETABLETAG).isVisible()) {
            TimetableFragment fragment = (TimetableFragment) getSupportFragmentManager().findFragmentByTag(TIMETABLETAG);
            // TODO: 04/07/2018 Enable when components is used
            /*if (fragment.getFab().isExpanded()){
                fragment.getFab().setExpanded(false);
                return;
            }*/
        }
        super.onBackPressed();
    }
}


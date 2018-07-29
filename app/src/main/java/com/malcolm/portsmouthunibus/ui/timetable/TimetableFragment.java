package com.malcolm.portsmouthunibus.ui.timetable;


import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.malcolm.portsmouthunibus.BuildConfig;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.ui.HomeActivity;
import com.malcolm.portsmouthunibus.utilities.ImageGenerator;
import com.malcolm.unibusutilities.entity.Times;
import com.malcolm.unibusutilities.helper.TermDateUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.RequestCreator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TimetableFragment extends Fragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, StopPickerAdapter.StopSelectedListener,
        DialogInterface.OnClickListener, Callback, Palette.PaletteAsyncListener {

    private static final String TAG = "TimetableFragment";
    Unbinder unbinder;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.no_timetable)
    TextView noTimetable;
    @BindView(R.id.timetableFragment)
    CoordinatorLayout layout;
    @BindView(R.id.top_layout_next_bus)
    TextView topLayoutNextBus;
    private final Observer<String> countdownObserver = makeCountdownObserver();
    @BindView(R.id.top_layout_stop)
    TextView topLayoutStop;
    @BindView(R.id.timetable_fab)
    FloatingActionButton fab;
    private int currentIconColor;

    @BindView(R.id.top_favourite)
    ImageButton favourite;
    @BindView(R.id.top_image)
    ImageView stopImage;
    @BindView(R.id.top_layout_card)
    CardView topCard;

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
        // TODO: 27/07/2018 change back to butterknife binding
        unbinder = ButterKnife.bind(this, rootView);
        stopsArray = getResources().getStringArray(R.array.bus_stops_spinner);
        setUpRecyclerView();
        return rootView;
    }

    private void setUpRecyclerView() {
        viewedStop = stopNumberGenerator(getStartingStop());
        boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
        TimetableViewModel.Factory factory = new TimetableViewModel.Factory(getActivity().getApplication(), timeFormat, viewedStop);
        viewModel = ViewModelProviders.of(this, factory).get(TimetableViewModel.class);
        Observer<List<Times>> observer = timesList -> {
            if (adapter == null) {
                recyclerView.setHasFixedSize(true);
                adapter = new TimetableFragmentAdapter(getContext(), timesList, viewedStop);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setVisibility(View.VISIBLE);
                LayoutAnimationController animationController =
                        AnimationUtils.loadLayoutAnimation(getContext(), R.anim.home_appear_animation);
                recyclerView.setLayoutAnimation(animationController);
                sharedPreferences.edit().putInt(getString(R.string.preference_last_viewed_stop), viewedStop).apply();
                generateImage(viewedStop);
            } else {
                adapter.swapData(timesList, viewedStop);
                recyclerView.scheduleLayoutAnimation();
                sharedPreferences.edit().putInt(getString(R.string.preference_last_viewed_stop), viewedStop).apply();
                generateImage(viewedStop);
            }
        };
        viewModel.getData().observe(this, observer);
        if (!TermDateUtils.isWeekendInHoliday() && !TermDateUtils.isBankHoliday()) {
            viewModel.getCurrentCountdown().observe(this, countdownObserver);
        } else {
            topLayoutNextBus.setText(getString(R.string.error_no_buses_scheduled));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            favourite.setVisibility(View.VISIBLE);
            favourite.setOnClickListener(l -> buildDialog(viewedStop));
        }
    }

    @Override
    public void onSuccess() {
        Drawable drawable = stopImage.getDrawable();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        new Palette.Builder(bitmap).maximumColorCount(16).generate(this);
    }

    @Override
    public void onError() {

    }

    private void generateImage(int stop){
        int nightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        RequestCreator requestCreator = ImageGenerator.generateImage(getContext(), stop);
        if (nightMode == Configuration.UI_MODE_NIGHT_YES){
            requestCreator.placeholder(R.drawable.image_placeholder_night).into(stopImage, this);
        } else {
            requestCreator.placeholder(R.drawable.image_placeholder).into(stopImage, this);
        }
    }

    @Override // TODO: 27/07/2018 Switch text color when components stable and fonts sorted
    public void onGenerated(@NonNull Palette palette) {
        Palette.Swatch swatch = palette.getMutedSwatch();
        if (swatch != null){
            if (topCard.getVisibility() != View.VISIBLE){
                topCard.setCardBackgroundColor(swatch.getRgb());
                topLayoutNextBus.setTextColor(swatch.getTitleTextColor());
                topLayoutStop.setTextColor(swatch.getBodyTextColor());
                currentIconColor = swatch.getTitleTextColor();
                favourite.getDrawable().setTint(currentIconColor);
                setupFab(swatch);
                animateCardVisibility();
            } else {
                animateColorChanges(swatch);
            }
        } else {
            swatch = palette.getVibrantSwatch();
            if (topCard.getVisibility() != View.VISIBLE) {
                topCard.setCardBackgroundColor(swatch.getRgb());
                topLayoutNextBus.setTextColor(swatch.getTitleTextColor());
                topLayoutStop.setTextColor(swatch.getBodyTextColor());
                currentIconColor = swatch.getTitleTextColor();
                favourite.getDrawable().setTint(currentIconColor);
                setupFab(swatch);
                animateCardVisibility();
            } else {
                animateColorChanges(swatch);
            }
        }
    }

    private void animateCardVisibility(){
        ValueAnimator visibilityAnimator = ValueAnimator.ofFloat(0f, 1f);
        visibilityAnimator.setDuration(200);
        visibilityAnimator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            topCard.setAlpha(alpha);
        });
        topCard.setVisibility(View.VISIBLE);
        visibilityAnimator.start();
    }

    private void animateColorChanges(Palette.Swatch swatch){
        currentIconColor = swatch.getTitleTextColor();
        ObjectAnimator titleAnimation = ObjectAnimator.ofObject(topLayoutStop, "textColor"
                , new ArgbEvaluator(), topLayoutStop.getTextColors().getDefaultColor(), swatch.getBodyTextColor());
        ObjectAnimator textAnimation = ObjectAnimator.ofObject(topLayoutNextBus, "textColor",
                new ArgbEvaluator(), topLayoutNextBus.getTextColors().getDefaultColor(), swatch.getTitleTextColor());
        ObjectAnimator cardAnimation = ObjectAnimator.ofObject(topCard, "cardBackgroundColor",
                new ArgbEvaluator(), topCard.getCardBackgroundColor().getDefaultColor(), swatch.getRgb());
        ObjectAnimator imageAnimation = ObjectAnimator.ofObject(favourite.getDrawable(),
                "tint", new ArgbEvaluator(),currentIconColor, swatch.getBodyTextColor());
        ValueAnimator fabAnimation = ValueAnimator.ofArgb(fab.getBackgroundTintList().getDefaultColor(), swatch.getRgb());
        fabAnimation.addUpdateListener(animation -> fab.setBackgroundTintList(ColorStateList.valueOf((int) animation.getAnimatedValue())));
        ObjectAnimator iconAnimation;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            VectorDrawableCompat drawable = (VectorDrawableCompat) fab.getDrawable();
            iconAnimation = ObjectAnimator.ofObject(drawable, "tint", new ArgbEvaluator(),
                    currentIconColor, swatch.getBodyTextColor());
        } else {
            VectorDrawable drawable = (VectorDrawable) fab.getDrawable();
            iconAnimation = ObjectAnimator.ofObject(drawable, "tint", new ArgbEvaluator(),
                    currentIconColor, swatch.getBodyTextColor());
        }
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.setDuration(200);
        set.playTogether(titleAnimation, textAnimation, cardAnimation, imageAnimation, fabAnimation, iconAnimation);
        set.start();
    }

    private void setupFab(@Nullable Palette.Swatch swatch) {
        //StopPickerAdapter pickerAdapter = new StopPickerAdapter(this, stopsArray);
        //pickerList.setLayoutManager(new LinearLayoutManager(getContext()));
        //pickerList.setAdapter(pickerAdapter);
        if (swatch != null){
            fab.setBackgroundTintList(ColorStateList.valueOf(swatch.getRgb()));
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                VectorDrawableCompat drawableCompat = (VectorDrawableCompat) fab.getDrawable();
                drawableCompat.setTint(swatch.getBodyTextColor());
            } else {
                VectorDrawable drawable = (VectorDrawable) fab.getDrawable();
                drawable.setTint(swatch.getBodyTextColor());
            }
        }
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
        int stop = stopNumberGenerator(position);
        if (stop == viewedStop){
            return;
        }
        boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
        topLayoutStop.setText(stopsArray[position]);
        if (position == 10) {
            if (TermDateUtils.isHoliday() || TermDateUtils.isWeekend()) {
                viewedStop = 1;
                recyclerView.setVisibility(View.GONE);
                noTimetable.setText(R.string.error_eastney);
                noTimetable.setVisibility(View.VISIBLE);
                adapter.clearData();
                viewModel.updateStopList(0, timeFormat);//Reports back as -1 to hide the next stop TextView
                generateImage(viewedStop);
                return;
            }
        }
        viewedStop = stop;
        String[] array = getContext().getResources().getStringArray(R.array.bus_stops_spinner);
        viewModel.changeListOfStops(viewedStop, timeFormat);
        noTimetable.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        if (!TermDateUtils.isWeekendInHoliday() && !TermDateUtils.isBankHoliday()) {
            if (viewModel.getCurrentCountdown().hasActiveObservers()) {
                viewModel.updateStopList(viewedStop, timeFormat);
            } else {
                viewModel.getCurrentCountdown().observe(this, countdownObserver);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.firebase_property_stop_id), array[position]);
        firebaseAnalytics.logEvent(getString(R.string.firebase_event_timetable_changed_stop), bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            favourite.setOnClickListener(null);
            favourite.setOnClickListener(l -> buildDialog(viewedStop));
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
                .setPositiveButton("Okay", (dialog, which) -> saveShortcut(stop))
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
        Objects.requireNonNull(manager).addDynamicShortcuts(Collections.singletonList(shortcutInfo));
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



    @Override // TODO: 27/07/2018 Activate when components stable
    public void onStopSelected(int position) {
        int stop = stopNumberGenerator(position);
        if (stop == viewedStop){
            return;
        }
        boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
        topLayoutStop.setText(stopsArray[position]);
        if (position == 10) {
            if (TermDateUtils.isHoliday() || TermDateUtils.isWeekend()) {
                recyclerView.setVisibility(View.GONE);
                noTimetable.setText(R.string.error_eastney);
                noTimetable.setVisibility(View.VISIBLE);
                adapter.clearData();
                viewModel.updateStopList(0, timeFormat);//Reports back as -1 to hide the next stop TextView
                generateImage(stop);
                return;
            }
        }
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
            favourite.setOnClickListener(null);
            favourite.setOnClickListener(l -> buildDialog(stop));
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
                topLayoutNextBus.setText(getText(R.string.error_no_buses_scheduled));
                return;
            }
            topLayoutNextBus.setVisibility(View.VISIBLE);
            if (s.equals(String.valueOf(Integer.MAX_VALUE))) {
                topLayoutNextBus.setText(getText(R.string.error_no_buses));
            } else {
                topLayoutNextBus.setText(getString(R.string.timetable_next_bus, s));
            }
        };
    }


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

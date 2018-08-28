package com.malcolm.portsmouthunibus.ui.timetable;


import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transformation.TransformationChildCard;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TimetableFragment extends Fragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, StopPickerAdapter.StopSelectedListener,
        Callback, Palette.PaletteAsyncListener {

    private static final String TAG = "TimetableFragment";
    private Unbinder unbinder;
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
    MaterialButton favourite;
    @BindView(R.id.top_image)
    ImageView stopImage;
    @BindView(R.id.top_layout_card)
    CardView topCard;

    @BindView(R.id.picker_recycler_view)
    RecyclerView pickerList;
    @BindView(R.id.scrim)
    View scrim;
    @BindView(R.id.sheet)
    TransformationChildCard childCard;
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
        return rootView;
    }

    private void setUpRecyclerView() {
        viewedStop = getStartingStop();
        boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
        TimetableViewModel.Factory factory = new TimetableViewModel.Factory(getActivity().getApplication(), timeFormat, viewedStop);
        viewModel = ViewModelProviders.of(this, factory).get(TimetableViewModel.class);
        Observer<List<Times>> observer = timesList -> {
            // FIXME: 07/08/2018 eastney bus doesnt display error if shortcut and not running
            // FIXME: 07/08/2018 eastney bus when shortcut and nor running does not indicate no running buses on countdown
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

    private void generateImage(int stop) {
        int nightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        RequestCreator requestCreator = ImageGenerator.generateImage(getContext(), stop);
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            requestCreator.placeholder(R.drawable.image_placeholder_night).into(stopImage, this);
        } else {
            requestCreator.placeholder(R.drawable.image_placeholder).into(stopImage, this);
        }
    }

    @Override // TODO: 27/07/2018 Switch text color when components stable and fonts sorted
    public void onGenerated(@NonNull Palette palette) {
        Palette.Swatch swatch = palette.getMutedSwatch();
        if (swatch != null) {
            if (topCard.getVisibility() != View.VISIBLE) {
                topCard.setCardBackgroundColor(swatch.getRgb());
                childCard.setBackgroundTintList(ColorStateList.valueOf(swatch.getRgb()));
                topLayoutNextBus.setTextColor(swatch.getTitleTextColor());
                topLayoutStop.setTextColor(swatch.getBodyTextColor());
                currentIconColor = swatch.getTitleTextColor();
                favourite.getIcon().setTint(swatch.getTitleTextColor());
                favourite.setTextColor(swatch.getTitleTextColor());
                favourite.setStrokeColor(ColorStateList.valueOf(swatch.getTitleTextColor()));
                setupFab(swatch);
                animateCardVisibility();
            } else {
                animateColorChanges(swatch);
            }
        } else {
            swatch = palette.getVibrantSwatch();
            if (swatch != null) {
                if (topCard.getVisibility() != View.VISIBLE) {
                    topCard.setCardBackgroundColor(swatch.getRgb());
                    childCard.setBackgroundTintList(ColorStateList.valueOf(swatch.getRgb()));
                    topLayoutNextBus.setTextColor(swatch.getTitleTextColor());
                    topLayoutStop.setTextColor(swatch.getBodyTextColor());
                    currentIconColor = swatch.getTitleTextColor();
                    favourite.getIcon().setTint(swatch.getTitleTextColor());
                    favourite.setTextColor(swatch.getTitleTextColor());
                    favourite.setStrokeColor(ColorStateList.valueOf(swatch.getTitleTextColor()));
                    setupFab(swatch);
                    animateCardVisibility();
                } else {
                    animateColorChanges(swatch);
                }
            }
        }
    }

    private void animateCardVisibility() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            int currentFavourite = sharedPreferences.getInt(getString(R.string.preferences_favourite), -1);
            if (currentFavourite == viewedStop) {
                ValueAnimator buttonAnimation = ValueAnimator.ofArgb(favourite.getIconTint().getDefaultColor(),
                        ContextCompat.getColor(getContext(), R.color.gold));
                buttonAnimation.addUpdateListener(animation -> {
                    favourite.setIconTint(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                    favourite.setStrokeColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                    favourite.setTextColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                    favourite.setText(R.string.favorited);
                });
                buttonAnimation.setInterpolator(new FastOutSlowInInterpolator());
                buttonAnimation.setDuration(200);
                buttonAnimation.start();
                favourite.setText(R.string.favorited);
            }
            favourite.setVisibility(View.VISIBLE);
            favourite.setOnClickListener(l -> buildDialog(viewedStop));
        }
        ValueAnimator visibilityAnimator = ValueAnimator.ofFloat(0f, 1f);
        visibilityAnimator.setStartDelay(200);
        visibilityAnimator.setDuration(300);
        visibilityAnimator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            topCard.setAlpha(alpha);
        });
        topCard.setVisibility(View.VISIBLE);
        visibilityAnimator.start();
    }

    private void animateColorChanges(Palette.Swatch swatch) {
        currentIconColor = swatch.getTitleTextColor();
        ObjectAnimator titleAnimation = ObjectAnimator.ofObject(topLayoutStop, "textColor"
                , new ArgbEvaluator(), topLayoutStop.getTextColors().getDefaultColor(), swatch.getBodyTextColor());
        ObjectAnimator textAnimation = ObjectAnimator.ofObject(topLayoutNextBus, "textColor",
                new ArgbEvaluator(), topLayoutNextBus.getTextColors().getDefaultColor(), swatch.getTitleTextColor());
        ObjectAnimator cardAnimation = ObjectAnimator.ofObject(topCard, "cardBackgroundColor",
                new ArgbEvaluator(), topCard.getCardBackgroundColor().getDefaultColor(), swatch.getRgb());
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            int currentFavourite = sharedPreferences.getInt(getString(R.string.preferences_favourite), -1);
            ValueAnimator buttonAnimation;
            if (currentFavourite == viewedStop) {
                buttonAnimation = ValueAnimator.ofArgb(favourite.getIconTint().getDefaultColor(),
                        ContextCompat.getColor(getContext(), R.color.gold));
                buttonAnimation.addUpdateListener(animation -> {
                    favourite.setIconTint(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                    favourite.setStrokeColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                    favourite.setTextColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                    favourite.setText(R.string.favorited);
                });
            } else {
                buttonAnimation = ValueAnimator.ofArgb(favourite.getTextColors().getDefaultColor(),
                        swatch.getBodyTextColor());
                buttonAnimation.addUpdateListener(animation -> {
                    favourite.setIconTint(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                    favourite.setStrokeColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                    favourite.setTextColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                    favourite.setText(R.string.set_favourite);
                });
            }
            set.playTogether(titleAnimation, textAnimation, cardAnimation, fabAnimation, iconAnimation, buttonAnimation);
        } else {
            set.playTogether(titleAnimation, textAnimation, cardAnimation, fabAnimation, iconAnimation);
        }
        set.start();
        childCard.setBackgroundTintList(ColorStateList.valueOf(swatch.getRgb()));
    }

    private void setupFab(@Nullable Palette.Swatch swatch) {
        StopPickerAdapter pickerAdapter = new StopPickerAdapter(this, stopsArray);
        pickerList.setLayoutManager(new LinearLayoutManager(getContext()));
        pickerList.setHasFixedSize(true);
        pickerList.setAdapter(pickerAdapter);
        if (swatch != null) {
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
        fab.setOnClickListener(l -> {
            pickerAdapter.setPickerColors(topCard.getCardBackgroundColor(), favourite.getTextColors());
            pickerAdapter.notifyDataSetChanged();
            fab.setExpanded(!fab.isExpanded());
        });
        scrim.setOnClickListener(l -> fab.setExpanded(false));
    }

    private int getStartingStop() {
        if (getArguments() != null) {
            int pref = getArguments().getInt(getString(R.string.shortcut_specific_timetable));
            if (pref == 1) {
                topLayoutStop.setText(stopsArray[10]);
            } else {
                topLayoutStop.setText(stopsArray[pref - 1]);
            }
            return pref;
        } else {
            int pref = sharedPreferences.getInt(getString(R.string.preferences_home_bus_stop), 0);
            if (pref == -2) {//If no default stop set
                topLayoutStop.setText(stopsArray[0]);
                return 0;
            } else {
                topLayoutStop.setText(stopsArray[pref]);
                return stopNumberGenerator(pref);
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
        new AlertDialog.Builder(getContext())
                .setTitle("Add Shortcut")
                .setMessage(R.string.dialog_create_shortcut)
                .setPositiveButton("Okay", (dialog, which) -> saveShortcut(stop))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Saves the shortcut chosen by the user
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void saveShortcut(int stopToSave) {
        String shortcutLong = getResources().getStringArray(R.array.bus_stops_shortcuts_long)[stopToSave - 1];
        String shortcutShort = getResources().getStringArray(R.array.bus_stops_shortcuts_short)[stopToSave - 1];
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
        ValueAnimator buttonAnimation = ValueAnimator.ofArgb(favourite.getIconTint().getDefaultColor(),
                ContextCompat.getColor(getContext(), R.color.gold));
        buttonAnimation.addUpdateListener(animation -> {
            favourite.setIconTint(ColorStateList.valueOf((int) animation.getAnimatedValue()));
            favourite.setStrokeColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
            favourite.setTextColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
            favourite.setText(R.string.favorited);
        });
        buttonAnimation.setInterpolator(new FastOutSlowInInterpolator());
        buttonAnimation.setDuration(200);
        buttonAnimation.start();
        favourite.setText(R.string.favorited);
        sharedPreferences.edit().putInt(getString(R.string.preferences_favourite), stopToSave).apply();
    }

    @Override
    public void onStopSelected(int position) {
        int stop = stopNumberGenerator(position);
        if (stop == viewedStop) {
            return;
        }
        boolean timeFormat = sharedPreferences.getBoolean(getString(R.string.preferences_24hourclock), true);
        topLayoutStop.setText(stopsArray[position]);
        String[] array = getResources().getStringArray(R.array.bus_stops_spinner);
        if (position == 10) {
            if (TermDateUtils.isHoliday() || TermDateUtils.isWeekend()) {
                viewedStop = 1;
                recyclerView.setVisibility(View.GONE);
                noTimetable.setText(R.string.error_eastney);
                noTimetable.setVisibility(View.VISIBLE);
                adapter.clearData();
                viewModel.updateStopList(0, timeFormat);//Reports back as -1 to hide the next stop TextView
                generateImage(viewedStop);
                fab.setExpanded(false);
                return;
            }
        }
        viewedStop = stop;
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
        fab.setExpanded(false);
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
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    public CoordinatorLayout getLayout() {
        return layout;
    }
}

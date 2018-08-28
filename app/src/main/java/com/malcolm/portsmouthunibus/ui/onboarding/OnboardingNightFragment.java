package com.malcolm.portsmouthunibus.ui.onboarding;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.malcolm.portsmouthunibus.App;
import com.malcolm.portsmouthunibus.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OnboardingNightFragment extends BaseOnboardingFragment {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.button_complete)
    Button complete;
    @BindView(R.id.checkBox)
    CheckBox checkBox;
    @BindView(R.id.switch1)
    Switch aSwitch;
    @BindView(R.id.layout)
    ConstraintLayout layout;
    private AnimatorSet set;
    private ValueAnimator lottie;
    private ObjectAnimator backgroundColor;
    private ValueAnimator textColor;
    private ValueAnimator buttonColor;
    private ValueAnimator statusColor;
    private ObjectAnimator switchInvisibility;
    private ObjectAnimator switchVisibility;
    private static final long ANIM_TIME = 1200;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("CommitPrefEdits")
        final View rootView = inflater.inflate(R.layout.fragment_onboarding_night, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        lottieAnimationView.setAnimation("onboarding_night.json");
        backgroundColor = ObjectAnimator.ofArgb(layout, "backgroundColor",
                Color.parseColor("#F5F5F5"), Color.parseColor("#121212"));
        lottie = ValueAnimator.ofFloat(0.0f, 1.0f);
        textColor = ValueAnimator.ofArgb(Color.parseColor("#DE000000"), Color.parseColor("#ffffffff"));
        textColor.addUpdateListener(animation -> {
            title.setTextColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
            aSwitch.setTextColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
            checkBox.setTextColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
        });
        buttonColor = ValueAnimator.ofArgb(Color.parseColor("#621360"), Color.parseColor("#00A0FF"));
        buttonColor.addUpdateListener(animation -> {
            complete.setTextColor(ColorStateList.valueOf((int) animation.getAnimatedValue()));
            checkBox.setButtonTintList(ColorStateList.valueOf((int) animation.getAnimatedValue()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                aSwitch.setThumbTintList(ColorStateList.valueOf((int) animation.getAnimatedValue()));
                aSwitch.setTrackTintList(ColorStateList.valueOf((int) animation.getAnimatedValue()));
            }
        });
        statusColor = ValueAnimator.ofArgb(Color.parseColor("#350036"), Color.parseColor("#1d0018"));
        statusColor.addUpdateListener(animation -> {
            getActivity().getWindow().setNavigationBarColor((Integer) animation.getAnimatedValue());
            getActivity().getWindow().setStatusBarColor((Integer) animation.getAnimatedValue());
        });
        lottie.addUpdateListener(valueAnimator -> lottieAnimationView.setProgress((Float) valueAnimator.getAnimatedValue()));
        switchInvisibility = ObjectAnimator.ofFloat(aSwitch, "alpha", 1, 0).setDuration(ANIM_TIME);
        switchInvisibility.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                aSwitch.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        switchVisibility = ObjectAnimator.ofFloat(aSwitch, "alpha", 0, 1).setDuration(ANIM_TIME);
        switchVisibility.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                aSwitch.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set = new AnimatorSet();
        set.setDuration(ANIM_TIME);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                checkBox.setClickable(false);
                aSwitch.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                checkBox.setClickable(true);
                aSwitch.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.playTogether(backgroundColor, lottie, textColor, buttonColor, statusColor);
        aSwitch.setClickable(false);
        animateNightMode();
        checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked){
                aSwitch.setClickable(false);
                animateNightMode();
                switchInvisibility.start();
            } else {
                aSwitch.setClickable(true);
                switchVisibility.start();
                if (aSwitch.isChecked()){
                    nightToDay();
                } else {
                    dayToNight();
                }
            }
        });
        aSwitch.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked){
                nightToDay();
            } else {
                dayToNight();
            }

        });
        complete.setOnClickListener(l -> {
            if (checkBox.isChecked()){
                sharedPreferences.edit().putString(getString(R.string.preferences_night_mode_new), String.valueOf(AppCompatDelegate.MODE_NIGHT_AUTO)).apply();
                App.nightModeSwitching(AppCompatDelegate.MODE_NIGHT_AUTO);
            } else {
                if (aSwitch.isChecked()){
                    sharedPreferences.edit().putString(getString(R.string.preferences_night_mode_new), String.valueOf(AppCompatDelegate.MODE_NIGHT_NO)).apply();
                    App.nightModeSwitching(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    sharedPreferences.edit().putString(getString(R.string.preferences_night_mode_new), String.valueOf(AppCompatDelegate.MODE_NIGHT_YES)).apply();
                    App.nightModeSwitching(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
            listener.onOnboardingFinished();
        });
        return rootView;
    }

    private void dayToNight(){
        if (lottieAnimationView.getProgress() < 0.95f){
            set.start();
        }
    }

    private void nightToDay(){
        if (lottieAnimationView.getProgress() != 0.0f) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                set.reverse();
            } else {
                lottie.setDuration(ANIM_TIME);
                lottie.reverse();
                backgroundColor.setDuration(ANIM_TIME);
                backgroundColor.reverse();
                textColor.setDuration(ANIM_TIME);
                textColor.reverse();
                buttonColor.setDuration(ANIM_TIME);
                buttonColor.reverse();
                statusColor.setDuration(ANIM_TIME);
                statusColor.reverse();
            }
        }
    }

    private void animateNightMode(){
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            dayToNight();
        } else {
            nightToDay();
        }
    }
}

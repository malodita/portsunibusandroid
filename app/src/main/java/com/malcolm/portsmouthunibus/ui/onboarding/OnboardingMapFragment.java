package com.malcolm.portsmouthunibus.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.malcolm.portsmouthunibus.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OnboardingMapFragment extends BaseOnboardingFragment {

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottie;
    @BindView(R.id.map_yes)
    Button mapYes;
    @BindView(R.id.map_no)
    Button mapNo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_onboarding_map, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        lottie.setAnimation("onboarding_location.json");
        lottie.setRepeatCount(LottieDrawable.INFINITE);
        lottie.playAnimation();
        mapNo.setOnClickListener(l -> {
            sharedPreferences.edit()
                    .putBoolean(getString(R.string.preferences_maps_card), false)
                    .putBoolean(getString(R.string.preferences_instant_card), false)
                    .apply();
            listener.onPageFinished();
        });
        mapYes.setOnClickListener(l -> listener.onPageFinished());
        return rootView;
    }


}

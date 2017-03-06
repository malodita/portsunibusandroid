package com.malcolm.portsmouthunibus.intro;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.TopActivity;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

/**
 * Created by Malcolm on 01/01/2017.
 * The purpose of this activity is to establish a walkthrough. It uses the MaterialIntroScreen library.
 * At the end it changes the shared preferences file to true so it only shows on first run
 */

public class IntroActivity extends MaterialIntroActivity {
    //Todo: Finish Onboarding
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackButtonVisible();
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.intro_background)
                .buttonsColor(R.color.intro_button)
                .image(R.drawable.onboarding_1)
                .title("Helping you be on time\n" +
                        "all the time")
                .description("The unofficial Portsmouth Uni Bus app")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.intro_background)
                .buttonsColor(R.color.intro_button)
                .image(R.drawable.onboarding_2)
                .title("Straight to the point")
                .description("The next bus from your home stop to uni\n" +
                        "is always the first thing you see")
                .build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.intro_background)
                    .buttonsColor(R.color.intro_button)
                    .image(R.drawable.onboarding_3)
                    .title("Always know the way")
                    .description("See your nearest bus stop\n" +
                            "available once you grant location access")
                    .possiblePermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION})
                    .build(), new MessageButtonBehaviour("Thanks!"));
        } else {
            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.intro_background)
                    .buttonsColor(R.color.intro_button)
                    .image(R.drawable.onboarding_3)
                    .title("Always know the way")
                    .description("See your nearest bus stop\n" +
                            "available when you need it")
                    .build());
        }

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.intro_background)
                .buttonsColor(R.color.intro_button)
                .image(R.drawable.onboarding_4)
                .title("Plan ahead")
                .description("The timetable for every stop\n" +
                        " at your fingertips. Including\n" +
                        "weekend and holiday service")
                .build());
        addSlide(new SlideFragmentBuilder()
                .image(R.drawable.onboarding_5)
                .backgroundColor(R.color.intro_night)
                .buttonsColor(R.color.intro_night_button)
                .title("Night mode")
                .description("For when things get dark")
                .build());
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.intro_background)
                .buttonsColor(R.color.intro_button)
                .image(R.drawable.onboarding_6)
                .title("That's everything!")
                .description("Ready to get started?")
                .build());
    }

    @Override
    public void onFinish() {
        super.onFinish();
        SharedPreferences settings = getSharedPreferences(getString(R.string.onboarding_key),0);
        settings.edit()
                .putBoolean("onboarding",true)
                .apply();
        Intent view = new Intent(this, TopActivity.class);
        view.setAction(Intent.ACTION_MAIN);
        startActivity(view);
        finish();
    }
}

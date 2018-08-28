package com.malcolm.portsmouthunibus.ui.onboarding;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.ui.HomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroActivity extends AppCompatActivity implements IntroFragment.IntroAnimationListener,
        OnboardingStopFragment.OnboardingProgressListener, ViewPager.PageTransformer {

    private static final String TAG = "Intro";
    private SharedPreferences sharedPreferences;
    @BindView(R.id.intro_placeholder)
    FrameLayout placeholder;
    @BindView(R.id.viewpager)
    NonSwipeViewPager viewPager;
    IntroFragment introFragment;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
        introFragment = new IntroFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.intro_placeholder, introFragment, "Intro")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitNow();
    }

    @Override
    public void onIntroAnimationFinished() {
        boolean onboarding = sharedPreferences.getBoolean(getString(R.string.preferences_new_onboarding), false);
        if (onboarding) {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(introFragment)
                    .commit();
            final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPager.setPageTransformer(false,this);
            viewPager.setAdapter(adapter);
            ObjectAnimator animator = ObjectAnimator.ofFloat(viewPager, "alpha", 0, 1)
                    .setDuration(400);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    placeholder.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                    viewPager.setAdapter(adapter);
                    viewPager.setAllowedSwipeDirection(NonSwipeViewPager.SwipeDirection.none);
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
            animator.start();
        }
    }

    @Override
    public void onPageFinished() {
        currentPage++;
        viewPager.setCurrentItem(currentPage);
    }


    @Override
    public void onPermissionRejected() {
        sharedPreferences.edit()
                .putBoolean(getString(R.string.preferences_maps_card), false)
                .putBoolean(getString(R.string.preferences_instant_card), false)
                .apply();
        currentPage = currentPage + 2;
        viewPager.setCurrentItem(currentPage);
    }

    @Override
    public void onOnboardingFinished() {
        sharedPreferences.edit()
                .putBoolean(getString(R.string.preferences_new_onboarding), true)
                .apply();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setVisibility(View.INVISIBLE);
        page.setAlpha(0);
        if(position == 0){
            if (page.getAlpha() != 1){
                ObjectAnimator animator = ObjectAnimator.ofFloat(page, "alpha", 0, 1).setDuration(700);
                animator.setStartDelay(600);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        page.setVisibility(View.VISIBLE);
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
                animator.start();
            }
        } else {
            if (page.getAlpha() > 0.9){
                ObjectAnimator animator = ObjectAnimator.ofFloat(page, "alpha", 1, 0).setDuration(500);
                animator.setStartDelay(150);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        page.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animator.start();
            }
        }
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private static final int NUMBER_OF_PAGES = 4;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new OnboardingStopFragment();
                case 1:
                    return new OnboardingPermissionFragment();
                case 2:
                    return new OnboardingMapFragment();
                case 3:
                    return new OnboardingNightFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }
    }

}

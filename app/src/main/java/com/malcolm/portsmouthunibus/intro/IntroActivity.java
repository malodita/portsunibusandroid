package com.malcolm.portsmouthunibus.intro;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.TopActivity;
import com.pixelcan.inkpageindicator.InkPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    private static final String TAG = "Intro";
    private final float[] ANIMATION_TIMES = {0f, 0.11f, 0.30f, 0.63f, 1f};
    private final int[] BACKGROUND_COLOURS = {0xFF4c2466, 0xFFFFC107, 0xFF4CAF50, 0xFF09040d};
    private final int[] BACKGROUND_COLOURS_NIGHT = { 0XFF190c22, 0xFF846300, 0xFF134116, 0xFF09040d};
    private final int LOCATION_PERMISSION_REQUEST_CODE = 5384;
    private int locationPermission;
    @BindView(R.id.layout)
    ConstraintLayout layout;
    @BindView(R.id.viewpager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.lottie)
    LottieAnimationView lottie;
    @BindView(R.id.button2)
    Button button;
    @BindView(R.id.intro)
    TextView intro;
    @BindView(R.id.indicator)
    InkPageIndicator indicator;
    @BindView(R.id.button_complete)
    Button buttonComplete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }
        locationPermission =  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        viewPager.setAllowedSwipeDirection(NonSwipeViewPager.SwipeDirection.right);
        viewPager.addOnPageChangeListener(this);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        LottieComposition.Factory.fromAssetFileName(this, "onboarding.json", composition -> {
            if (composition != null) {
                lottie.setComposition(composition);
                lottie.useHardwareAcceleration();
                animateIntro();
            }
        });
        button.setOnClickListener(view -> {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        viewPager.setPagingEnabled(true);
        ObjectAnimator buttonVisibility = ObjectAnimator.ofFloat(button, View.ALPHA, 1, 0).setDuration(1000);
        buttonVisibility.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                button.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        buttonVisibility.start();
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions can be changed in the device settings", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void animateIntro(){
        ObjectAnimator introVisibility = ObjectAnimator.ofFloat(intro, View.ALPHA, 1, 0).setDuration(1500);
        introVisibility.setStartDelay(3000);
        ObjectAnimator lottieVisibility = ObjectAnimator.ofFloat(lottie, View.ALPHA, 0, 1).setDuration(500);
        ObjectAnimator pagerVisibility = ObjectAnimator.ofFloat(viewPager, View.ALPHA, 0, 1).setDuration(500);
        ObjectAnimator indicatorVisibility = ObjectAnimator.ofFloat(indicator, View.ALPHA, 0, 1).setDuration(500);
        lottieVisibility.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                intro.setVisibility(View.GONE);
                lottie.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                indicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                AnimatorSet set = makeAnimation(0, getDelay(0));
                set.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        AnimatorSet set = new AnimatorSet();
        AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(pagerVisibility, lottieVisibility, indicatorVisibility);
        set.playSequentially(introVisibility, set2);
        set.start();
    }

    private long getDelay(int page){
        switch (page) {
            case 0:
                return 3000;
            case 1:
                return 5000;
            case 2:
                return 6000;
            case 3:
                return 6000;
            default:
                return 5000;
        }
    }

    private AnimatorSet makeAnimation(int page, long duration) {
        int color = Color.TRANSPARENT;
        Drawable drawable = layout.getBackground();
        if (drawable instanceof ColorDrawable){
            color = ((ColorDrawable) drawable).getColor();
        }
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        int[] colorArray;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            colorArray = BACKGROUND_COLOURS_NIGHT;
        } else {
            colorArray = BACKGROUND_COLOURS;
        }
        ValueAnimator colour = ValueAnimator.ofObject(new ArgbEvaluator()
                , color
                , colorArray[page])
                .setDuration(duration / 12);
        colour.setStartDelay((long) (duration / 3.333333));
        colour.addUpdateListener(valueAnimator -> {
            int colorId = (int) valueAnimator.getAnimatedValue();
            layout.setBackgroundColor(colorId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(colorId);
            }
        });
        ValueAnimator time = ValueAnimator.ofFloat(ANIMATION_TIMES[page], ANIMATION_TIMES[page + 1])
                .setDuration(duration);
        time.addUpdateListener(animation -> lottie.setProgress(Math.abs((Float) animation.getAnimatedValue())));
        AnimatorSet set = new AnimatorSet();
        if (page == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && locationPermission != PackageManager.PERMISSION_GRANTED){
            viewPager.setPagingEnabled(false);
            button.setVisibility(View.VISIBLE);
            ObjectAnimator buttonVisibility = ObjectAnimator.ofFloat(button, View.ALPHA, 0, 1).setDuration(1000);
            buttonVisibility.setStartDelay(duration / 2);
            set.playTogether(colour, time, buttonVisibility);
        } else {
            viewPager.setPagingEnabled(true);
            if (button.isShown()) {
                ObjectAnimator buttonVisibility = ObjectAnimator.ofFloat(button, View.ALPHA, 0, 1).setDuration(1000);
                buttonVisibility.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        button.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                buttonVisibility.start();
            }
            set.playTogether(colour, time);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    viewPager.setPagingEnabled(false);

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    viewPager.setPagingEnabled(true);
                    if (page == 3){
                        showLastButton();
                    }
                }
                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        return set;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        AnimatorSet set = makeAnimation(position, getDelay(position));
        set.start();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void showLastButton(){
        buttonComplete.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(buttonComplete, View.ALPHA,  0, 1)
                .setDuration(1000);
        animator.start();
        buttonComplete.setOnClickListener(view -> {
            SharedPreferences settings = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
            settings.edit()
                    .putBoolean(getString(R.string.preferences_onboarding),true)
                    .apply();
            Intent intent = new Intent(this, TopActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            startActivity(intent);
            finish();
        });
    }

    public static class BlankFragment extends Fragment {

        private TextView title;
        private TextView subtitle;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View root = inflater.inflate(R.layout.fragment_blank, container, false);
            title = root.findViewById(R.id.title);
            subtitle = root.findViewById(R.id.subtitle);
            setPageText(getArguments().getInt("page"));
            return root;
        }

        public static Fragment newInstance(int page) {
            Fragment fragment = new BlankFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("page", page);
            fragment.setArguments(bundle);
            return fragment;
        }

        private void setPageText(int page){
            title.setText(getResources().getStringArray(R.array.onboarding_titles)[page]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext()
                    , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                subtitle.setText(getResources().getStringArray(R.array.onboarding_subtitles_M)[page]);
            } else {
                subtitle.setText(getResources().getStringArray(R.array.onboarding_subtitles)[page]);
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
                    return BlankFragment.newInstance(0);
                case 1:
                    return BlankFragment.newInstance(1);
                case 2:
                    return BlankFragment.newInstance(2);
                case 3:
                    return BlankFragment.newInstance(3);
                case 4:
                    return BlankFragment.newInstance(4);
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

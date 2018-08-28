package com.malcolm.portsmouthunibus.ui.onboarding;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.malcolm.portsmouthunibus.R;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IntroAnimationListener} interface
 * to handle interaction events.
 */
public class IntroFragment extends androidx.fragment.app.Fragment implements Animator.AnimatorListener {

    private IntroAnimationListener animationListener;
    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottie;
    @BindView(R.id.intro_text)
    TextView introText;
    @BindView(R.id.frame)
    FrameLayout frame;

    public IntroFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_intro, container, false);
        ButterKnife.bind(this, rootView);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            frame.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));
            lottie.setVisibility(View.GONE);
            introText.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(introText, "alpha", 0, 1);
            animator.setDuration(600);
            animator.start();
            ObjectAnimator off = ObjectAnimator.ofFloat(introText, "alpha", 1, 0);
            off.setStartDelay(2500);
            off.setDuration(600);
            off.addListener(this);
            off.start();
        } else {
            lottie.addAnimatorListener(this);
            lottie.setAnimation("intro.json");
            lottie.playAnimation();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        animationListener.onIntroAnimationFinished();
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IntroAnimationListener) {
            animationListener = (IntroAnimationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IntroAnimationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        animationListener = null;
    }


    public interface IntroAnimationListener {
        void onIntroAnimationFinished();
    }
}

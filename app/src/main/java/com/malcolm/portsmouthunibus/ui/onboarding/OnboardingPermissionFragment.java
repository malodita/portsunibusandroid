package com.malcolm.portsmouthunibus.ui.onboarding;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.malcolm.portsmouthunibus.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;


public class OnboardingPermissionFragment extends BaseOnboardingFragment  {

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottie;
    @BindView(R.id.permission_request)
    Button permissionRequest;
    @BindView(R.id.permission_not_needed)
    Button permissionNo;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_onboarding_permission, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        lottie.setAnimation("moving_bus.json");
        lottie.setRepeatCount(LottieDrawable.INFINITE);
        permissionNo.setOnClickListener(l -> {
            Toast.makeText(getContext(), "Permissions can be changed in settings", Toast.LENGTH_SHORT).show();
            listener.onPermissionRejected();}
            );
        permissionRequest.setOnClickListener(c -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
               requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                listener.onPageFinished();
            }
        });
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listener.onPageFinished();
            } else {
                Toast.makeText(getContext(), "Permissions can be changed in settings", Toast.LENGTH_SHORT).show();
                listener.onPermissionRejected();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        lottie.playAnimation();
    }

    @Override
    public void onPause() {
        lottie.cancelAnimation();
        super.onPause();
    }
}

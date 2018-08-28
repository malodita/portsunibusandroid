package com.malcolm.portsmouthunibus.ui.onboarding;

import android.content.Context;
import android.content.SharedPreferences;

import com.malcolm.portsmouthunibus.R;

import androidx.fragment.app.Fragment;
import butterknife.Unbinder;

public class BaseOnboardingFragment extends Fragment {

    protected OnboardingProgressListener listener;
    protected SharedPreferences sharedPreferences;
    protected Unbinder unbinder;
    final int LOCATION_PERMISSION_REQUEST_CODE = 5384;



    interface OnboardingProgressListener {

        void onPageFinished();
        void onPermissionRejected();
        void onOnboardingFinished();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnboardingProgressListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnboardingProgressListener");
        }
        sharedPreferences = context.getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);

    }


    @Override
    public void onDestroyView() {
        if (unbinder != null){
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}

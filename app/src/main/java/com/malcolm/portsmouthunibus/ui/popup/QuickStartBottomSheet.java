package com.malcolm.portsmouthunibus.ui.popup;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.malcolm.portsmouthunibus.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class QuickStartBottomSheet extends BottomSheetDialogFragment {

    /**
     * Listener to obtain dialog result
     */
    public interface DialogButtonSelectedListener{
        /**
         * Called when a button is pressed on the dialog
         * @param positive If the dialog was closed with a positive result
         */
        void onButtonSelected(boolean positive);
    }

    protected DialogButtonSelectedListener listener;
    private Unbinder unbinder;
    @BindView(R.id.textview)
    TextView textView;
    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottie;
    @BindView(R.id.button_confirm)
    Button buttonConfirm;
    @BindView(R.id.button_reject)
    Button buttonReject;

    @Override
    public void onAttach(Context context) {
        try{
            listener = (DialogButtonSelectedListener) getActivity();
        } catch (ClassCastException c){
            throw new ClassCastException(getActivity().toString()
                    + " must implement DialogButtonSelectedListener");
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.dialog_quickstart_bottom_sheet, container, false);
        unbinder = ButterKnife.bind(this, root);
        lottie.setAnimation("search-ask_loop.json");
        lottie.setRepeatCount(LottieDrawable.INFINITE);
        lottie.playAnimation();
        buttonConfirm.setOnClickListener(view -> {
            listener.onButtonSelected(true);
            dismiss();
        });
        buttonReject.setOnClickListener(view -> {
            listener.onButtonSelected(false);
            dismiss();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}

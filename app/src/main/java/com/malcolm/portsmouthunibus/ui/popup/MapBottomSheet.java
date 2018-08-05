package com.malcolm.portsmouthunibus.ui.popup;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.malcolm.portsmouthunibus.R;
import com.malcolm.portsmouthunibus.utilities.ImageGenerator;
import com.squareup.picasso.RequestCreator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Intent.ACTION_VIEW;

public class MapBottomSheet extends BottomSheetDialogFragment {

    public static final String STOP_NAME = "stop_name";
    public static final String STOP_LOCATION = "stop_location";
    public static final String STOP_DETAIL = "stop_detail";

    private Unbinder unbinder;
    @BindView(R.id.map_sheet_title)
    TextView title;
    @BindView(R.id.map_sheet_detail)
    TextView detail;
    @BindView(R.id.map_sheet_image)
    ImageView stopImage;
    @BindView(R.id.map_sheet_navigate)
    Button navigate;
    private LatLng location;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.dialog_map_bottom_sheet, container, false);
        unbinder = ButterKnife.bind(this, root);
        Bundle bundle = getArguments();
        location = bundle != null ? bundle.getParcelable(STOP_LOCATION) : null;
        String stop = bundle != null ? bundle.getString(STOP_NAME) : null;
        title.setText(stop);
        detail.setText(bundle != null ? bundle.getString(STOP_DETAIL) : null);
        RequestCreator requestCreator = ImageGenerator.generateStopImage(getContext(), stop);
        requestCreator.into(stopImage);
        navigate.setOnClickListener(l -> {
            if (isPackageInstalled("com.google.android.apps.maps", getContext().getPackageManager())) {
                Uri nav = Uri.parse("geo:0,0?q="
                        + location.latitude + "," + location.longitude +
                        "(" + title.getText() + ")&mode=w");
                Intent i = new Intent(ACTION_VIEW, nav);
                if (i.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(i);
                }
            } else {
                Uri nav = Uri.parse("geo:" + location.latitude + "," + location.longitude);
                Intent i = new Intent(ACTION_VIEW, nav);
                if (i.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(i);
                }
            }
        });
        return root;
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}

package com.malcolm.portsmouthunibus.models;

import android.view.View;

import com.airbnb.epoxy.EpoxyHolder;

import androidx.annotation.CallSuper;
import butterknife.ButterKnife;

/**
 * Created by Malcolm on 29/01/2017.
 */

public abstract class BaseHolder extends EpoxyHolder {
    @CallSuper
    @Override
    protected void bindView(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}

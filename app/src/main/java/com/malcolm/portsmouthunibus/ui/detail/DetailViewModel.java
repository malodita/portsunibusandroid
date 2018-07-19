
package com.malcolm.portsmouthunibus.ui.detail;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.malcolm.portsmouthunibus.App;
import com.malcolm.unibusutilities.entity.Times;
import com.malcolm.unibusutilities.repository.MainRepository;

import java.util.List;

public class DetailViewModel extends AndroidViewModel {

    private List<Times> list;
    private MainRepository repository;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        repository = ((App) application).getMainRepository();
    }

    /**
     * Fetches the list of times for an individual bus.
     *
     * @param stopId Id of stop
     * @param is24Hours If should be reported in 12 or 24 hour format
     * @return A formatted list of times suitable for a recyclerview.
     */
    public List<Times> fetchList(int stopId, boolean is24Hours) {
        list = repository.getDataForList(stopId, is24Hours);
        return list;
    }

    public List<Times> getList() {
        return list;
    }

    public void setList(List<Times> list) {
        this.list = list;
    }
}


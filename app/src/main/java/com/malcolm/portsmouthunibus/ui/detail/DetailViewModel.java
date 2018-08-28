
package com.malcolm.portsmouthunibus.ui.detail;


import android.app.Application;

import com.malcolm.portsmouthunibus.App;
import com.malcolm.unibusutilities.entity.Times;
import com.malcolm.unibusutilities.repository.MainRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DetailViewModel extends AndroidViewModel {

    private List<Times> list;
    private MainRepository repository;

    private DetailViewModel(Application application) {
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

    public static class Factory extends ViewModelProvider.NewInstanceFactory{

        @NonNull private final Application application;

        Factory(@NonNull Application application) {
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new DetailViewModel(application);
        }

    }
}


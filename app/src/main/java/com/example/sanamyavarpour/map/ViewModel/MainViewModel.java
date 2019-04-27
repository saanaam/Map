package com.example.sanamyavarpour.map.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.sanamyavarpour.map.Service.Model.Asiatag;
import com.example.sanamyavarpour.map.Service.Repository.MapRepository;

import io.reactivex.annotations.NonNull;

public class MainViewModel extends AndroidViewModel {
    MapRepository mapRepository;

    public MainViewModel(@NonNull Application application) throws Exception {
        super(application);
        mapRepository = new MapRepository(application );

    }

    public LiveData<Asiatag> getMapDirections(String json, double lat,
                                              double lon) throws Exception {
        return mapRepository.getMapDirections(json, lat , lon  );
    }
}

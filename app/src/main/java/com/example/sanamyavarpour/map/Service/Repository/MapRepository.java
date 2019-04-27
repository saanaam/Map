package com.example.sanamyavarpour.map.Service.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.sanamyavarpour.map.Service.Model.Asiatag;

import io.reactivex.annotations.NonNull;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MapRepository {
    private ApiInterface client;

    public MapRepository(@NonNull Application application) {
        client = ServiceGenerator.create(ApiInterface.class);

    }


    public LiveData<Asiatag> getMapDirections(String json, double lat,
                                              double lon) {
        final MutableLiveData<Asiatag> data = new MutableLiveData<>();
        client.getMapDirections(json , lat , lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Asiatag>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Asiatag result) {
                        Asiatag directionResults = new Asiatag();
                        directionResults.setAddress( result.getAddress() );
                        data.setValue( directionResults );


                    }
                });
        return data;
    }


}

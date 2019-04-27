package com.example.sanamyavarpour.map.App;

import android.arch.lifecycle.MutableLiveData;

public class Application  extends android.app.Application{
    public MutableLiveData<String> address  ;
    public MutableLiveData<String>  destination ;

    @Override
    public void onCreate() {
        super.onCreate();
        address = new MutableLiveData<>();
        address.setValue( "" );
        destination= new MutableLiveData<>();
        destination.setValue( "" );

    }
}

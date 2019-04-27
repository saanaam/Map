package com.example.sanamyavarpour.map.View.Fragment;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sanamyavarpour.map.App.Application;
import com.example.sanamyavarpour.map.R;
import com.example.sanamyavarpour.map.Service.Model.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OriginFragment extends BaseFragment {
@BindView( R.id.origin )
    TextView origin;
    @BindView( R.id.btnok )
    Button btnok;
    MutableLiveData<Integer> gotaddress ;
    FragNavController fragNavController;
    Application application;

    @NonNull
    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_origin;
    }

    @Override
    protected void init() {
        gotaddress = new MutableLiveData<>();
        gotaddress.setValue( 0 );


    }

    @OnClick(R.id.btnok)
    public void setBtnok(){
        EventBus.getDefault().postSticky(new MessageEvent(1));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(String addressOrigin) {
        origin.setText( addressOrigin );
        if (origin.getText().toString().length()>0)
        EventBus.getDefault().postSticky( addressOrigin );
        getMainActivity().pushFragment(new OriginFragment());
    }



}

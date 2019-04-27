package com.example.sanamyavarpour.map.View.Fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanamyavarpour.map.MainActivity;
import com.example.sanamyavarpour.map.R;
import com.example.sanamyavarpour.map.Service.Model.Asiatag;
import com.example.sanamyavarpour.map.Service.Model.Destination;
import com.example.sanamyavarpour.map.Service.Model.MessageEvent;
import com.example.sanamyavarpour.map.ViewModel.MainViewModel;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * destination fragment
 */
public class DestinationFragment extends BaseFragment {
    @BindView( R.id.destination )
    TextView destination;
    @BindView( R.id.origin )
    TextView origin;
    @BindView( R.id.btnok )
    Button btnok;
    MainViewModel mainViewModel;
    double lat, lot ;

    @NonNull
    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_destination;
    }

    @Override
    protected void init() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(String addressOrigin) {
        origin.setText( addressOrigin );
    }

    @OnClick(R.id.btnok)
    public void setBtnok(){
        if (origin.getText().toString().length()>0){
            if (destination.getText().toString().length()>0){
                btnok.setText( R.string.get );
                callApi( "jsonv2" , lat , lot );
                return;
            }

        }else
        EventBus.getDefault().postSticky(new MessageEvent(2));

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Destination destinationads) {
        destination.setText( destinationads.getDestinationAdress() );

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(LatLng latLng) {
         lat = latLng.latitude;
         lot = latLng.longitude;
    }

    /**
     * call asiatech api
     */
    public void callApi(String json, double lat, double lon) {
        try {
            mainViewModel.getMapDirections( json, lat, lon ).observe( this, new Observer<Asiatag>() {
                @Override
                public void onChanged(@Nullable Asiatag directionResults) {

                    if (directionResults.getAddress().getCity()!= null){
                        EventBus.getDefault().postSticky(new MessageEvent(3));
                        Toast.makeText( getContext(), directionResults.getAddress().getCity().toString(), Toast.LENGTH_SHORT ).show();

                    }

                }

            } );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

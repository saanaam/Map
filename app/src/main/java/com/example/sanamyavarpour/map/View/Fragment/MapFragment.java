package com.example.sanamyavarpour.map.View.Fragment;


import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sanamyavarpour.map.App.Application;
import com.example.sanamyavarpour.map.MainActivity;
import com.example.sanamyavarpour.map.R;
import com.example.sanamyavarpour.map.Service.Model.Destination;
import com.example.sanamyavarpour.map.Service.Model.MessageEvent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * map fragment
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback {
    String strAdd = "";
    LatLng latLng;
    Application app;
    public static SupportMapFragment MapFragment;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_COD = 123;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    ArrayList<LatLng> listPoints;
    private LatLng mapCenterLatLng, destination, LatLng, center;
    private Boolean locationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedlocationprovider;
    MutableLiveData<Integer> pressedorigin;
    Integer btnoriginpressed = 0;
    String addressorigin,  destinationaddress = "";
    int keypass ;


    @NonNull
    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_map;
    }

    @Override
    protected void init() {
        getlocationPermission();
        pressedorigin = new MutableLiveData<>();
        pressedorigin.setValue( 0 );

        pressedorigin.observe( this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                btnoriginpressed = integer;
                if (btnoriginpressed == 1)
//                    addpoint( mMap );
                    keypass =1 ;

                if (btnoriginpressed == 2)
//                    adddestination( mMap );
                    keypass=2;

                if (btnoriginpressed == 3){
                   keypass = 3;
                }

            }
        } );
        latLng = new LatLng( 0, 0 );
        listPoints = new ArrayList<>();
    }

    /**
     * get location permission
     */
    private void getlocationPermission() {
        Log.d( TAG, "getlocationPermission: getting permission" );
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        if (ContextCompat.checkSelfPermission( getContext(),
                FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission( getContext(), COARSE_LOCATION ) ==
                    PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                initmap();


            } else {
                ActivityCompat.requestPermissions( getMainActivity(),
                        permissions, LOCATION_PERMISSION_REQUEST_COD );
            }
        } else {
            ActivityCompat.requestPermissions( getMainActivity(),
                    permissions, LOCATION_PERMISSION_REQUEST_COD );
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d( TAG, "onRequestPermissionsResult: called" );
        locationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_COD: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            Log.d( TAG, "onRequestPermissionsResult: permission Failed" );
                            return;
                        }
                    }
                    Log.d( TAG, "onRequestPermissionsResult: permission Granted" );
                    locationPermissionGranted = true;
                    //initialize our map
                    initmap();
                }
            }
        }

    }

    private void initmap() {
        Log.d( TAG, "initmap: initializing map" );
        MapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.map );
        MapFragment.getMapAsync( (OnMapReadyCallback) this );

    }

    /**
     * get current location of device
     */
    private void getDeviceLocation() {
        Log.d( TAG, "getDeviceLocation: getting the device current location" );
        mfusedlocationprovider = LocationServices.getFusedLocationProviderClient( getMainActivity() );

        try {
            if (locationPermissionGranted) {

                Task location = mfusedlocationprovider.getLastLocation();
                location.addOnCompleteListener( new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Log.d( TAG, "onComplete: found location" );
                            Toast.makeText( getContext(), "found location!", Toast.LENGTH_SHORT ).show();
                            Location currentlocation = (Location) task.getResult();
                            moveCamera( new LatLng( currentlocation.getLatitude(), currentlocation.getLongitude() ), DEFAULT_ZOOM );

                        } else {
                            Log.d( TAG, "onComplete: current location is null" );
                            Toast.makeText( getContext(), "unable to get current location", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );
            }

        } catch (SecurityException e) {
            Toast.makeText( getContext(), e.getMessage(), Toast.LENGTH_SHORT ).show();
        }

    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d( TAG, "moveCamera: move to location :" + latLng.latitude + latLng.longitude );

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, zoom ) );
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        Geocoder geocoder = new Geocoder( getContext(), Locale.getDefault() );
        try {
            List<Address> addresses = geocoder.getFromLocation( LATITUDE, LONGITUDE, 1 );
            if (addresses != null) {
                Address returnedAddress = addresses.get( 0 );
                StringBuilder strReturnedAddress = new StringBuilder( "" );

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append( returnedAddress.getAddressLine( i ) ).append( "\n" );
                }
                strAdd = strReturnedAddress.toString();
                Toast.makeText( getContext(), strReturnedAddress.toString(), Toast.LENGTH_SHORT ).show();
            } else {
                Toast.makeText( getContext(), "No Address returned", Toast.LENGTH_SHORT ).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText( getContext(), "Canont get Address!", Toast.LENGTH_SHORT ).show();

        }
        return strAdd;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d( TAG, "onMapReady: map is ready" );
        if (locationPermissionGranted) {
            mMap.getUiSettings().setZoomControlsEnabled( true );
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(
                    getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    ( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION )
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mapCenterLatLng = mMap.getCameraPosition().target;
            mMap.setMyLocationEnabled( true );

            mMap.setOnCameraIdleListener( new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    if (listPoints.size() == 2) {
                        listPoints.clear();
                        mMap.clear();
                    }
                    mapCenterLatLng = mMap.getCameraPosition().target;
                    String addressidelcamer = getCompleteAddressString
                            ( mapCenterLatLng.longitude, mapCenterLatLng.longitude );
                    Toast.makeText( getContext(), addressidelcamer, Toast.LENGTH_SHORT ).show();

                    if (keypass==1){
                        center = mMap.getCameraPosition().target;
                        mMap.addMarker( new MarkerOptions().position( center ).title( "your Position" ) );
                        EventBus.getDefault().postSticky( new LatLng( center.longitude, center.longitude ) );
                        LatLng originlatln = new LatLng( center.longitude, center.longitude );
                        addressorigin = getCompleteAddressString( center.longitude, center.longitude );
                        Toast.makeText( getContext(), originlatln.toString(), Toast.LENGTH_SHORT ).show();
                        EventBus.getDefault().postSticky( addressorigin );
                        listPoints.add( originlatln );

                    }else if(keypass==2){
                        destination = mMap.getCameraPosition().target;
                        mMap.addMarker( new MarkerOptions().position( destination ).title( " Position" ) );
                        LatLng destinationlatln = new LatLng( destination.longitude, destination.longitude );
                        destinationaddress = getCompleteAddressString( destination.longitude, destination.longitude );
                        Toast.makeText( getContext(), destinationlatln.toString(), Toast.LENGTH_SHORT ).show();
                        EventBus.getDefault().postSticky( new Destination( destinationaddress ) );
                        listPoints.add( destinationlatln );
                    }else {
                        mMap.clear();
                        listPoints.clear();
                    }

                }
            } );


        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent pressed) {
        pressedorigin.setValue( pressed.getOroginpressed() );

    }


}


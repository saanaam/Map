package com.example.sanamyavarpour.map;

import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanamyavarpour.map.Service.Model.Asiatag;
import com.example.sanamyavarpour.map.View.Fragment.DestinationFragment;
import com.example.sanamyavarpour.map.View.Fragment.FragNavController;
import com.example.sanamyavarpour.map.View.Fragment.MapFragment;
import com.example.sanamyavarpour.map.View.Fragment.OriginFragment;
import com.example.sanamyavarpour.map.ViewModel.MainViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    //    @BindView(R.id.containerMap)
//    FrameLayout containerMap;
    @BindView(R.id.container)
    FrameLayout container;

    //    String strAdd = "";
//    LatLng latLng;
    private MutableLiveData<LatLng> originPlace;
    private MutableLiveData<LatLng> destinationdata;
    //    public static SupportMapFragment MapFragment;
    public static FragNavController fragNavController;

    protected MainViewModel mainViewModel;
    private static final String TAG = "MainActivity";
//    private LatLng mapCenterLatLng;
//    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
//    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
//    private static final int LOCATION_PERMISSION_REQUEST_COD = 123;
//    private static final float DEFAULT_ZOOM = 15f;
//    private static final int PLACE_PICKER_REQUEST = 1;
//    ArrayList<LatLng> listPoints;
//    LatLng destination;
//    LatLng center;
//    private Boolean locationPermissionGranted = false;
//    private GoogleMap mMap;
//    private FusedLocationProviderClient mfusedlocationprovider;

    List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        ButterKnife.bind( this );


        /*
         *get location permission and init map
         */
//        getlocationPermission();

//        latLng = new LatLng( 0, 0 );
//        listPoints = new ArrayList<>();
        mainViewModel = ViewModelProviders.of( this ).get( MainViewModel.class );


//        originPlace = new MutableLiveData<LatLng>();
//        destinationdata = new MutableLiveData<LatLng>();
//        originPlace.setValue( latLng );
//        destinationdata.setValue( latLng );

//        originPlace.observe(this, new Observer<LatLng>() {
//            @Override
//            public void onChanged(@Nullable LatLng s) {
//                if (s.toString().contains(  latLng.toString() )){
////                    address.setText( " " );
////                    address.setText( "مکان مبدا را وارد کنید" );
//
//                }else {
//                    getCompleteAddressString( s.latitude, s.longitude );
////                    address.setText( strAdd.toString() );
//                    btnok.isClickable();
//                    btnok.setText( "مکان مقصد را وارد کنید و دکنه تایید را فشار دهید" );
//                }
//
//            }
//        });

//        destinationdata.observe(this, new Observer<LatLng>() {
//            @Override
//            public void onChanged(@Nullable LatLng s) {
//                if (s.toString().contains(  latLng.toString() )){
////                    destination.setText( "" );
//
//                }else {
//                    getCompleteAddressString( s.latitude, s.longitude);
////                    destination.setText( strAdd.toString() );
////                    destination.setVisibility( View.VISIBLE );
//
//                    btnok.isClickable();
//                    btnok.setText( "دریافت" );
//                    getCompleteAddressString( s.latitude, s.longitude );
////                    destination.setText( strAdd.toString() );
//
//                }
//
//            }
//        });

        fragments = new ArrayList<>();
        fragments.add( new OriginFragment() );
        fragments.add( new DestinationFragment() );

        fragNavController = new FragNavController( savedInstanceState,
                getSupportFragmentManager(), R.id.container, fragments, 0 );

        fragNavController.addFragment( R.id.containerMap, new MapFragment() );


    }

//    private void getlocationPermission() {
//        Log.d( TAG, "getlocationPermission: getting permission" );
//        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION};
//
//        if (ContextCompat.checkSelfPermission( this.getApplicationContext(),
//                FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
//            if (ContextCompat.checkSelfPermission( this.getApplicationContext(), COARSE_LOCATION ) ==
//                    PackageManager.PERMISSION_GRANTED) {
//                locationPermissionGranted = true;
//                initmap();
//
//
//            } else {
//                ActivityCompat.requestPermissions( this,
//                        permissions, LOCATION_PERMISSION_REQUEST_COD );
//            }
//        } else {
//            ActivityCompat.requestPermissions( this,
//                    permissions, LOCATION_PERMISSION_REQUEST_COD );
//        }
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d( TAG, "onRequestPermissionsResult: called" );
//        locationPermissionGranted = false;
//        switch (requestCode) {
//            case LOCATION_PERMISSION_REQUEST_COD: {
//                if (grantResults.length > 0) {
//                    for (int i = 0; i < grantResults.length; i++) {
//                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                            locationPermissionGranted = false;
//                            Log.d( TAG, "onRequestPermissionsResult: permission Failed" );
//                            return;
//                        }
//                    }
//                    Log.d( TAG, "onRequestPermissionsResult: permission Granted" );
//                    locationPermissionGranted = true;
//                    //initialize our map
//                    initmap();
//                }
//            }
//        }
//
//    }

//    private void initmap() {
//        Log.d( TAG, "initmap: initializing map" );
//        MapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
//        MapFragment.getMapAsync( this );
//    }

    /*
     *get device location onMapReady();
     */
//    private void getDeviceLocation() {
//        Log.d( TAG, "getDeviceLocation: getting the device current location" );
//        mfusedlocationprovider = LocationServices.getFusedLocationProviderClient( this );
//
//        try {
//            if (locationPermissionGranted) {
//
//                Task location = mfusedlocationprovider.getLastLocation();
//                location.addOnCompleteListener( new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//
//                        if (task.isSuccessful()) {
//                            Log.d( TAG, "onComplete: found location" );
//                            Toast.makeText( MainActivity.this, "found location!", Toast.LENGTH_SHORT ).show();
//                            Location currentlocation = (Location) task.getResult();
//                            moveCamera( new LatLng( currentlocation.getLatitude(), currentlocation.getLongitude() ), DEFAULT_ZOOM );
//
//                        } else {
//                            Log.d( TAG, "onComplete: current location is null" );
//                            Toast.makeText( MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT ).show();
//                        }
//                    }
//                } );
//            }
//
//        } catch (SecurityException e) {
//            Toast.makeText( this, e.getMessage(), Toast.LENGTH_SHORT ).show();
//        }
//
//    }

//    private void moveCamera(LatLng latLng, float zoom) {
//        Log.d( TAG, "moveCamera: move to location :" + latLng.latitude + latLng.longitude );
//
//        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, zoom ) );
//    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        Log.d( TAG, "onMapReady: map is ready" );
//        if (locationPermissionGranted) {
//            mMap.getUiSettings().setZoomControlsEnabled( true );
//            getDeviceLocation();
//            if (ActivityCompat.checkSelfPermission(
//                    MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION )
//                    != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            mapCenterLatLng = mMap.getCameraPosition().target;
//            mMap.setMyLocationEnabled( true );
//
////            centerMarker = mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target)
////                    .title("Center of Map")
////                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_audiotrack_dark)));
//
//            mMap.setOnCameraIdleListener( new GoogleMap.OnCameraIdleListener() {
//                @Override
//                public void onCameraIdle() {
////                    if (listPoints.size()==2){
////                        listPoints.clear();
////                        mMap.clear();
////                    }
////
////                    mapCenterLatLng = mMap.getCameraPosition().target;
//////                    animateMarker(centerMarker,mapCenterLatLng,false);
////
////                    Toast.makeText( MainActivity.this, "The camera has stopped moving.",
////                            Toast.LENGTH_SHORT ).show();
////
////                    String addressidelcamer = getCompleteAddressString
////                            (mapCenterLatLng.longitude, mapCenterLatLng.longitude);
////
////                    Toast.makeText( MainActivity.this, addressidelcamer, Toast.LENGTH_SHORT ).show();
////
////                    if (listPoints.size()==0)
////                        btnok.setText( "تایید مبدا" );
////
////                    btnok.setOnClickListener( new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////
////                            center = mMap.getCameraPosition().target;
////                            mMap.addMarker( new MarkerOptions().position( center ).title( "your Position" ) );
////                            LatLng originlatln = new LatLng( center.longitude, center.longitude );
////
////                            String addressidelcamer = getCompleteAddressString
////                                    (center.longitude, center.longitude);
////
////                            Toast.makeText( MainActivity.this, originlatln.toString(), Toast.LENGTH_SHORT ).show();
////
////                            listPoints.add( originlatln );
////                            originPlace.setValue( originlatln );
////
////                            if (listPoints.size() == 1) {
////
////                                btnok.setText( "select destination" );
////
////                                btnok.setOnClickListener( new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////                                        destination = mMap.getCameraPosition().target;
////                                        mMap.addMarker( new MarkerOptions().position( destination ).title( " Position" ) );
////
////
////                                        String destinationaddress = getCompleteAddressString
////                                                (destination.longitude, destination.longitude);
////
////                                        LatLng destinationlatln = new LatLng( destination.longitude, destination.longitude );
////
////                                        Toast.makeText( MainActivity.this, destinationlatln.toString(), Toast.LENGTH_SHORT ).show();
////
////                                        listPoints.add( destinationlatln );
////
////                                        originPlace.setValue( destinationlatln );
////                                    }
////                                } );
////                            }
////
////                            if(listPoints.size()==2){
////                                btnok.setText( "got origin/destination call api" );
////                                btnok.setOnClickListener( new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////                                        Toast.makeText( MainActivity.this, "call api", Toast.LENGTH_SHORT ).show();
////                                        mMap.clear();
////                                        listPoints.clear();
////                                    }
////                                } );
////                            }
////
////                        }
////                    } );
//
//                }
//            } );

//            mMap.setOnCameraMoveStartedListener( new GoogleMap.OnCameraMoveStartedListener() {
//                @Override
//                public void onCameraMoveStarted(int reason) {
////
//                    if (listPoints.size() == 2) {
//                        listPoints.clear();
//                        mMap.clear();
//                    }
//
//                    if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
//
////                        address.setText("Lat " + mapCenterLatLng.latitude + "  Long :" + mapCenterLatLng.longitude);
//                        LatLng firtspont = new LatLng( mapCenterLatLng.latitude, mapCenterLatLng.longitude );
//                        //save first poin click
//                        listPoints.add( firtspont );
//                        if (listPoints.size() == 1) {
//
//                            //add first marker to the map
////                            originPlace.setValue( listPoints.get( 0 ) );
//
//
//                        } else {
//                            //add second marker to the map
//                            Toast.makeText( MainActivity.this, listPoints.get( 1 ) + "", Toast.LENGTH_SHORT ).show();
//                            destinationdata.setValue( listPoints.get( 1 ) );
//
//                        }
//
//                        if (listPoints.size() == 2) {
//
//                            mMap.clear();
//                            btnok.setOnClickListener( new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    CallApi( "jsonv2", listPoints.get( 0 ).latitude, listPoints.get( 0 ).longitude );
//                                }
//                            } );
//
//                        }
//
//
//                        Toast.makeText( MainActivity.this, "The user gestured on the map.",
//                                Toast.LENGTH_SHORT ).show();
//
//                    } else if (reason == GoogleMap.OnCameraMoveStartedListener
//                            .REASON_API_ANIMATION) {
//
//                        Toast.makeText( MainActivity.this, "The user tapped something on the map.",
//                                Toast.LENGTH_SHORT ).show();
//                    } else if (reason == GoogleMap.OnCameraMoveStartedListener
//                            .REASON_DEVELOPER_ANIMATION) {
//                        Toast.makeText( MainActivity.this, "The app moved the camera.",
//                                Toast.LENGTH_SHORT ).show();
//                    }
//                }
//            } );


//            mMap.setOnMapLongClickListener( new GoogleMap.OnMapLongClickListener() {
//                @Override
//                public void onMapLongClick(LatLng latLng) {
//                    //reset market when already 2
//
//                    if (listPoints.size() == 2) {
//                        listPoints.clear();
//                        mMap.clear();
//                    }
//                    //save first poin click
//                    listPoints.add( latLng );
//                    //create marker
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position( latLng );
//                    if (listPoints.size() == 1) {
//
//                        //add first marker to the map
//                        markerOptions.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE ) );
//                        Toast.makeText( MainActivity.this, listPoints.get( 0 ) + "", Toast.LENGTH_SHORT ).show();
//                        originPlace.setValue( listPoints.get( 0 ) );
//
//                    } else {
//                        //add second marker to the map
//                        markerOptions.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_GREEN ) );
//                        Toast.makeText( MainActivity.this, listPoints.get( 1 ) + "", Toast.LENGTH_SHORT ).show();
//                        destinationdata.setValue( listPoints.get( 1 ) );
//                    }
//                    mMap.addMarker( markerOptions );
//
//                    // TODO : request get direction code
//
//                    if (listPoints.size() == 2) {
//                        //Create the URL to get request from first marker to second marker
////                        String url = getRequestURL( listPoints.get( 0 ), listPoints.get( 1 ) );
////                        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
////                        taskRequestDirections.execute( url );
//
//                        btnok.setOnClickListener( new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                CallApi("jsonv2",listPoints.get( 0 ).latitude , listPoints.get( 0 ).longitude );
//                            }
//                        } );
//
//                    }
//
//                }
//            } );


//    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
//        Geocoder geocoder = new Geocoder( this, Locale.getDefault() );
//        try {
//            List<Address> addresses = geocoder.getFromLocation( LATITUDE, LONGITUDE, 1 );
//            if (addresses != null) {
//                Address returnedAddress = addresses.get( 0 );
//                StringBuilder strReturnedAddress = new StringBuilder( "" );
//
//                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append( returnedAddress.getAddressLine( i ) ).append( "\n" );
//                }
//                strAdd = strReturnedAddress.toString();
//                Toast.makeText( this, strReturnedAddress.toString(), Toast.LENGTH_SHORT ).show();
//            } else {
//                Toast.makeText( this, "No Address returned", Toast.LENGTH_SHORT ).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText( this, "Canont get Address!", Toast.LENGTH_SHORT ).show();
//
//        }
//        return strAdd;
//    }




    public void pushFragment(Fragment fragment) {
        fragNavController.pushFragment( fragment );
    }

}









package com.example.sanamyavarpour.map.Service.Repository;

import com.example.sanamyavarpour.map.Service.Model.Asiatag;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiInterface {



    @GET("reverse")
//    Observable<DirectionResults> getJson(@Query("origin") String origin, @Query("destination") String destination);

    Observable<Asiatag> getMapDirections(@Query("format") String jsonv2,
                                         @Query("lat") double lat,
                                         @Query("lon") double lon);


}


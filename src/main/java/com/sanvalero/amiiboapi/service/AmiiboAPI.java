package com.sanvalero.amiiboapi.service;

import com.sanvalero.amiiboapi.model.AmiiboFilterResponse;
import com.sanvalero.amiiboapi.model.AmiiboResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AmiiboAPI {

    @GET("{filterName}")
    Observable<AmiiboFilterResponse> getInformation(@Path("filterName") String filterName);

    @GET("amiibo?{searchText}")
    Observable<AmiiboResponse> getAmiibo(@Path("searchText") String searchText);
    
}

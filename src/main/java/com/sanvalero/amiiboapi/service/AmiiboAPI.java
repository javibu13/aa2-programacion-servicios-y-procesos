package com.sanvalero.amiiboapi.service;

import com.sanvalero.amiiboapi.model.AmiiboFilterResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AmiiboAPI {

    @GET("{filterName}")
    Observable<AmiiboFilterResponse> getInformation(@Path("filterName") String filterName);
    
}

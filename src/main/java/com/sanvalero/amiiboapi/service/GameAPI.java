package com.sanvalero.amiiboapi.service;

import com.sanvalero.amiiboapi.model.GameResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GameAPI {

    @GET("games")
    Observable<GameResponse> getGame(@Query("key") String secretAPIKey, @Query("parent_platforms") String parentPlatforms, @Query("search") String searchGame, @Query("search_precise") boolean searchPrecise);
    
}

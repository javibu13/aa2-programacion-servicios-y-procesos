package com.sanvalero.amiiboapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sanvalero.amiiboapi.model.AmiiboFilterEntry;
import com.sanvalero.amiiboapi.model.AmiiboJsonEntry;
import com.sanvalero.amiiboapi.util.FilterGroup;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AmiiboRetrieveService {
    private static final Logger logger = LoggerFactory.getLogger(AmiiboRetrieveService.class);

    private static final String BASE_URL = "https://amiiboapi.com/api/";
    private FilterGroup filterGroup;
    private AmiiboAPI amiiboAPI;

    public AmiiboRetrieveService(FilterGroup filterGroup) {
        logger.info("Creating AmiiboRetrieveService with search arguments: {}", filterGroup);
        this.filterGroup = filterGroup;
        // Create HttpClient with logging interceptor
        HttpLoggingInterceptor httpInterceptor = new HttpLoggingInterceptor();
        httpInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(httpInterceptor)
                .build();
        // Create Gson instance
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        amiiboAPI = retrofit.create(AmiiboAPI.class);
        logger.info("AmiiboRetrieveService created successfully.");
    }

    public Observable<AmiiboJsonEntry> getAmiibo() {
        return amiiboAPI.getAmiibo(filterGroup.getType(), filterGroup.getSeries(), filterGroup.getCharacter()) // This returns an Observable<AmiiboResponse>
                        .map(amiiboResponse -> amiiboResponse.getAmiibo()) // Extract the List<AmiiboJsonEntry>
                        .flatMapIterable(amiiboJsonEntry -> amiiboJsonEntry); // Slice the List<AmiiboJsonEntry> into individual Observable<AmiiboJsonEntry> items
    }
}

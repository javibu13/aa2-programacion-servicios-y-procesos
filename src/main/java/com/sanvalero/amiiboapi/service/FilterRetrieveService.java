package com.sanvalero.amiiboapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sanvalero.amiiboapi.model.AmiiboFilterEntry;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilterRetrieveService {
    private static final Logger logger = LoggerFactory.getLogger(FilterRetrieveService.class);

    private static final String BASE_URL = "https://amiiboapi.com/api/";
    private String filterName;
    private AmiiboAPI amiiboAPI;

    public FilterRetrieveService(String filterName) {
        logger.info("Creating FilterRetrieveService for filter: {}", filterName);
        this.filterName = filterName;
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
    }

    public Observable<AmiiboFilterEntry> getInformation() {
        return amiiboAPI.getInformation(filterName) // This returns an Observable<AmiiboFilterResponse>
                        .map(amiiboFilterResponse -> amiiboFilterResponse.getAmiibo()) // Extract the List<AmiiboFilterEntry>
                        .flatMapIterable(amiiboFilterEntry -> amiiboFilterEntry); // Slice the List<AmiiboFilterEntry> into individual Observable<AmiiboFilterEntry> items
    }
}

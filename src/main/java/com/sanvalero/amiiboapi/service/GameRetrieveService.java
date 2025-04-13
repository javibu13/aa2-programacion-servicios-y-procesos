package com.sanvalero.amiiboapi.service;

import com.sanvalero.amiiboapi.model.GameJsonEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameRetrieveService {
    private static final Logger logger = LoggerFactory.getLogger(GameRetrieveService.class);

    private static final String BASE_URL = "https://api.rawg.io/api/";
    private static String secretAPIKey;
    private String gameSeries;
    private GameAPI gameAPI;

    public GameRetrieveService(String gameSeries) {
        logger.info("Creating GameRetrieveService with search arguments: {}", gameSeries);
        this.gameSeries = gameSeries;
        // Get API key from text file in main resources folder
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/secretAPIKey.key"))) {
            secretAPIKey = reader.readLine();
            logger.info("API key loaded successfully.");
        } catch (IOException e) {
            logger.error("Error loading API key: {}", e.getMessage());
        }
        // Create HttpClient with logging interceptor
        HttpLoggingInterceptor httpInterceptor = new HttpLoggingInterceptor();
        httpInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create an interceptor to add headers to the request
        Interceptor headerInterceptor = chain -> {
                Request originalRequest = chain.request();
                Request modifiedRequest = originalRequest.newBuilder()
                        .addHeader("User-Agent", "Mozilla/5.0")
                        .addHeader("Accept", "application/json")
                        .build();
                return chain.proceed(modifiedRequest);
        };

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(headerInterceptor) // Add the header interceptor
                .addInterceptor(httpInterceptor)
                .proxy(Proxy.NO_PROXY) // Use no proxy
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
        gameAPI = retrofit.create(GameAPI.class);
        logger.info("GameRetrieveService created successfully.");
    }

    public Observable<GameJsonEntry> getGame() {
        return gameAPI.getGame(secretAPIKey, "7", gameSeries, true) // This returns an Observable<GameResponse>
                        .map(gameResponse -> gameResponse.getResults()) // Extract the List<GameJsonEntry>
                        .flatMapIterable(gameJsonEntry -> gameJsonEntry); // Slice the List<GameJsonEntry> into individual Observable<GameJsonEntry> items
    }
}

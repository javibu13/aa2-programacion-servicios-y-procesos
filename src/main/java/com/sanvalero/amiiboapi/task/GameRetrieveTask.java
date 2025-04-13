package com.sanvalero.amiiboapi.task;

import com.sanvalero.amiiboapi.entry.GameEntry;
import com.sanvalero.amiiboapi.model.GameJsonEntry;
import com.sanvalero.amiiboapi.service.GameRetrieveService;

import io.reactivex.functions.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class GameRetrieveTask extends Task<Object> {
    private static final Logger logger = LoggerFactory.getLogger(GameRetrieveTask.class);

    private String gameSeries;
    private ObservableList<GameEntry> gameObservableList;

    public GameRetrieveTask(String gameSeries, ObservableList<GameEntry> gameObservableList) {
        logger.info("Creating GameRetrieveTask with game series: {}", gameSeries);
        this.gameSeries = gameSeries;
        this.gameObservableList = gameObservableList;
    }

    @Override
    protected Object call() throws Exception {
        logger.info("Executing GameRetrieveTask for game series: {}", gameSeries);
        GameRetrieveService gameRetrieveService = new GameRetrieveService(gameSeries);
        Consumer<GameJsonEntry> consumer = gameJsonEntry -> {
            logger.info("Adding game entry: {}", gameJsonEntry.toString());
            Thread.sleep(100); // Simulate delay to show concurrency
            Platform.runLater(() -> {
                gameObservableList.add(new GameEntry(gameJsonEntry));
            });
        };
        gameRetrieveService.getGame()
                .subscribe(consumer, 
                        throwable -> {
                            logger.error("Error retrieving game information: {}", throwable.getMessage());
                        }, 
                        () -> {
                            logger.info("Game retrieval completed for game series: {}", gameSeries);
                        });
        return null;
    }

}

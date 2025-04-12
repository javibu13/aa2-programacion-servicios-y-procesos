package com.sanvalero.amiiboapi.task;

import com.sanvalero.amiiboapi.entry.AmiiboEntry;
import com.sanvalero.amiiboapi.model.AmiiboJsonEntry;
import com.sanvalero.amiiboapi.service.AmiiboRetrieveService;

import io.reactivex.functions.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class AmiiboRetrieveTask extends Task<Object> {
    private static final Logger logger = LoggerFactory.getLogger(AmiiboRetrieveTask.class);

    private String searchText;
    private ObservableList<AmiiboEntry> amiiboObservableList;
    public AmiiboRetrieveTask(String searchText, ObservableList<AmiiboEntry> amiiboObservableList) {
        logger.info("Creating AmiiboRetrieveTask with search arguments: {}", searchText);
        this.searchText = searchText;
        this.amiiboObservableList = amiiboObservableList;
    }

    @Override
    protected Object call() throws Exception {
        logger.info("Executing AmiiboRetrieveTask for search: {}", searchText);
        AmiiboRetrieveService amiiboRetrieveService = new AmiiboRetrieveService(searchText);
        Consumer<AmiiboJsonEntry> consumer = amiiboJsonEntry -> {
            logger.info("Adding amiibo entry: {}", amiiboJsonEntry.toString());
            Thread.sleep(100); // Simulate delay to show concurrency
            Platform.runLater(() -> {
                logger.info("Adding amiibo entry to observable list: {}", amiiboJsonEntry.toString());
                amiiboObservableList.add(new AmiiboEntry(amiiboJsonEntry));
            });
        };
        logger.info("Subscribing to amiibo retrieval service with search: {}", searchText);
        amiiboRetrieveService.getAmiibo()
                .doOnSubscribe(disposable -> logger.info("Subscription STARTED for amiibo retrieval."))
                .subscribe(consumer, 
                        throwable -> {
                            logger.error("Error retrieving amiibo information: {}", throwable.getMessage());
                        }, 
                        () -> {
                            logger.info("Amiibo retrieval completed for search: {}", searchText);
                        });
        return null;
    }

}

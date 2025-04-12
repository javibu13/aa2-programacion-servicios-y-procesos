package com.sanvalero.amiiboapi.task;

import com.sanvalero.amiiboapi.entry.AmiiboEntry;
import com.sanvalero.amiiboapi.model.AmiiboJsonEntry;
import com.sanvalero.amiiboapi.service.AmiiboRetrieveService;
import com.sanvalero.amiiboapi.util.FilterGroup;

import io.reactivex.functions.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class AmiiboRetrieveTask extends Task<Object> {
    private static final Logger logger = LoggerFactory.getLogger(AmiiboRetrieveTask.class);

    private FilterGroup filterGroup;
    private ObservableList<AmiiboEntry> amiiboObservableList;

    public AmiiboRetrieveTask(FilterGroup filterGroup, ObservableList<AmiiboEntry> amiiboObservableList) {
        logger.info("Creating AmiiboRetrieveTask with search arguments: {}", filterGroup.toString());
        this.filterGroup = filterGroup;
        this.amiiboObservableList = amiiboObservableList;
    }

    @Override
    protected Object call() throws Exception {
        logger.info("Executing AmiiboRetrieveTask for search: {}", filterGroup);
        AmiiboRetrieveService amiiboRetrieveService = new AmiiboRetrieveService(filterGroup);
        Consumer<AmiiboJsonEntry> consumer = amiiboJsonEntry -> {
            logger.info("Adding amiibo entry: {}", amiiboJsonEntry.toString());
            Thread.sleep(100); // Simulate delay to show concurrency
            Platform.runLater(() -> {
                amiiboObservableList.add(new AmiiboEntry(amiiboJsonEntry));
            });
        };
        amiiboRetrieveService.getAmiibo()
                .subscribe(consumer, 
                        throwable -> {
                            logger.error("Error retrieving amiibo information: {}", throwable.getMessage());
                        }, 
                        () -> {
                            logger.info("Amiibo retrieval completed for search: {}", filterGroup);
                        });
        return null;
    }

}

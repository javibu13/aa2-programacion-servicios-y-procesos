package com.sanvalero.amiiboapi.task;

import com.sanvalero.amiiboapi.entry.FilterEntry;
import com.sanvalero.amiiboapi.model.AmiiboFilterEntry;
import com.sanvalero.amiiboapi.service.FilterRetrieveService;

import io.reactivex.functions.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class FilterRetrieveTask extends Task<Object> {
    private static final Logger logger = LoggerFactory.getLogger(FilterRetrieveTask.class);

    private String filterName;
    private ObservableList<FilterEntry> filterObservableList;

    public FilterRetrieveTask(String filterName, ObservableList<FilterEntry> filterObservableList) {
        logger.info("Creating FilterRetrieveTask for filter: {}", filterName);
        this.filterName = filterName;
        this.filterObservableList = filterObservableList;
    }

    @Override
    protected Object call() throws Exception {
        logger.info("Executing FilterRetrieveTask for filter: {}", filterName);
        FilterRetrieveService filterRetrieveService = new FilterRetrieveService(filterName);
        Consumer<AmiiboFilterEntry> consumer = amiiboFilterEntry -> {
            logger.info("Adding filter entry: {}", amiiboFilterEntry.getName());
            Thread.sleep(250); // Simulate delay to show concurrency
            Platform.runLater(() -> {
                filterObservableList.add(new FilterEntry(amiiboFilterEntry.getKey(), amiiboFilterEntry.getName()));
            });
        };
        filterRetrieveService.getInformation()
                .subscribe(consumer, 
                        throwable -> {
                            logger.error("Error retrieving filter information: {}", throwable.getMessage());
                        }, 
                        () -> {
                            logger.info("Filter retrieval completed for filter: {}", filterName);
                        });
        return null;
    }

}

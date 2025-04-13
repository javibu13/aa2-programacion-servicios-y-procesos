package com.sanvalero.amiiboapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanvalero.amiiboapi.entry.AmiiboEntry;
import com.sanvalero.amiiboapi.entry.FilterEntry;
import com.sanvalero.amiiboapi.task.AmiiboRetrieveTask;
import com.sanvalero.amiiboapi.util.FilterGroup;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class SearchTabController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(SearchTabController.class);

    @FXML
    private CheckBox amiiboNameCharacterFilterCheckBox;
    @FXML
    private CheckBox amiiboAmiiboSeriesGameSeriesFilterCheckBox;
    @FXML
    private TextField amiiboSearchTextField;
    @FXML
    private Button amiiboSearchButton;
    @FXML
    private TableView<AmiiboEntry> amiiboTableView;
    @FXML
    private TableColumn<AmiiboEntry, ImageView> amiiboImageColumn;
    @FXML
    private TableColumn<AmiiboEntry, String> amiiboNameColumn;
    @FXML
    private TableColumn<AmiiboEntry, String> amiiboCharacterColumn;
    @FXML
    private TableColumn<AmiiboEntry, String> amiiboSeriesColumn;
    @FXML
    private TableColumn<AmiiboEntry, String> amiiboGameSeriesColumn;
    @FXML
    private TableColumn<AmiiboEntry, String> amiiboTypeColumn;

    private FilterGroup filterGroup;
    private ObservableList<AmiiboEntry> allAmiiboList;
    private FilteredList<AmiiboEntry> filteredAmiiboList;
    private AmiiboRetrieveTask amiiboRetrieveTask;

    
    public SearchTabController(FilterEntry typeEntry, FilterEntry seriesEntry, FilterEntry characterEntry) {
        filterGroup = new FilterGroup(typeEntry, seriesEntry, characterEntry);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing SearchTabController...");
        configureTableView();
        allAmiiboList = FXCollections.observableArrayList();
        filteredAmiiboList = new FilteredList<>(allAmiiboList, amiiboEntry -> true);
        amiiboTableView.setItems(filteredAmiiboList);
        amiiboRetrieveTask = new AmiiboRetrieveTask(filterGroup, allAmiiboList);
        amiiboRetrieveTask.setOnSucceeded(event -> {
            logger.info("Amiibo retrieval task completed successfully.");
            if (allAmiiboList.isEmpty()) {
                amiiboTableView.setPlaceholder(new Label("No amiibo found for the selected filters."));
            } else {
                amiiboTableView.setPlaceholder(null);
                allAmiiboList.forEach(AmiiboEntry::loadImageAsync);
                amiiboNameCharacterFilterCheckBox.setDisable(false);
                amiiboAmiiboSeriesGameSeriesFilterCheckBox.setDisable(false);
                amiiboSearchTextField.setDisable(false);
                amiiboSearchButton.setDisable(false);
            }
        });
        amiiboRetrieveTask.setOnFailed(event -> {
            logger.error("Amiibo retrieval task failed: {}", amiiboRetrieveTask.getException().getMessage());
        });
        new Thread(amiiboRetrieveTask).start();
    }

    public void cancelTask() {
        if (amiiboRetrieveTask != null && amiiboRetrieveTask.isRunning()) {
            amiiboRetrieveTask.cancel();
            logger.info("Amiibo retrieval task cancelled.");
        }
    }

    @FXML
    private void updateLocalFilters() {
        String searchText = amiiboSearchTextField.getText();
        filteredAmiiboList.setPredicate(entry -> {
            // Filter by search text
            boolean searchResult = true;
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseSearchText = searchText.toLowerCase();
                searchResult = entry.getName().toLowerCase().contains(lowerCaseSearchText ) ||
                entry.getCharacter().toLowerCase().contains(lowerCaseSearchText ) ||
                entry.getAmiiboSeries().toLowerCase().contains(lowerCaseSearchText ) ||
                entry.getGameSeries().toLowerCase().contains(lowerCaseSearchText ) ||
                entry.getType().toLowerCase().contains(lowerCaseSearchText );
            }
            // Filter by Amiibo Name and Character
            boolean filterNameCharacterResult = true;
            if (amiiboNameCharacterFilterCheckBox.isSelected()) {
                filterNameCharacterResult = entry.getName().toLowerCase().equals(entry.getCharacter().toLowerCase());
            }
            // Filter by Amiibo Series and Game Series
            boolean filterAmiiboSeriesGameSeriesResult = true;
            if (amiiboAmiiboSeriesGameSeriesFilterCheckBox.isSelected()) {
                filterAmiiboSeriesGameSeriesResult = entry.getAmiiboSeries().toLowerCase().equals(entry.getGameSeries().toLowerCase());
            }
            logger.debug("Filtering amiibo: {}, Search Result: {}, Filter Name/Character: {}, Filter Series/Game: {}",
                    entry.getName(), searchResult, filterNameCharacterResult, filterAmiiboSeriesGameSeriesResult);
            return searchResult && filterNameCharacterResult && filterAmiiboSeriesGameSeriesResult;
        });
    }

    private void configureTableView() {
        amiiboTableView.setPlaceholder(new Label("Loading..."));
        amiiboImageColumn.setCellValueFactory(cellData -> cellData.getValue().getImageViewProperty());
        amiiboNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        amiiboCharacterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCharacter()));
        amiiboSeriesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAmiiboSeries()));
        amiiboGameSeriesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGameSeries()));
        amiiboTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
    }
}

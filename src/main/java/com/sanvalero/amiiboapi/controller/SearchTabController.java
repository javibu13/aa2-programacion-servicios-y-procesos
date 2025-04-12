package com.sanvalero.amiiboapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanvalero.amiiboapi.entry.AmiiboEntry;
import com.sanvalero.amiiboapi.entry.FilterEntry;
import com.sanvalero.amiiboapi.task.AmiiboRetrieveTask;
import com.sanvalero.amiiboapi.task.FilterRetrieveTask;
import com.sanvalero.amiiboapi.util.FilterGroup;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;

public class SearchTabController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(SearchTabController.class);

    @FXML
    private TableView<AmiiboEntry> amiiboTableView;
    @FXML
    private TableColumn<AmiiboEntry, Image> amiiboImageColumn;
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
    private ObservableList<AmiiboEntry> amiiboList;
    private AmiiboRetrieveTask amiiboRetrieveTask;

    
    public SearchTabController(FilterEntry typeEntry, FilterEntry seriesEntry, FilterEntry characterEntry) {
        filterGroup = new FilterGroup(typeEntry, seriesEntry, characterEntry);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing SearchTabController...");
        configureTableView();
        amiiboList = FXCollections.observableArrayList();
        amiiboTableView.setItems(amiiboList);
        amiiboRetrieveTask = new AmiiboRetrieveTask(filterGroup, amiiboList);
        new Thread(amiiboRetrieveTask).start();
    }

    private void configureTableView() {
        amiiboTableView.setPlaceholder(new Label("No data available"));
        // TODO: Configure the image column to display images
        // amiiboImageColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<Image>(cellData.getValue().getImage()));
        amiiboNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        amiiboCharacterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCharacter()));
        amiiboSeriesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAmiiboSeries()));
        amiiboGameSeriesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGameSeries()));
        amiiboTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
    }
}

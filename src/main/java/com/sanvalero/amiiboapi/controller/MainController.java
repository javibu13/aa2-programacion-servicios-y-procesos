package com.sanvalero.amiiboapi.controller;

import com.sanvalero.amiiboapi.entry.FilterEntry;
import com.sanvalero.amiiboapi.task.FilterRetrieveTask;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Font;

public class MainController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private Label appTitleText;
    // TYPE
    @FXML
    private CheckBox typeCheckBox;
    @FXML
    private TableView<FilterEntry> typeTableView;
    @FXML
    private TableColumn<FilterEntry, String> typeKeyColumn;
    @FXML
    private TableColumn<FilterEntry, String> typeNameColumn;
    // SERIES
    @FXML
    private CheckBox seriesCheckBox;
    @FXML
    private TableView<FilterEntry> seriesTableView;
    @FXML
    private TableColumn<FilterEntry, String> seriesKeyColumn;
    @FXML
    private TableColumn<FilterEntry, String> seriesNameColumn;
    // CHARACTER
    @FXML
    private CheckBox characterCheckBox;
    @FXML
    private TableView<FilterEntry> characterTableView;
    @FXML
    private TableColumn<FilterEntry, String> characterKeyColumn;
    @FXML
    private TableColumn<FilterEntry, String> characterNameColumn;
    @FXML
    private TabPane searchTabPane;

    private ObservableList<FilterEntry> typeList;
    private ObservableList<FilterEntry> seriesList;
    private ObservableList<FilterEntry> characterList;
    private FilterRetrieveTask filterRetrieveTask;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("Initializing MainController...");
        try {
            var fontStream = getClass().getResourceAsStream("/fonts/RoSpritendo.otf");
            if (fontStream != null) {
                Font customFont = Font.loadFont(fontStream, 30);
                appTitleText.setFont(customFont);
            } else {
                logger.error("Font file not found: /fonts/RoSpritendo.otf");
            }
        } catch (Exception e) {
            logger.error("Error loading custom font", e);
        }
        configureTableView(typeTableView, typeKeyColumn, typeNameColumn);
        configureTableView(seriesTableView, seriesKeyColumn, seriesNameColumn);
        configureTableView(characterTableView, characterKeyColumn, characterNameColumn);
    }

    @FXML
    private void typeCheckBoxAction() {
        logger.debug("Type checkbox action triggered with value: " + typeCheckBox.isSelected());
        if (typeCheckBox.isSelected()) {
            typeTableView.disableProperty().set(false);
            typeList = FXCollections.observableArrayList();
            typeTableView.setItems(typeList);
            filterRetrieveTask = new FilterRetrieveTask("type", typeList);
            new Thread(filterRetrieveTask).start();
        } else {
            typeTableView.disableProperty().set(true);
            typeTableView.getItems().clear();
        }
    }

    @FXML
    private void seriesCheckBoxAction() {
        logger.debug("Series checkbox action triggered with value: " + seriesCheckBox.isSelected());
        if (seriesCheckBox.isSelected()) {
            seriesTableView.disableProperty().set(false);
            seriesList = FXCollections.observableArrayList();
            seriesTableView.setItems(seriesList);
            filterRetrieveTask = new FilterRetrieveTask("amiiboseries", seriesList);
            new Thread(filterRetrieveTask).start();
        } else {
            seriesTableView.disableProperty().set(true);
            seriesTableView.getItems().clear();
        }
    }

    @FXML
    private void characterCheckBoxAction() {
        logger.debug("Character checkbox action triggered with value: " + characterCheckBox.isSelected());
        if (characterCheckBox.isSelected()) {
            characterTableView.disableProperty().set(false);
            characterList = FXCollections.observableArrayList();
            characterTableView.setItems(characterList);
            filterRetrieveTask = new FilterRetrieveTask("character", characterList);
            new Thread(filterRetrieveTask).start();
        } else {
            characterTableView.disableProperty().set(true);
            characterTableView.getItems().clear();
        }
    }

    @FXML
    private void launchAmiiboSearch() {
        logger.info("Launching Amiibo Search...");
        logger.debug("Filter checkboxes: type={}, series={}, character={}", 
            typeCheckBox.isSelected(), seriesCheckBox.isSelected(), characterCheckBox.isSelected());
        logger.debug("Selection in tables: type={}, series={}, character={}", 
            typeTableView.getSelectionModel().getSelectedItem(), 
            seriesTableView.getSelectionModel().getSelectedItem(), 
            characterTableView.getSelectionModel().getSelectedItem());
        try {
            // Load the FXML file for the Amiibo Search tab
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchTab.fxml"));
            // SearchTabController searchTabController = new SearchTabController();
            // fxmlLoader.setController(searchTabController);
            Tab newTab = new Tab("Placeholder", fxmlLoader.load()); // TODO: Replace tab title with filters used to search
            newTab.setClosable(true); // Allow the tab to be closed
            // newTab.setUserData(searchTabController); // Store the controller in the tab for later access
            searchTabPane.getTabs().add(newTab);
            logger.info("Search tab added to TabPane.");
        } catch (Exception e) {
            logger.error("Error launching Amiibo Search", e);
        }
    }

    private void configureTableView(TableView<FilterEntry> tableView, TableColumn<FilterEntry, String> keyColumn, TableColumn<FilterEntry, String> nameColumn) {
        tableView.setPlaceholder(new Label("No data available"));
        keyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    }
}

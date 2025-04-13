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
    // TODO: Divide this into one task per filter
    // TODO: Control (stop) the task execution when the checkbox is unchecked
    private FilterRetrieveTask filterRetrieveTypeTask;
    private FilterRetrieveTask filterRetrieveSeriesTask;
    private FilterRetrieveTask filterRetrieveCharacterTask;

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
            filterRetrieveTypeTask = new FilterRetrieveTask("type", typeList);
            filterRetrieveTypeTask.setOnSucceeded(event -> {
                logger.info("FilterRetrieveTask for type completed successfully.");
                if (typeList.isEmpty()) {
                    typeTableView.setPlaceholder(new Label("No types found."));
                } else {
                    typeTableView.setPlaceholder(null);
                }
            });
            filterRetrieveTypeTask.setOnFailed(event -> {
                logger.error("FilterRetrieveTask for type failed: {}", filterRetrieveTypeTask.getException().getMessage());
                typeTableView.setPlaceholder(new Label("Error loading types."));
            });
            filterRetrieveTypeTask.setOnCancelled(event -> {
                logger.info("FilterRetrieveTask for type was cancelled.");
                typeTableView.setPlaceholder(new Label("Cancelled."));
            });
            typeTableView.setPlaceholder(new Label("Loading..."));
            new Thread(filterRetrieveTypeTask).start();
        } else {
            typeTableView.disableProperty().set(true);
            filterRetrieveTypeTask.cancel(true);
            logger.debug("filterRetrieveTypeTask cancelled: " + filterRetrieveTypeTask.isCancelled());
            typeTableView.getItems().clear();
            typeTableView.setPlaceholder(new Label("No data available"));
        }
    }

    @FXML
    private void seriesCheckBoxAction() {
        logger.debug("Series checkbox action triggered with value: " + seriesCheckBox.isSelected());
        if (seriesCheckBox.isSelected()) {
            seriesTableView.disableProperty().set(false);
            seriesList = FXCollections.observableArrayList();
            seriesTableView.setItems(seriesList);
            filterRetrieveSeriesTask = new FilterRetrieveTask("amiiboseries", seriesList);
            filterRetrieveSeriesTask.setOnSucceeded(event -> {
                logger.info("FilterRetrieveTask for series completed successfully.");
                if (seriesList.isEmpty()) {
                    seriesTableView.setPlaceholder(new Label("No series found."));
                } else {
                    seriesTableView.setPlaceholder(null);
                }
            });
            filterRetrieveSeriesTask.setOnFailed(event -> {
                logger.error("FilterRetrieveTask for series failed: {}", filterRetrieveSeriesTask.getException().getMessage());
                seriesTableView.setPlaceholder(new Label("Error loading series."));
            });
            filterRetrieveSeriesTask.setOnCancelled(event -> {
                logger.info("FilterRetrieveTask for series was cancelled.");
                seriesTableView.setPlaceholder(new Label("Cancelled."));
            });
            seriesTableView.setPlaceholder(new Label("Loading..."));
            new Thread(filterRetrieveSeriesTask).start();
        } else {
            seriesTableView.disableProperty().set(true);
            filterRetrieveSeriesTask.cancel(true);
            logger.debug("filterRetrieveSeriesTask cancelled: " + filterRetrieveSeriesTask.isCancelled());
            seriesTableView.getItems().clear();
            seriesTableView.setPlaceholder(new Label("No data available"));
        }
    }

    @FXML
    private void characterCheckBoxAction() {
        logger.debug("Character checkbox action triggered with value: " + characterCheckBox.isSelected());
        if (characterCheckBox.isSelected()) {
            characterTableView.disableProperty().set(false);
            characterList = FXCollections.observableArrayList();
            characterTableView.setItems(characterList);
            filterRetrieveCharacterTask = new FilterRetrieveTask("character", characterList);
            filterRetrieveCharacterTask.setOnSucceeded(event -> {
                logger.info("FilterRetrieveTask for character completed successfully.");
                if (characterList.isEmpty()) {
                    characterTableView.setPlaceholder(new Label("No characters found."));
                } else {
                    characterTableView.setPlaceholder(null);
                }
            });
            filterRetrieveCharacterTask.setOnFailed(event -> {
                logger.error("FilterRetrieveTask for character failed: {}", filterRetrieveCharacterTask.getException().getMessage());
                characterTableView.setPlaceholder(new Label("Error loading characters."));
            });
            filterRetrieveCharacterTask.setOnCancelled(event -> {
                logger.info("FilterRetrieveTask for character was cancelled.");
                characterTableView.setPlaceholder(new Label("Cancelled."));
            });
            characterTableView.setPlaceholder(new Label("Loading..."));
            new Thread(filterRetrieveCharacterTask).start();
        } else {
            characterTableView.disableProperty().set(true);
            filterRetrieveCharacterTask.cancel(true);
            logger.debug("filterRetrieveCharacterTask cancelled: " + filterRetrieveCharacterTask.isCancelled());
            characterTableView.getItems().clear();
            characterTableView.setPlaceholder(new Label("No data available"));
        }
    }

    @FXML
    private void launchAmiiboSearch() {
        logger.info("Launching Amiibo Search...");
        logger.debug("Filter checkboxes: type={}, series={}, character={}", 
            typeCheckBox.isSelected(), seriesCheckBox.isSelected(), characterCheckBox.isSelected());
        FilterEntry type = typeTableView.getSelectionModel().getSelectedItem();
        FilterEntry series = seriesTableView.getSelectionModel().getSelectedItem();
        FilterEntry character = characterTableView.getSelectionModel().getSelectedItem();
        String tabName = (type != null ? "T:" + type.getName() + " " : "") + 
            (series != null ? "S:" + series.getName() + " " : "") + 
            (character != null ? "C:" + character.getName() + " " : "");
        if (tabName.isEmpty()) {
            tabName = "Amiibo Search";
        }
        logger.debug("Selection in tables: type={}, series={}, character={}", 
            type != null ? type.getKey() + " - " + type.getName() : "null",
            series != null ? series.getKey() + " - " + series.getName() : "null",
            character != null ? character.getKey() + " - " + character.getName() : "null"
        );
        try {
            // Load the FXML file for the Amiibo Search tab
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchTab.fxml"));
            SearchTabController searchTabController = new SearchTabController(type, series, character);
            fxmlLoader.setController(searchTabController);
            Tab newTab = new Tab(tabName, fxmlLoader.load());
            newTab.setClosable(true); // Allow the tab to be closed
            newTab.setUserData(searchTabController); // Store the controller in the tab for later access
            newTab.setOnClosed(event -> {
                Tab closedTab = (Tab) event.getSource();
                Object userData = closedTab.getUserData();

                // Por ejemplo, si has guardado un controlador:
                if (userData instanceof SearchTabController controller) {
                    controller.cancelTask(); // Cancel the task on tab close
                    logger.info("Search tab closed, task cancelled.");
                }
            });
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

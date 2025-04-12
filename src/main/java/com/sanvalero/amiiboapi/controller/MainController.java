package com.sanvalero.amiiboapi.controller;

import com.sanvalero.amiiboapi.entry.FilterEntry;
import com.sanvalero.amiiboapi.task.FilterRetrieveTask;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
    // SERIES
    @FXML
    private CheckBox seriesCheckBox;
    @FXML
    private TableView<FilterEntry> seriesTableView;
    // CHARACTER
    @FXML
    private CheckBox characterCheckBox;
    @FXML
    private TableView<FilterEntry> characterTableView;
    @FXML
    private TabPane searchTabPane;

    private ObservableList<FilterEntry> typeList;
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
            // TODO: Launch Amiibo's Series retrieval process
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
            // TODO: Launch Amiibo's Character retrieval process
        } else {
            characterTableView.disableProperty().set(true);
            characterTableView.getItems().clear();
        }
    }

    @FXML
    private void launchAmiiboSearch() {
        logger.info("Launching Amiibo Search...");
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
}

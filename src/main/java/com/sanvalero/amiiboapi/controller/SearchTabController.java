package com.sanvalero.amiiboapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanvalero.amiiboapi.entry.AmiiboEntry;
import com.sanvalero.amiiboapi.entry.FilterEntry;
import com.sanvalero.amiiboapi.entry.GameEntry;
import com.sanvalero.amiiboapi.task.AmiiboRetrieveTask;
import com.sanvalero.amiiboapi.task.GameRetrieveTask;
import com.sanvalero.amiiboapi.util.FilterGroup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

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
    @FXML
    private Button amiiboGameListButton;
    @FXML
    private CheckBox amiiboCompressToZipCheckBox;
    @FXML
    private Button amiiboExportDataButton;

    private String tabName;
    private FilterGroup filterGroup;
    private ObservableList<AmiiboEntry> allAmiiboList;
    private FilteredList<AmiiboEntry> filteredAmiiboList;
    private AmiiboRetrieveTask amiiboRetrieveTask;
    private ObservableList<GameEntry> gameList;

    
    public SearchTabController(FilterEntry typeEntry, FilterEntry seriesEntry, FilterEntry characterEntry) {
        filterGroup = new FilterGroup(typeEntry, seriesEntry, characterEntry);
        tabName = (typeEntry != null ? "T:" + typeEntry.getName() + " " : "") + 
            (seriesEntry != null ? "S:" + seriesEntry.getName() + " " : "") + 
            (characterEntry != null ? "C:" + characterEntry.getName() + " " : "");
        if (tabName.isEmpty()) {
            tabName = "Amiibo Search";
        }
        tabName = tabName.replaceAll(" ", "_").replaceAll(":", "_").replaceAll("\\s+", "_");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing SearchTabController...");
        configureTableView();
        amiiboTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                amiiboGameListButton.setDisable(false);
            } else {
                amiiboGameListButton.setDisable(true);
            }
        });
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
                amiiboCompressToZipCheckBox.setDisable(false);
                amiiboExportDataButton.setDisable(false);
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

    @FXML
    private void exportDataToFile() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Export Folder");
        File selectedDirectory = directoryChooser.showDialog(amiiboTableView.getScene().getWindow());
        if (selectedDirectory == null) {
            return; // User cancelled
        }
        boolean compressToZip = amiiboCompressToZipCheckBox.isSelected();
        String fileName = tabName + "_export_" + DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        fileName += compressToZip ? ".zip" : ".csv";
        Path outputPath = selectedDirectory.toPath().resolve(fileName);

        exportAmiiboDataAsync(filteredAmiiboList, outputPath, compressToZip);
    }

    @FXML
    private void openGameList() {
        AmiiboEntry selectedAmiibo = amiiboTableView.getSelectionModel().getSelectedItem();
        if (selectedAmiibo != null) {
            // Create a new window to display a tableView of games for the selected amiibo retieved from other API not implemented yet
            Stage gameListStage = new Stage();
            gameListStage.setTitle("Game List for " + selectedAmiibo.getGameSeries());
            // Create a TableView and populate it with the games for the selected amiibo
            // This part is not implemented yet, as it requires a separate API to retrieve the game list
            // For now, just create the empty tableView and set it to the stage
            TableView<GameEntry> gameListTableView = new TableView<>();
            TableColumn<GameEntry, String> gameNameColumn = new TableColumn<>("Game Name");
            TableColumn<GameEntry, String> gameReleaseDateColumn = new TableColumn<>("Release Date");
            TableColumn<GameEntry, String> gameScoreColumn = new TableColumn<>("Game Score");
            gameNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
            gameReleaseDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReleaseDate()));
            gameScoreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getScore()));
            gameListTableView.getColumns().add(gameNameColumn);
            gameListTableView.getColumns().add(gameReleaseDateColumn);
            gameListTableView.getColumns().add(gameScoreColumn);
            gameListTableView.setPlaceholder(new Label("No games found for this game series."));
            gameListTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            gameListStage.setScene(new Scene(gameListTableView, 600, 400));
            gameList = FXCollections.observableArrayList();
            gameListTableView.setItems(gameList);
            GameRetrieveTask gameRetrieveTask = new GameRetrieveTask(selectedAmiibo.getGameSeries(), gameList);
            gameRetrieveTask.setOnSucceeded(event -> {
                logger.info("Game retrieval task completed successfully.");
                if (gameList.isEmpty()) {
                    gameListTableView.setPlaceholder(new Label("No games found for the selected game series."));
                } else {
                    gameListTableView.setPlaceholder(null);
                }
            });
            gameRetrieveTask.setOnFailed(event -> {
                logger.error("Game retrieval task failed: {}", gameRetrieveTask.getException().getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Game retrieval failed!");
                alert.setContentText("An error occurred while retrieving the game list: " + gameRetrieveTask.getException().getMessage());
                alert.showAndWait();
            });
            gameListStage.show();
            new Thread(gameRetrieveTask).start();
        }
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

    private void exportAmiiboToCsv(List<AmiiboEntry> amiiboList, Path filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("Name,Character,Amiibo Series,Game Series,Type,Image URL");
            writer.newLine();
            for (AmiiboEntry entry : amiiboList) {
                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        entry.getName(),
                        entry.getCharacter(),
                        entry.getAmiiboSeries(),
                        entry.getGameSeries(),
                        entry.getType(),
                        entry.getImageUrl()));
                writer.newLine();
            }
        }
    }

    private void zipFile(Path sourceFile, Path zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            ZipEntry zipEntry = new ZipEntry(sourceFile.getFileName().toString());
            zos.putNextEntry(zipEntry);
            Files.copy(sourceFile, zos);
            zos.closeEntry();
        }
    }

    public void exportAmiiboDataAsync(List<AmiiboEntry> amiiboList, Path outputPath, boolean compressToZip) {
        CompletableFuture.runAsync(() -> {
            try {
                Path csvPath = outputPath;
                if (compressToZip) {
                    csvPath = Files.createTempFile("amiibo_export", ".csv");
                }
                
                exportAmiiboToCsv(amiiboList, csvPath);

                if (compressToZip) {
                    zipFile(csvPath, outputPath);
                    Files.deleteIfExists(csvPath);
                }

                Platform.runLater(() -> {
                    logger.info("Export completed successfully: {}", outputPath);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Export Successful");
                    alert.setHeaderText("Export completed successfully!");
                    alert.setContentText("The data has been successfully exported to " + outputPath);
                    alert.showAndWait();
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    logger.error("Export failed: {}", e.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Export Failed");
                    alert.setHeaderText("Export failed!");
                    alert.setContentText("An error occurred while exporting the data: " + e.getMessage());
                });
            }
        });
    }

}

package com.sanvalero.amiiboapi.entry;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanvalero.amiiboapi.model.AmiiboJsonEntry;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmiiboEntry {
    private static final Logger logger = LoggerFactory.getLogger(AmiiboEntry.class);

    private String amiiboSeries;
    private String character;
    private String gameSeries;
    private String imageUrl;
    private ImageView image;
    private String name;
    private String type;
    private transient ObjectProperty<ImageView> imageViewProperty = new SimpleObjectProperty<>(new ImageView());

    public AmiiboEntry(AmiiboJsonEntry amiiboJsonEntry) {
        this.amiiboSeries = amiiboJsonEntry.getAmiiboSeries();
        this.character = amiiboJsonEntry.getCharacter();
        this.gameSeries = amiiboJsonEntry.getGameSeries();
        this.imageUrl = amiiboJsonEntry.getImage();
        this.name = amiiboJsonEntry.getName();
        this.type = amiiboJsonEntry.getType();
        
        // Create ImageView from URL at entry creation
        // Image img = new Image(this.imageUrl, 64, 64, true, true);
        // this.image = new ImageView(img);
        // // this.image.setFitWidth(64);
        // this.image.setPreserveRatio(true);
    }

    @Override
    public String toString() {
        return name + " (" + amiiboSeries + ")";
    }

    public void loadImageAsync() {
        Task<Image> getImageTask = new Task<>() {
            @Override
            protected Image call() {
                return new Image(imageUrl, 128, 128, true, true);
            }
        };

        getImageTask.setOnSucceeded(event -> {
            ImageView imageView = new ImageView(getImageTask.getValue());
            imageView.setFitWidth(128);
            // imageView.setFitHeight(64);
            imageView.setPreserveRatio(true);
            imageViewProperty.set(imageView);
        });

        getImageTask.setOnFailed(event -> {
            logger.error("Failed to load image from URL: {}", imageUrl);
            // Load a placeholder image or handle the error as needed
            ImageView imageView = new ImageView(new Image(getClass().getResource("/images/amiibo.png").toExternalForm()));
            imageView.setFitWidth(128);
            // imageView.setFitHeight(64);
            imageView.setPreserveRatio(true);
            imageViewProperty.set(imageView);
        });

        new Thread(getImageTask).start();
    }
}
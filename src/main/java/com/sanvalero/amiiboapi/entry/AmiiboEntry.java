package com.sanvalero.amiiboapi.entry;

import lombok.Data;

import com.sanvalero.amiiboapi.model.AmiiboJsonEntry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmiiboEntry {
    private String amiiboSeries;
    private String character;
    private String gameSeries;
    private String image;   // TODO: Change from String to URL/Image 
    private String name;
    private String type;

    public AmiiboEntry(AmiiboJsonEntry amiiboJsonEntry) {
        this.amiiboSeries = amiiboJsonEntry.getAmiiboSeries();
        this.character = amiiboJsonEntry.getCharacter();
        this.gameSeries = amiiboJsonEntry.getGameSeries();
        this.image = amiiboJsonEntry.getImage();
        this.name = amiiboJsonEntry.getName();
        this.type = amiiboJsonEntry.getType();
    }

    @Override
    public String toString() {
        return name + " (" + amiiboSeries + ")";
    }
}
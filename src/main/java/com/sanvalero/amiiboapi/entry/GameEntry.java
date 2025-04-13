package com.sanvalero.amiiboapi.entry;

import lombok.Data;

import com.sanvalero.amiiboapi.model.GameJsonEntry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameEntry {
    private String name;
    private String releaseDate;
    private String score;

    public GameEntry(GameJsonEntry gameJsonEntry) {
        this.name = gameJsonEntry.getName();
        this.releaseDate = gameJsonEntry.getReleased();
        this.score = gameJsonEntry.getRating() + " / 5.0";
    }
}

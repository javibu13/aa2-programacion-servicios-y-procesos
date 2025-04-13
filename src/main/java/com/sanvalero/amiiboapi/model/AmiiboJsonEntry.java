package com.sanvalero.amiiboapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmiiboJsonEntry {
    private String amiiboSeries;
    private String character;
    private String gameSeries;
    private String image;
    private String name;
    private String type;

    @Override
    public String toString() {
        return name + " (" + amiiboSeries + ")";
    }
}

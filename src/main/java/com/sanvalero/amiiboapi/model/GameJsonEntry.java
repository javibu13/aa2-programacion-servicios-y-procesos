package com.sanvalero.amiiboapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameJsonEntry {
    private String name;
    private String released;
    private String rating;

    @Override
    public String toString() {
        return name;
    }
}

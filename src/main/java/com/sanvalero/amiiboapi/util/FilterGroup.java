package com.sanvalero.amiiboapi.util;

import lombok.Data;

import com.sanvalero.amiiboapi.entry.FilterEntry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterGroup {
    private String type;
    private String series;
    private String character;

    public FilterGroup(FilterEntry type, FilterEntry series, FilterEntry character) {
        this.type = type != null ? type.getKey() : null;
        this.series = series != null ? series.getKey() : null;
        this.character = character != null ? character.getKey() : null;
    }

    @Override
    public String toString() {
        return "type: " + type + ", series: " + series + ", character: " + character;
    }
}

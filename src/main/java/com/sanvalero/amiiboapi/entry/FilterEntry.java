package com.sanvalero.amiiboapi.entry;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterEntry {
    private String key;
    private String name;
}

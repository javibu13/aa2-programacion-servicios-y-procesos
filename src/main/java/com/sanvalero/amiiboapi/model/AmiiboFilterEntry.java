package com.sanvalero.amiiboapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmiiboFilterEntry {
    private String key;
    private String name;
}

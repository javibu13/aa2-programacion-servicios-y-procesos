package com.sanvalero.amiiboapi.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmiiboFilterResponse {
    private List<AmiiboFilterEntry> amiibo;
}

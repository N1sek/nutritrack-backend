package com.nutritrack.nutritrackbackend.dto.response.external.openfood;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OpenFoodFactsResponse {
    private List<OpenFoodFactsProduct> products;
}


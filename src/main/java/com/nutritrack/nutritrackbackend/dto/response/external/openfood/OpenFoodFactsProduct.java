package com.nutritrack.nutritrackbackend.dto.response.external.openfood;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenFoodFactsProduct {
    private String product_name;
    private String image_url;
    private NutrimentsDTO nutriments;
    private String brands;
    private List<String> categories_tags;

}



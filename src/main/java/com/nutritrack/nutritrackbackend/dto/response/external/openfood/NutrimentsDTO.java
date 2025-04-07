package com.nutritrack.nutritrackbackend.dto.response.external.openfood;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NutrimentsDTO {

    @JsonProperty("energy-kcal_100g")
    private Double calories;

    @JsonProperty("proteins_100g")
    private Double protein;

    @JsonProperty("fat_100g")
    private Double fat;

    @JsonProperty("carbohydrates_100g")
    private Double carbs;

    @JsonProperty("sugars_100g")
    private Double sugar;

    @JsonProperty("salt_100g")
    private Double salt;

    @JsonProperty("saturated-fat_100g")
    private Double saturatedFat;
}




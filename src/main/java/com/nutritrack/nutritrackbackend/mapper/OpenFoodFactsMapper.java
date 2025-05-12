package com.nutritrack.nutritrackbackend.mapper;

import com.nutritrack.nutritrackbackend.dto.response.external.openfood.NutrimentsDTO;
import com.nutritrack.nutritrackbackend.dto.response.external.openfood.OpenFoodFactsProduct;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;

public class OpenFoodFactsMapper {

    public static FoodResponse mapToFoodResponse(OpenFoodFactsProduct product) {
        NutrimentsDTO nutriments = product.getNutriments();

        return FoodResponse.builder()
                .name(product.getProduct_name())
                .imageUrl(product.getImage_url())
                .calories(round(nutriments.getCalories()))
                .protein(round(nutriments.getProtein()))
                .fat(round(nutriments.getFat()))
                .carbs(round(nutriments.getCarbs()))
                .sugar(round(nutriments.getSugar()))
                .salt(round(nutriments.getSalt()))
                .saturatedFat(round(nutriments.getSaturatedFat()))
                .imported(true)
                .build();
    }

    private static Double round(Double value) {
        if (value == null) return null;
        return Math.round(value * 100.0) / 100.0;
    }


}



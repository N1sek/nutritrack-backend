package com.nutritrack.nutritrackbackend.mapper;

import com.nutritrack.nutritrackbackend.dto.response.external.openfood.NutrimentsDTO;
import com.nutritrack.nutritrackbackend.dto.response.external.openfood.OpenFoodFactsProduct;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;

public class OpenFoodFactsMapper {

    public static FoodResponse mapToFoodResponse(OpenFoodFactsProduct product) {
        NutrimentsDTO nutriments = product.getNutriments();
        System.out.println("Azúcar: " + nutriments.getSugar());
        System.out.println("Sal: " + nutriments.getSalt());
        System.out.println("Saturadas: " + nutriments.getSaturatedFat());


        return FoodResponse.builder()
                .name(product.getProduct_name())
                .imageUrl(product.getImage_url())
                .calories(nutriments.getCalories())
                .protein(nutriments.getProtein())
                .fat(nutriments.getFat())
                .carbs(nutriments.getCarbs())
                .sugar(nutriments.getSugar())
                .salt(nutriments.getSalt())
                .saturatedFat(nutriments.getSaturatedFat())
                .imported(true)
                .build();
    }
}



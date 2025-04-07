package com.nutritrack.nutritrackbackend.dto.response.food;

import com.nutritrack.nutritrackbackend.dto.response.allergen.AllergenResponse;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodResponse {

    private Long id;
    private String name;
    private String imageUrl;

    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double sugar;
    private Double salt;
    private Double saturatedFat;


    private Set<AllergenResponse> allergens;
    private String createdBy;
    private boolean imported;
}


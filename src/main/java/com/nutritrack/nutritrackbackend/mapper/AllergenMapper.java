package com.nutritrack.nutritrackbackend.mapper;

import com.nutritrack.nutritrackbackend.dto.response.allergen.AllergenResponse;
import com.nutritrack.nutritrackbackend.entity.Allergen;
import org.springframework.stereotype.Component;

@Component
public class AllergenMapper {

    public AllergenResponse toResponse(Allergen allergen) {
        if (allergen == null) return null;

        return AllergenResponse.builder()
                .id(allergen.getId())
                .name(allergen.getName())
                .iconUrl(allergen.getIconUrl())
                .build();
    }
}

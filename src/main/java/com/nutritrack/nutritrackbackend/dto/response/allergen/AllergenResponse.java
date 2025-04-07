package com.nutritrack.nutritrackbackend.dto.response.allergen;

import lombok.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllergenResponse {
    private Long id;
    private String name;
    private String iconUrl;
}

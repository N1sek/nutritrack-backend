package com.nutritrack.nutritrackbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;


@Entity
@Table(name = "recipe_food")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Recipe recipe;

    @ManyToOne
    private Food food;

    @DecimalMin(value = "0.0", inclusive = false)
    private Double quantityInGrams; // o mililitros
}


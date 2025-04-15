package com.nutritrack.nutritrackbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "foods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String imageUrl;

    @DecimalMin(value = "0.0", inclusive = true)
    private Double calories;

    private Double protein;
    private Double fat;
    private Double carbs;
    private Double sugar;
    private Double salt;
    private Double saturatedFat;


    @ManyToMany
    @JoinTable(
            name = "food_allergen",
            joinColumns = @JoinColumn(name = "food_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    private Set<Allergen> allergens = new HashSet<>();

    @ManyToOne
    private User createdBy;

    private boolean imported = false; // true si viene de OpenFoodFacts

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> usedInRecipes = new HashSet<>();
}


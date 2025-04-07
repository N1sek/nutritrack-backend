package com.nutritrack.nutritrackbackend.entity;

import com.nutritrack.nutritrackbackend.enums.MealType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @ManyToOne
    private User createdBy;

    @ManyToMany
    @JoinTable(
            name = "recipe_favorites",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> favoritedBy = new HashSet<>();

    private LocalDateTime createdAt;
}

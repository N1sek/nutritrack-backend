package com.nutritrack.nutritrackbackend.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "allergens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Allergen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String iconUrl;
}


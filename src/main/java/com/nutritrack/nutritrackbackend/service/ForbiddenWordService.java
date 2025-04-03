package com.nutritrack.nutritrackbackend.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

// TODO
/*
Crear una entidad ForbiddenWord
Guardar las palabras en BBDD
Hacer que ForbiddenWordService las lea desde el repositorio

Esto es para poder editar las palabras desde el frontend en el panel de ADMIN
 */

@Service
@Getter
public class ForbiddenWordService {

    private final Set<String> forbiddenWords = new HashSet<>();

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("forbidden-nicknames.txt").getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                forbiddenWords.add(line.trim().toLowerCase());
            }

        } catch (Exception e) {
            System.err.println("No se pudieron cargar las palabras prohibidas: " + e.getMessage());
        }
    }
}

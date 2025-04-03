package com.nutritrack.nutritrackbackend.validation;

import com.nutritrack.nutritrackbackend.service.ForbiddenWordService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class NoForbiddenWordsValidator implements ConstraintValidator<NoForbiddenWords, String> {

    @Autowired
    private ForbiddenWordService forbiddenWordService;

    private static final String EMOJI_REGEX = "[\\p{So}\\p{Cn}]+";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String nickname = value.toLowerCase().trim();

        // Palabras prohibidas
        if (forbiddenWordService.getForbiddenWords().stream().anyMatch(nickname::contains)) {
            return false;
        }

        // Regex para detectar muchos emojis
        if (nickname.matches(".*" + EMOJI_REGEX + ".*")) {
            return false;
        }

        // 3. Nickname vacio despues de limpieza
        if (nickname.replaceAll("[^a-zA-Z0-9]", "").isEmpty()) {
            return false;
        }

        return true;
    }
}

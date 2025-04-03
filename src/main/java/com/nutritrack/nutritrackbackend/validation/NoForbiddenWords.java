package com.nutritrack.nutritrackbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoForbiddenWordsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoForbiddenWords {
    String message() default "El nombre contiene palabras no permitidas";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

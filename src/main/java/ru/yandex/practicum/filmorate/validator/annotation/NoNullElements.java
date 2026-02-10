package ru.yandex.practicum.filmorate.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.implementation.NoNullElementsValidator;

import java.lang.annotation.*;

@SuppressWarnings("unused")
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NoNullElementsValidator.class)
public @interface NoNullElements {
    String message() default "Коллекция не должна содержать null-элементы";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

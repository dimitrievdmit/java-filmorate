package ru.yandex.practicum.filmorate.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.implementation.AfterSpecifiedDateImpl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@SuppressWarnings("unused")
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = AfterSpecifiedDateImpl.class)
public @interface AfterSpecifiedDate {
    String minDate() default "";

    @SuppressWarnings("UnusedReturnValue") String message() default "Дата раньше минимально допустимой";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

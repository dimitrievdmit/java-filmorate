package ru.yandex.practicum.filmorate.validator.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.validator.annotation.AfterSpecifiedDate;

import java.time.LocalDate;

public class AfterSpecifiedDateImpl implements ConstraintValidator<AfterSpecifiedDate, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(AfterSpecifiedDate constraintAnnotation) {
        this.minDate = LocalDate.parse(constraintAnnotation.minDate());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return !localDate.isBefore(minDate);
    }
}

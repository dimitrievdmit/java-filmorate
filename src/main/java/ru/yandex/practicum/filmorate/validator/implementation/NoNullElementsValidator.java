package ru.yandex.practicum.filmorate.validator.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.validator.annotation.NoNullElements;

import java.util.Collection;
import java.util.Objects;

public class NoNullElementsValidator implements ConstraintValidator<NoNullElements, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> collection, ConstraintValidatorContext context) {
        // Если коллекция null — это допустимо
        if (collection == null) {
            return true;
        }
        // Проверяем, что нет null-элементов внутри
        return collection.stream().noneMatch(Objects::isNull);
    }
}

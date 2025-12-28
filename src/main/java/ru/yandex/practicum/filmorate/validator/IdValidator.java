package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
public class IdValidator {
    public static void validateId(Long id, String message) {
        if (id == null) {
            log.error("Ошибка: {}", message);
            throw new ValidationException(message);
        }
    }
}

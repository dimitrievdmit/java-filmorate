package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final String MIN_RELEASE_DATE_STR = "1895-12-28";
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.parse(MIN_RELEASE_DATE_STR);

    public static void validateId(Film film) {
        if (film.getId() == null) {
            log.error("Ошибка: не указан id фильма");
            throw new ValidationException("Id фильма должен быть указан");
        }
    }
}

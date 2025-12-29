package ru.yandex.practicum.filmorate.validator;


import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class Validator {
    public static final String LOGIN_REGEXP = "^\\S*$";
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final String MIN_RELEASE_DATE_STR = "1895-12-28";
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.parse(MIN_RELEASE_DATE_STR);

    public static void validateId(Long id, String message) {
        if (id == null) {
            log.error("Ошибка: {}", message);
            throw new ValidationException(message);
        }
    }

    public static void fillNameWithLoginIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Отсутствует имя пользователя с логином {}. Использовать логин в качестве имени", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}

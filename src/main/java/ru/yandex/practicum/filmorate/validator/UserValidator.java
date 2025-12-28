package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserValidator {
    public static final String LOGIN_REGEXP = "^\\S*$";


    public static void fillNameWithLoginIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Отсутствует имя пользователя с логином {}. Использовать логин в качестве имени", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}

package ru.yandex.practicum.filmorate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserExistenceValidator {

    private final UserStorage userStorage;

    public void checkThatUserExists(Long userId) {
        // Проверяем на null
        Validator.validateId(userId, "Id пользователя должен быть указан");
        // Проверяем существование в хранилище
        if (userStorage.checkIfNotExists(userId)) {
            String errText = "Пользователь с id = " + userId + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
    }
}

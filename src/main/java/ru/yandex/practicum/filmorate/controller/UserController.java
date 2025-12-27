package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя {}", user.getLogin());
        UserValidator.fillNameWithLoginIfEmpty(user);
        // формируем дополнительные данные
        log.debug("Формируем id пользователя {}", user.getLogin());
        user.setId(getNextId());
        // сохраняем в памяти приложения
        log.debug("Сохраняем пользователя {} в памяти приложения", user.getLogin());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Обновление пользователя {}", newUser.getLogin());
        // проверяем необходимые условия
        UserValidator.fillNameWithLoginIfEmpty(newUser);
        UserValidator.validateId(newUser);
        if (!users.containsKey(newUser.getId())) {
            String errText = "Пользователь с id = " + newUser.getId() + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }

        // если найден и все условия соблюдены, обновляем содержимое
        User oldUser = users.get(newUser.getId());
        log.debug("setEmail");
        oldUser.setEmail(newUser.getEmail());
        log.debug("setLogin");
        oldUser.setLogin(newUser.getLogin());
        log.debug("setName");
        oldUser.setName(newUser.getName());
        log.debug("setBirthday");
        oldUser.setBirthday(newUser.getBirthday());
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

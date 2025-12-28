package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.IdValidator;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
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

    @Override
    public User getUser(Long id) {
        log.debug("Получение пользователя по id {}", id);
        IdValidator.validateId(id, "Id пользователя должен быть указан");
        checkIfExists(id);
        return users.get(id);
    }

    @Override
    public User updateUser(User newUser) {
        log.info("Обновление пользователя {}", newUser.getLogin());
        UserValidator.fillNameWithLoginIfEmpty(newUser);
        User oldUser = getUser(newUser.getId());
        return updateUserFields(oldUser, newUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя по id {}", id);
        User user = getUser(id);
        log.info("Удаление пользователя {}", user.getName());
        users.remove(id);
    }

    private void checkIfExists(long id) {
        if (!users.containsKey(id)) {
            String errText = "Пользователь с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
    }

    private User updateUserFields(User oldUser, User newUser) {
        log.trace("setEmail");
        oldUser.setEmail(newUser.getEmail());
        log.trace("setLogin");
        oldUser.setLogin(newUser.getLogin());
        log.trace("setName");
        oldUser.setName(newUser.getName());
        log.trace("setBirthday");
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

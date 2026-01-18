package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Validator;

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
        Validator.fillNameWithLoginIfEmpty(user);
        // формируем дополнительные данные
        log.info("Формируем id пользователя {}", user.getLogin());
        user.setId(getNextId());
        // сохраняем в памяти приложения
        log.info("Сохраняем пользователя {} в памяти приложения", user.getLogin());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        log.info("Получение пользователя по id {}", id);
        Validator.validateId(id, "Id пользователя должен быть указан");
        checkIfExists(id);
        return users.get(id);
    }

    @Override
    public User updateUser(User newUser) {
        log.info("Обновление пользователя {}", newUser.getLogin());
        Validator.fillNameWithLoginIfEmpty(newUser);
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
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
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

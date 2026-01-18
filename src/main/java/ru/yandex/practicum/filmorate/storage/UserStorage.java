package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();

    User createUser(User film);

    User getUser(Long id);

    User updateUser(User newUser);

    void deleteUser(Long id);
}

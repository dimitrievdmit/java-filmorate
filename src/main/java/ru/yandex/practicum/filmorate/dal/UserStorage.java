package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();

    User createUser(User film);

    User getUser(Long id);

    User updateUser(User newUser);

    void deleteUser(Long id);

    boolean checkIfNotExists(Long id);

    void userAddUnconfirmedFriend(Long id, Long friendId);

    void confirmFriendship(Long id, Long friendId);

    void userDeleteFriend(Long id, Long friendId);
}

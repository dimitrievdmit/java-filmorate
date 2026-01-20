package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(@Valid User user) {
        return userStorage.createUser(user);
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public User updateUser(@Valid User newUser) {
        return userStorage.updateUser(newUser);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public User userAddFriend(Long id, Long friendId) {
        log.info("Добавление пользователя {} в друзья к пользователю {}", friendId, id);
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        if (user.getFriendshipStatus(friendId) == FriendshipStatus.UNCONFIRMED) {
            log.info("Подтверждение пользователем {} дружбы с пользователем {}", id, friendId);
        }
        user.addFriend(friendId, FriendshipStatus.CONFIRMED);

        if (friend.getFriendshipStatus(id) == null) {
            log.info("Приглашение пользователя {} в друзья к {}", friendId, id);
            friend.addFriend(id, FriendshipStatus.UNCONFIRMED);
        }
        return user;
    }

    public void userDeleteFriend(Long id, Long friendId) {
        log.info("Удаление пользователя {} из друзей пользователя {}", friendId, id);
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        user.removeFriend(friendId);
        friend.removeFriend(id);
    }

    public Collection<User> userGetFriends(Long id) {
        log.info("Получение друзей пользователя {}", id);
        return userStorage.getUser(id).getFriends().keySet()
                .stream()
                .map(userStorage::getUser)
                .toList();
    }

    public Collection<User> usersGetCommonFriends(Long id, Long otherId) {
        log.info("Получение общих друзей для пользователей {} и {}", id, otherId);
        Set<Long> userFriends = userStorage.getUser(id).getFriends().keySet();
        Set<Long> otherUserFriends = userStorage.getUser(otherId).getFriends().keySet();

        Set<Long> commonFriends = new HashSet<>(userFriends);
        log.info("Получение общих ИД пользователей {} и {}", id, otherId);
        commonFriends.retainAll(otherUserFriends);
        log.info("Получение списка пользователей по списку общих ИД друзей {} и {}", id, otherId);
        return commonFriends
                .stream()
                .map(userStorage::getUser)
                .toList();
    }
}

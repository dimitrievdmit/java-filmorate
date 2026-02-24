package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FeedService feedService;

    public UserService(UserStorage userStorage, FeedService feedService) {
        this.userStorage = userStorage;
        this.feedService = feedService;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(@Valid User user) {
        return userStorage.createUser((user));
    }

    public User getUser(Long id) {
        log.info("Получение пользователя по id {}", id);
        Validator.validateId(id, "Id пользователя должен быть указан");
        checkThatUserExists(id);
        return userStorage.getUser(id);
    }

    public void checkThatUserExists(Long id) {
        log.info("Проверить, что пользователь существует.");
        if (userStorage.checkIfNotExists(id)) {
            String errText = "Пользователь с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
    }

    public User updateUser(@Valid User newUser) {
        log.info("Обновление пользователя {}", newUser.getLogin());
        checkThatUserExists(newUser.getId());
        Validator.fillNameWithLoginIfEmpty(newUser);
        return userStorage.updateUser(newUser);
    }

    public void deleteUser(Long id) {
        log.info("Удаление пользователя по id {}", id);
        User user = getUser(id);
        log.info("Удаление пользователя {}", user.getName());
        userStorage.deleteUser(id);
    }

    public void userAddFriend(Long id, Long friendId) {
        log.info("Добавление дружбы: пользователь {} → друг {}", id, friendId);
        User user = getUser(id);
        User friend = getUser(friendId);

        if (user.getFriendshipStatus(friendId) == FriendshipStatus.CONFIRMED) {
            log.info("Дружба: пользователь {} ↔ друг {} уже подтверждена", id, friendId);
            return;
        }

        boolean added = false;

        if (friend.getFriendshipStatus(id) == FriendshipStatus.UNCONFIRMED) {
            log.info("Пользователь {} уже находится в неподтверждённых друзьях пользователя {}", id, friendId);
            log.info("Подтверждение дружбы: пользователь {} ↔ друг {}", id, friendId);
            userStorage.userAddUnconfirmedFriend(id, friendId);
            userStorage.confirmFriendship(id, friendId);
            added = true;
        }

        if (friend.getFriendshipStatus(id) == null) {
            log.info("Добавление неподтверждённой дружбы: пользователь {} → друг {}", id, friendId);
            userStorage.userAddUnconfirmedFriend(id, friendId);
            added = true;
        }

        if (added) {
            feedService.logEvent(id, EventType.FRIEND, EventOperation.ADD, friendId);
        }
    }

    public void userDeleteFriend(Long id, Long friendId) {
        log.info("Удаление дружбы: пользователь {} → друг {}", id, friendId);
        User user = getUser(id);
        User friend = getUser(friendId);

        if (user.getFriendshipStatus(friendId) == null) {
            log.info("Дружба: пользователь {} → друг {} не найдена. Ничего не делать.", id, friendId);
            return;
        }

        userStorage.userDeleteFriend(id, friendId);
        if (friend.getFriendshipStatus(id) == FriendshipStatus.CONFIRMED) {
            log.info("Дружба: пользователь {} ↔ друг {} была подтверждена", id, friendId);
            log.info("Понизить статус дружбы: пользователь {} → друг {} до неподтверждённой", friendId, id);
            log.info("Удаление дружбы: пользователь {} → друг {}", friendId, id);
            userStorage.userDeleteFriend(friendId, id);
            log.info("Добавление неподтверждённой дружбы: пользователь {} → друг {}", friendId, id);
            userStorage.userAddUnconfirmedFriend(friendId, id);
        }

        feedService.logEvent(id, EventType.FRIEND, EventOperation.REMOVE, friendId);
    }

    public Collection<User> userGetFriends(Long id) {
        log.info("Получение друзей пользователя {}", id);
        return getUser(id)
                .getFriends()
                .keySet()
                .stream()
                .map(this::getUser)
                .toList();
    }

    public Collection<User> usersGetCommonFriends(Long id, Long otherId) {
        log.info("Получение общих друзей для пользователей {} и {}", id, otherId);
        Set<Long> userFriends = getUser(id).getFriends().keySet();
        Set<Long> otherUserFriends = getUser(otherId).getFriends().keySet();

        Set<Long> commonFriends = new HashSet<>(userFriends);
        log.info("Получение общих ИД пользователей {} и {}", id, otherId);
        commonFriends.retainAll(otherUserFriends);
        log.info("Получение списка пользователей по списку общих ИД друзей {} и {}", id, otherId);
        return commonFriends
                .stream()
                .map(this::getUser)
                .toList();
    }
}

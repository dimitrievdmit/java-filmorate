package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@Component
@Slf4j
@Profile("inmemory")  // аннотация @Qualifier в сервисах мешала настроить тесты сразу на обе реализации
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
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
        return users.get(id);
    }

    @Override
    public User updateUser(User newUser) {
        User oldUser = getUser(newUser.getId());
        return updateUserFields(oldUser, newUser);
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public boolean checkIfNotExists(Long id) {
        return !users.containsKey(id);
    }

    @Override
    public void userAddUnconfirmedFriend(Long id, Long friendId) {
        getUser(id).addFriend(friendId, FriendshipStatus.UNCONFIRMED);
    }

    @Override
    public void confirmFriendship(Long id, Long friendId) {
        getUser(id).addFriend(friendId, FriendshipStatus.CONFIRMED);
        getUser(friendId).addFriend(id, FriendshipStatus.CONFIRMED);
    }

    @Override
    public void userDeleteFriend(Long id, Long friendId) {
        getUser(id).removeFriend(friendId);
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

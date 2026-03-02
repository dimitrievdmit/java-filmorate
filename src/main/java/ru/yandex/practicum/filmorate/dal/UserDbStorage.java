package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.mappers.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Slf4j
@Repository
public class UserDbStorage extends BaseDBRepository<User> implements UserStorage {

    private final FriendshipRowMapper friendshipRowMapper;

    private static final String SELECT_ALL_USERS = """
                SELECT
                    u.id,
                    u.email,
                    u.login,
                    u.name,
                    u.birthday
                FROM users u
            """;

    private static final String SELECT_USER_BY_ID = """
                SELECT
                    u.id,
                    u.email,
                    u.login,
                    u.name,
                    u.birthday
                FROM users u
                WHERE u.id = :id
            """;

    private static final String INSERT_USER = """
                INSERT INTO users (email, login, name, birthday)
                VALUES (:email, :login, :name, :birthday)
            """;

    private static final String UPDATE_USER = """
                UPDATE users
                SET email = :email,
                    login = :login,
                    name = :name,
                    birthday = :birthday
                WHERE id = :id
            """;

    private static final String DELETE_USER = "DELETE FROM users WHERE id = :id";

    private static final String INSERT_UNCONFIRMED_FRIEND = """
                INSERT INTO friends (user_id, friend_id, status_id)
                VALUES (:userId, :friendId, 2)
            """; // 2 = статус "неподтверждённый"

    private static final String CONFIRM_FRIENDSHIP = """
                UPDATE friends
                SET status_id = 1
                WHERE user_id = :userId AND friend_id = :friendId
                  OR user_id = :friendId AND friend_id = :userId
            """;  // 1 = статус "подтверждённый"

    private static final String DELETE_FRIEND = """
                DELETE FROM friends
                WHERE user_id = :userId AND friend_id = :friendId
            """;

    private static final String SELECT_FRIENDS_FOR_USERS = """
                SELECT
                    f.user_id,
                    f.friend_id,
                    f.status_id
                FROM friends f
                WHERE f.user_id IN (:userIds)
            """;

    public UserDbStorage(
            NamedParameterJdbcTemplate jdbc,
            RowMapper<User> userRowMapper,
            FriendshipRowMapper friendshipRowMapper
    ) {
        super(jdbc, userRowMapper);
        this.friendshipRowMapper = friendshipRowMapper;
    }


    @Override
    public Collection<User> getAllUsers() {
        log.info("Получение всех пользователей из БД");
        try {
            List<User> users = findMany(SELECT_ALL_USERS, Collections.emptyMap());
            if (users.isEmpty()) {
                return users;
            }
            return getUserAdditionalData(users);
        } catch (Exception e) {
            log.error("Ошибка при получении всех пользователей", e);
            throw new InternalServerException("Не удалось получить список пользователей", e);
        }
    }

    @Override
    public User getUser(Long id) {
        log.info("Получение пользователя по ID: {}", id);
        try {
            Optional<User> userOpt = findOne(SELECT_USER_BY_ID, Map.of("id", id));
            if (userOpt.isEmpty()) {
                String errText = "Пользователь с id = " + id + " не найден";
                log.error("Ошибка: {}", errText);
                throw new NotFoundException(errText);
            }

            User user = userOpt.get();
            return getUserAdditionalData(List.of(user)).stream().findFirst().orElse(null);
        } catch (Exception e) {
            log.error("Ошибка при получении пользователя с ID {}", id, e);
            throw new InternalServerException("Не удалось получить пользователя с ID " + id, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public User createUser(User user) {
        log.info("Создание нового пользователя: email={}, login={}", user.getEmail(), user.getLogin());
        try {
            Map<String, Object> params = Map.of(
                    "email", user.getEmail(),
                    "login", user.getLogin(),
                    "name", user.getName(),
                    "birthday", user.getBirthday()
            );

            long userId = insert(INSERT_USER, params);
            user.setId(userId);
            return user;
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: email={}", user.getEmail(), e);
            throw new InternalServerException("Не удалось создать пользователя", e);
        }
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public User updateUser(User newUser) {
        log.info("Обновление пользователя с ID: {}", newUser.getId());
        try {
            Map<String, Object> params = Map.of(
                    "id", newUser.getId(),
                    "email", newUser.getEmail(),
                    "login", newUser.getLogin(),
                    "name", newUser.getName(),
                    "birthday", newUser.getBirthday()
            );
            update(UPDATE_USER, params, true);
            return newUser;
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя с ID {}", newUser.getId(), e);
            throw new InternalServerException("Не удалось обновить пользователя с ID " + newUser.getId(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с ID: {}", id);
        try {
            delete(DELETE_USER, id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя с ID {}", id, e);
            throw new InternalServerException("Не удалось удалить пользователя с ID " + id, e);
        }
    }

    @Override
    public boolean checkIfNotExists(Long id) {
        try {
            return findOne(SELECT_USER_BY_ID, Map.of("id", id)).isEmpty();
        } catch (Exception e) {
            log.error("Ошибка при проверке существования пользователя с ID {}", id, e);
            throw new InternalServerException("Не удалось проверить существование пользователя с ID " + id, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void userAddUnconfirmedFriend(Long userId, Long friendId) {
        try {
            Map<String, Object> params = Map.of(
                    "userId", userId,
                    "friendId", friendId
            );
            update(INSERT_UNCONFIRMED_FRIEND, params, true);
        } catch (Exception e) {
            log.error("Ошибка при добавлении друга {} для пользователя {}", friendId, userId, e);
            throw new InternalServerException(
                    "Не удалось добавить друга " + friendId + " для пользователя " + userId, e
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void confirmFriendship(Long userId, Long friendId) {
        log.info("Подтверждение дружбы: пользователь {} ↔ друг {}", userId, friendId);
        try {
            Map<String, Object> params = Map.of(
                    "userId", userId,
                    "friendId", friendId
            );
            update(CONFIRM_FRIENDSHIP, params, true);
        } catch (Exception e) {
            log.error("Ошибка при подтверждении дружбы между {} и {}", userId, friendId, e);
            throw new InternalServerException(
                    "Не удалось подтвердить дружбу между " + userId + " и " + friendId, e
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void userDeleteFriend(Long userId, Long friendId) {
        log.info("Удаление друга: пользователь {} → друг {}", userId, friendId);
        try {
            Map<String, Object> params = Map.of(
                    "userId", userId,
                    "friendId", friendId
            );
            update(DELETE_FRIEND, params, true);
        } catch (Exception e) {
            log.error("Ошибка при удалении друга {} из списка друзей пользователя {}", friendId, userId, e);
            throw new InternalServerException(
                    "Не удалось удалить друга " + friendId + " из списка друзей пользователя " + userId, e
            );
        }
    }

    /**
     * Дополняет пользователей данными о друзьях (ID и статус дружбы).
     */
    private Collection<User> getUserAdditionalData(List<User> users) {
        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> userIds = extractUserIds(users);
        Map<Long, List<Friendship>> friendships = loadFriendships(userIds);

        return enrichUsersWithFriends(users, friendships);
    }

    // Шаг 1: Извлекаем ID пользователей
    private List<Long> extractUserIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }

    // Шаг 2: Выгружаем дружеские связи
    private Map<Long, List<Friendship>> loadFriendships(List<Long> userIds) {
        try {
            List<Friendship> friendships = jdbc.query(
                    SELECT_FRIENDS_FOR_USERS,
                    Map.of("userIds", userIds),
                    friendshipRowMapper
            );
            return friendships.stream()
                    .collect(Collectors.groupingBy(Friendship::userId));
        } catch (Exception e) {
            log.error("Ошибка при выгрузке дружеских связей", e);
            throw new InternalServerException("Не удалось выгрузить дружеские связи", e);
        }
    }

    // Шаг 3: Дополняем пользователей друзьями
    private List<User> enrichUsersWithFriends(List<User> users, Map<Long, List<Friendship>> friendships) {
        return users.stream()
                .map(user -> {
                    User enriched = new User();
                    enriched.setId(user.getId());
                    enriched.setEmail(user.getEmail());
                    enriched.setLogin(user.getLogin());
                    enriched.setName(user.getName());
                    enriched.setBirthday(user.getBirthday());


                    List<Friendship> userFriends = friendships.getOrDefault(user.getId(), Collections.emptyList());
                    userFriends.forEach(f -> enriched.addFriend(f.friendId(), f.status()));

                    return enriched;
                })
                .toList();
    }

}
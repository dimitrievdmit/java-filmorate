package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.UserReceiveDTO;
import ru.yandex.practicum.filmorate.dto.UserSendDTO;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    /**
     * Преобразует доменную модель User в SendDTO.
     * Включает информацию о друзьях с их статусом.
     */
    public static UserSendDTO mapToSendDTO(User user) {
        // Преобразуем друзей
        Map<Long, FriendshipStatus> friends = user.getFriends() != null
                ? new HashMap<>(user.getFriends())
                : new HashMap<>();

        return new UserSendDTO(
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                friends
        );
    }

    /**
     * Преобразует доменную модель User в ReceiveDTO.
     * Не включает информацию о друзьях.
     */
    public static UserReceiveDTO mapToReceiveDTO(User user) {
        return new UserReceiveDTO(
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
    }

    /**
     * Преобразует ReceiveDTO в доменную модель User.
     * Добавить пустую таблицу с друзьями.
     */
    public static User mapReceiveToDomain(UserReceiveDTO userDTO) {
        String name = userDTO.getName();
        if (name == null || name.trim().isEmpty()) {
            name = userDTO.getLogin();
        }
        return new User(
                userDTO.getId(),
                userDTO.getEmail(),
                userDTO.getLogin(),
                name,
                userDTO.getBirthday(),
                new HashMap<>()
        );
    }

    /**
     * Преобразует SendDTO в доменную модель User.
     * Включает информацию о друзьях с их статусом.
     */
    public static User mapSendToDomain(UserSendDTO userDTO) {
        // Преобразуем друзей
        Map<Long, FriendshipStatus> friends = userDTO.getFriends() != null
                ? new HashMap<>(userDTO.getFriends())
                : new HashMap<>();
        return new User(
                userDTO.getId(),
                userDTO.getEmail(),
                userDTO.getLogin(),
                userDTO.getName(),
                userDTO.getBirthday(),
                friends
        );
    }
}

package ru.yandex.practicum.filmorate.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserReceiveDTO;
import ru.yandex.practicum.filmorate.dto.UserSendDTO;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void mapToSendDTO_shouldConvertUserToDTOWithFriends() {
        // Создаем пользователя с друзьями
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.ru");
        user.setLogin("test_user");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        // Добавляем друзей
        Map<Long, FriendshipStatus> friends = new HashMap<>();
        friends.put(2L, FriendshipStatus.CONFIRMED);
        friends.put(3L, FriendshipStatus.UNCONFIRMED);
        user.setFriends(friends);

        UserSendDTO dto = UserMapper.mapToSendDTO(user);

        // Проверяем базовые поля
        assertEquals(1L, dto.id());
        assertEquals("test@mail.ru", dto.email());
        assertEquals("test_user", dto.login());
        assertEquals("Test User", dto.name());
        assertEquals(LocalDate.of(1990, 1, 1), dto.birthday());

        // Проверяем друзей
        assertEquals(2, dto.friends().size());
        assertEquals(FriendshipStatus.CONFIRMED, dto.friends().get(2L));
        assertEquals(FriendshipStatus.UNCONFIRMED, dto.friends().get(3L));
    }

    @Test
    void mapToReceiveDTO_shouldConvertUserToDTOWithoutFriends() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.ru");
        user.setLogin("test_user");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        UserReceiveDTO dto = UserMapper.mapToReceiveDTO(user);

        assertEquals(1L, dto.id());
        assertEquals("test@mail.ru", dto.email());
        assertEquals("test_user", dto.login());
        assertEquals("Test User", dto.name());
        assertEquals(LocalDate.of(1990, 1, 1), dto.birthday());
    }

    @Test
    void mapReceiveToDomain_shouldCreateUserWithEmptyFriendsMap() {
        UserReceiveDTO dto = new UserReceiveDTO(
                1L,
                "test@mail.ru",
                "test_user",
                "Test User",
                LocalDate.of(1990, 1, 1)
        );

        User user = UserMapper.mapReceiveToDomain(dto);

        assertEquals(1L, user.getId());
        assertEquals("test@mail.ru", user.getEmail());
        assertEquals("test_user", user.getLogin());
        assertEquals("Test User", user.getName());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthday());
        assertInstanceOf(HashMap.class, user.getFriends());
        assertTrue(user.getFriends().isEmpty());
    }

    @Test
    void mapSendToDomain_shouldCreateUserWithFriends() {
        // Создаем DTO с друзьями
        Map<Long, FriendshipStatus> friendsDto = new HashMap<>();
        friendsDto.put(2L, FriendshipStatus.CONFIRMED);
        friendsDto.put(3L, FriendshipStatus.UNCONFIRMED);

        UserSendDTO dto = new UserSendDTO(
                1L,
                "test@mail.ru",
                "test_user",
                "Test User",
                LocalDate.of(1990, 1, 1),
                friendsDto
        );

        // Конвертируем в доменную модель
        User user = UserMapper.mapSendToDomain(dto);

        // Проверяем базовые поля
        assertEquals(1L, user.getId());
        assertEquals("test@mail.ru", user.getEmail());
        assertEquals("test_user", user.getLogin());
        assertEquals("Test User", user.getName());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthday());

        // Проверяем друзей
        assertEquals(2, user.getFriends().size());
        assertEquals(FriendshipStatus.CONFIRMED, user.getFriends().get(2L));
        assertEquals(FriendshipStatus.UNCONFIRMED, user.getFriends().get(3L));
    }

    @Test
    void mapToDomain_shouldHandleNullFriends() {
        // Создаем DTO с null друзьями
        UserSendDTO dto = new UserSendDTO(
                1L,
                "test@mail.ru",
                "test_user",
                "Test User",
                LocalDate.of(1990, 1, 1),
                null
        );

        // Конвертируем в доменную модель
        User user = UserMapper.mapSendToDomain(dto);

        // Проверяем, что друзья стали пустой коллекцией
        assertNotNull(user.getFriends());
        assertTrue(user.getFriends().isEmpty());
    }

    @Test
    void mapToDomain_shouldPreserveEmptyFields() {
        // Создаем DTO с пустыми полями
        UserReceiveDTO dto = new UserReceiveDTO(
                1L,
                "test@mail.ru",
                "test_user",
                null,  // имя может быть null
                null  // дата рождения может быть null
        );

        User user = UserMapper.mapReceiveToDomain(dto);

        // Проверяем сохранение пустых значений
        assertEquals(1L, user.getId());
        assertEquals("test@mail.ru", user.getEmail());
        assertEquals("test_user", user.getLogin());
        assertNotNull(user.getName());
        assertNull(user.getBirthday());
    }

}

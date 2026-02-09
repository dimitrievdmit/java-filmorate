package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.mock.MockUsers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseUserServiceTest {

    protected abstract UserService getUserService();

    @Test
    void shouldCreateAndGetUser() {
        // Создаем валидного пользователя
        User user = MockUsers.getValidUser(1L);

        // Создаем пользователя через сервис
        User created = getUserService().createUser(user);
        assertNotNull(created.getId());
        assertEquals(user.getEmail(), created.getEmail());

        // Проверяем получение пользователя
        User retrieved = getUserService().getUser(created.getId());
        assertEquals(created, retrieved);
    }

    @Test
    void shouldDeleteUser() {
        // Создаем пользователя
        User user = MockUsers.getValidUser(1L);
        User created = getUserService().createUser(user);

        // Удаляем пользователя
        getUserService().deleteUser(created.getId());

        // Проверяем, что пользователь удален
        assertThrows(NotFoundException.class, () -> getUserService().getUser(created.getId()));
    }

    @Test
    void shouldAddAndRemoveFriend() {
        // Создаем двух пользователей
        User user1 = MockUsers.getValidUser(1L);
        User user2 = MockUsers.getValidUser(2L);

        User created1 = getUserService().createUser(user1);
        User created2 = getUserService().createUser(user2);

        // Добавляем друга
        getUserService().userAddFriend(created1.getId(), created2.getId());
        User withFriend = getUserService().getUser(created1.getId());
        assertTrue(withFriend.getFriends().containsKey(created2.getId()));
        assertEquals(FriendshipStatus.UNCONFIRMED, withFriend.getFriends().get(created2.getId()));

        // Подтверждаем дружбу
        getUserService().userAddFriend(created2.getId(), created1.getId());
        User confirmed = getUserService().getUser(created2.getId());
        assertEquals(FriendshipStatus.CONFIRMED, confirmed.getFriends().get(created1.getId()));

        // Удаляем друга
        getUserService().userDeleteFriend(created1.getId(), created2.getId());
        assertFalse(getUserService().getUser(created1.getId()).getFriends().containsKey(created2.getId()));
    }

    @Test
    void shouldGetFriends() {
        // Создаем пользователей
        User user1 = MockUsers.getValidUser(1L);
        User user2 = MockUsers.getValidUser(2L);
        User user3 = MockUsers.getValidUser(3L);

        User created1 = getUserService().createUser(user1);
        User created2 = getUserService().createUser(user2);
        User created3 = getUserService().createUser(user3);

        // Добавляем друзей
        getUserService().userAddFriend(created1.getId(), created2.getId());
        getUserService().userAddFriend(created2.getId(), created1.getId());
        getUserService().userAddFriend(created1.getId(), created3.getId());

        // Получаем список друзей
        List<User> friends = getUserService().userGetFriends(created1.getId()).stream().toList();

        // Проверяем количество друзей
        assertEquals(2, friends.size());

        // Проверяем наличие конкретных друзей
        assertTrue(friends.stream()
                .anyMatch(u -> u.getId().equals(created2.getId())));
        assertTrue(friends.stream()
                .anyMatch(u -> u.getId().equals(created3.getId())));
    }

    @Test
    void shouldGetCommonFriends() {
        // Создаем пользователей
        User user1 = MockUsers.getValidUser(1L);
        User user2 = MockUsers.getValidUser(2L);
        User user3 = MockUsers.getValidUser(3L);
        User user4 = MockUsers.getValidUser(4L);

        User created1 = getUserService().createUser(user1);
        User created2 = getUserService().createUser(user2);
        User created3 = getUserService().createUser(user3);
        User created4 = getUserService().createUser(user4);

        // Добавляем друзей
        getUserService().userAddFriend(created1.getId(), created2.getId());
        getUserService().userAddFriend(created2.getId(), created1.getId());
        getUserService().userAddFriend(created1.getId(), created3.getId());
        getUserService().userAddFriend(created3.getId(), created1.getId());
        getUserService().userAddFriend(created2.getId(), created3.getId());
        getUserService().userAddFriend(created3.getId(), created2.getId());
        getUserService().userAddFriend(created2.getId(), created4.getId());
        getUserService().userAddFriend(created4.getId(), created2.getId());

        // Получаем общих друзей
        List<User> commonFriends = getUserService().usersGetCommonFriends(
                created1.getId(), created2.getId()).stream().toList();

        // Проверяем, что общий друг только один
        assertEquals(1, commonFriends.size());

        // Проверяем, что это user3
        assertTrue(commonFriends.stream()
                .anyMatch(u -> u.getId().equals(created3.getId())));
    }

    @Test
    void shouldHandleNonExistingUser() {
        assertThrows(NotFoundException.class, () -> getUserService().getUser(999L));

        assertThrows(NotFoundException.class, () -> getUserService().deleteUser(999L));

        assertThrows(NotFoundException.class, () -> getUserService().userAddFriend(999L, 1L));
    }

    @Test
    void shouldGetAllUsers() {
        // Создаем нескольких пользователей
        User user1 = MockUsers.getValidUser(1L);
        User user2 = MockUsers.getValidUser(2L);
        User user3 = MockUsers.getValidUser(3L);

        getUserService().createUser(user1);
        getUserService().createUser(user2);
        getUserService().createUser(user3);

        // Получаем всех пользователей
        List<User> allUsers = getUserService().getAllUsers().stream().toList();

        // Проверяем количество
        assertEquals(3, allUsers.size());

        // Проверяем наличие всех созданных пользователей
        assertTrue(allUsers.stream()
                .anyMatch(u -> u.getId().equals(user1.getId())));
        assertTrue(allUsers.stream()
                .anyMatch(u -> u.getId().equals(user2.getId())));
        assertTrue(allUsers.stream()
                .anyMatch(u -> u.getId().equals(user3.getId())));
    }

    @Test
    void shouldHandleEmptyUserList() {
        // Проверяем, что при отсутствии пользователей возвращается пустой список
        List<User> allUsers = getUserService().getAllUsers().stream().toList();
        assertTrue(allUsers.isEmpty());
    }

    @Test
    void shouldUpdateUser() {
        // Создаем пользователя
        User user = MockUsers.getValidUser(1L);
        User created = getUserService().createUser(user);

        // Обновляем данные через MockUsers
        User updatedUser = MockUsers.getValidUser(created.getId());
        updatedUser.setEmail("new-email@mail.ru");
        updatedUser.setLogin("new-login");
        updatedUser.setName("New Name");
        updatedUser.setBirthday(LocalDate.of(1990, 1, 1));

        User updated = getUserService().updateUser(updatedUser);
        assertEquals("new-email@mail.ru", updated.getEmail());
        assertEquals("new-login", updated.getLogin());
        assertEquals("New Name", updated.getName());
    }

    @Test
    void shouldCreateUserWithMinimalData() {
        // Создаем пользователя с минимально допустимыми данными
        User user = MockUsers.getValidUser(1L);

        User created = getUserService().createUser(user);
        assertNotNull(created.getId());

        // Проверяем, что все обязательные поля заполнены
        assertNotNull(created.getEmail());
        assertNotNull(created.getLogin());
        assertNotNull(created.getBirthday());
    }


}
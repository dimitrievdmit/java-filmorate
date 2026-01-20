package ru.yandex.practicum.filmorate.service;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static ru.yandex.practicum.filmorate.mock.MockUsers.getValidUser;

class UserServiceTest {
    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userStorage = Mockito.mock(UserStorage.class);
        userService = new UserService(userStorage);
    }

    @Test
    void shouldGetAllUsers() {
        List<User> users = List.of(getValidUser(1L), getValidUser(2L));
        when(userStorage.getAllUsers()).thenReturn(users);

        Collection<User> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userStorage, times(1)).getAllUsers();
    }

    @Test
    void shouldCreateUser() {
        User user = getValidUser(1L);
        when(userStorage.createUser(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals(user, result);
        verify(userStorage, times(1)).createUser(user);
    }

    @Test
    void shouldGetUser() {
        User user = getValidUser(1L);
        when(userStorage.getUser(1L)).thenReturn(user);

        User result = userService.getUser(1L);

        assertEquals(user, result);
        verify(userStorage, times(1)).getUser(1L);
    }

    @Test
    void shouldUpdateUser() {
        User user = getValidUser(1L);
        when(userStorage.updateUser(user)).thenReturn(user);

        User result = userService.updateUser(user);

        assertEquals(user, result);
        verify(userStorage, times(1)).updateUser(user);
    }

    @Test
    void shouldAddFriend() {
        User user = getValidUser(1L);
        User friend = getValidUser(2L);
        when(userStorage.getUser(1L)).thenReturn(user);
        when(userStorage.getUser(2L)).thenReturn(friend);

        User result = userService.userAddFriend(1L, 2L);

        assertTrue(user.getFriends().containsKey(2L));
        assertTrue(friend.getFriends().containsKey(1L));
        assertEquals(user, result);
    }

    @Test
    void shouldDeleteFriend() {
        User user = getValidUser(1L);
        User friend = getValidUser(2L);
        user.addFriend(2L, FriendshipStatus.CONFIRMED);
        friend.addFriend(1L, FriendshipStatus.CONFIRMED);
        when(userStorage.getUser(1L)).thenReturn(user);
        when(userStorage.getUser(2L)).thenReturn(friend);

        userService.userDeleteFriend(1L, 2L);

        assertFalse(user.getFriends().containsKey(2L));
        assertFalse(friend.getFriends().containsKey(1L));
    }

    @Test
    void shouldGetUserFriends() {
        User user = getValidUser(1L);
        user.addFriend(2L, FriendshipStatus.CONFIRMED);
        User friend = getValidUser(2L);
        when(userStorage.getUser(1L)).thenReturn(user);
        when(userStorage.getUser(2L)).thenReturn(friend);

        Collection<User> result = userService.userGetFriends(1L);

        assertEquals(List.of(friend), result);
    }

    @Test
    void shouldGetCommonFriends() {
        User user1 = getValidUser(1L);
        User user2 = getValidUser(2L);
        User commonFriend = getValidUser(3L);
        user1.addFriend(3L, FriendshipStatus.CONFIRMED);
        user2.addFriend(3L, FriendshipStatus.CONFIRMED);
        when(userStorage.getUser(1L)).thenReturn(user1);
        when(userStorage.getUser(2L)).thenReturn(user2);
        when(userStorage.getUser(3L)).thenReturn(commonFriend);

        Collection<User> result = userService.usersGetCommonFriends(1L, 2L);

        assertEquals(Set.of(commonFriend), Set.copyOf(result));
    }

}
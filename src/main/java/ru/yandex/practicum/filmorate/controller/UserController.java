package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User userAddFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.userAddFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userDeleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.userDeleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> userGetFriends(@PathVariable Long id) {
        return userService.userGetFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> usersGetCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.usersGetCommonFriends(id, otherId);
    }
}

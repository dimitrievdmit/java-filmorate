package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmSendDTO;
import ru.yandex.practicum.filmorate.dto.UserReceiveDTO;
import ru.yandex.practicum.filmorate.dto.UserSendDTO;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RecommendationService recommendationService;

    @GetMapping
    public Collection<UserSendDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::mapToSendDTO)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserSendDTO createUser(@Valid @RequestBody UserReceiveDTO user) {
        return UserMapper.mapToSendDTO(userService.createUser(UserMapper.mapReceiveToDomain(user)));
    }

    @GetMapping("/{id}")
    public UserSendDTO getUser(@PathVariable Long id) {
        return UserMapper.mapToSendDTO(userService.getUser(id));
    }

    @PutMapping
    public UserSendDTO updateUser(@Valid @RequestBody UserReceiveDTO newUser) {
        return UserMapper.mapToSendDTO(userService.updateUser(UserMapper.mapReceiveToDomain(newUser)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void userAddFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.userAddFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userDeleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.userDeleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserSendDTO> userGetFriends(@PathVariable Long id) {
        return userService.userGetFriends(id)
                .stream()
                .map(UserMapper::mapToSendDTO)
                .toList();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserSendDTO> usersGetCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.usersGetCommonFriends(id, otherId)
                .stream()
                .map(UserMapper::mapToSendDTO)
                .toList();
    }

    @GetMapping("/{id}/recommendations")
    public Collection<FilmSendDTO> getFilmRecommendations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10")
            @Positive(message = "Параметр должен быть положительным числом")
            Long count
    ) {
        return recommendationService.getRecommendedFilms(id, count)
                .stream()
                .map(FilmMapper::mapToSendDTO)
                .toList();
    }
}

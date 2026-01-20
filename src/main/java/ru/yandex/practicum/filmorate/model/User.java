package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ru.yandex.practicum.filmorate.validator.Validator.LOGIN_REGEXP;

@Data
@Slf4j
public class User {

    private Long id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна соответствовать формату электронного адреса")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = LOGIN_REGEXP, message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Map<Long, FriendshipStatus> friends = new HashMap<>();

    private Set<Long> unconfirmedFriends = new HashSet<>();

    public void addFriend(Long id, FriendshipStatus status) {
        log.info("Добавить {} к друзьям {} со статусом {}", id, this.id, status);
        friends.put(id, status);
    }

    public void removeFriend(Long id) {
        log.info("Удалить {} из друзей {}", id, this.id);
        friends.remove(id);
    }

    public FriendshipStatus getFriendshipStatus(Long id) {
        log.info("Получить статус дружбы пользователя {} с пользователем {}", id, this.id);
        return friends.get(id);
    }
}

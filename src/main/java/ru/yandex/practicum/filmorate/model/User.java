package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validator.Validator.LOGIN_REGEXP;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
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

    public void addFriend(Long id, FriendshipStatus status) {
        friends.put(id, status);
    }

    @SuppressWarnings("unused")
    public void removeFriend(Long id) {
        friends.remove(id);
    }

    public FriendshipStatus getFriendshipStatus(Long id) {
        FriendshipStatus friendshipStatus = friends.get(id);
        log.info("Статус дружбы пользователя {} с пользователем {}: {}", id, this.id, friendshipStatus);
        return friendshipStatus;
    }
}

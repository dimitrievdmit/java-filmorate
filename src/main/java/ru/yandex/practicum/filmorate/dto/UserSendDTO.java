package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import ru.yandex.practicum.filmorate.enums.FriendshipStatus;

import java.time.LocalDate;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validator.Validator.LOGIN_REGEXP;

public record UserSendDTO(

        Long id,

        @NotBlank(message = "Электронная почта не может быть пустой")
        @Email(message = "Электронная почта должна соответствовать формату электронного адреса")
        String email,

        @NotBlank(message = "Логин не может быть пустым")
        @Pattern(regexp = LOGIN_REGEXP, message = "Логин не может содержать пробелы")
        String login,

        String name,

        @PastOrPresent(message = "Дата рождения не может быть в будущем")
        LocalDate birthday,

        Map<Long, FriendshipStatus> friends) {

}

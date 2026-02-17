package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDto(
        @NotNull(message = "ID пользователя должен быть задан")
        Long userId,
        @NotNull(message = "ID фильма должен быть задан")
        Long filmId,
        @NotBlank(message = "Напишите ваш комментарий")
        String content,
        @NotNull(message = "Поставьте оценку фильму")
        Boolean isPositive
) {
}
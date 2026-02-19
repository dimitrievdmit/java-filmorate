package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReviewUpdateDto(
        @NotNull(message = "ID отзыва должен быть задан")
        @Positive(message = "ID отзыва должен быть положительным числом")
        Long reviewId,
        @NotBlank(message = "Напишите ваш комментарий")
        String content,
        @NotNull(message = "Поставьте оценку фильму")
        Boolean isPositive
) {
}

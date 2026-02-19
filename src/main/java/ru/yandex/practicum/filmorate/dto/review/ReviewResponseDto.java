package ru.yandex.practicum.filmorate.dto.review;

public record ReviewResponseDto(
        Long reviewId,
        String content,
        Boolean isPositive,
        Long userId,
        Long filmId,
        Integer useful
) {
}

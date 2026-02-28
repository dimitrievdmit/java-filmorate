package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;

public record FilmRatingReceiveDTO(@NotNull(message = "ИД рейтинга не может быть null") Integer id) {
}

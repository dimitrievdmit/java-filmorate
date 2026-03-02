package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;

public record FilmGenreReceiveDTO(@NotNull(message = "ИД жанра не может быть null") Integer id) {
}

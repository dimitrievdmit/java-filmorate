package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record FilmDirectorReceiveDTO(@Min(value = 1L, message = "минимальный id для режиссера 1") Long id) {
}

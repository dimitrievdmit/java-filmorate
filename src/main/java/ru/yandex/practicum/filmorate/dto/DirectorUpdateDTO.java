package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder

public record DirectorUpdateDTO(@Min(value = 1L, message = "минимальный id для режиссера 1") Long id,
                                @NotBlank String name) {
}

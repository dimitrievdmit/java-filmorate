package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DirectorCreateDTO(@NotBlank String name) {
}

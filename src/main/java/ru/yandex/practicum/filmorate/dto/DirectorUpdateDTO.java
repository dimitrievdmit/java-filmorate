package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder

public class DirectorUpdateDTO {
    @Min(value = 1L, message = "минимальный id для режиссера 1")
    private final Long id;
    @NotBlank
    private final String name;
}

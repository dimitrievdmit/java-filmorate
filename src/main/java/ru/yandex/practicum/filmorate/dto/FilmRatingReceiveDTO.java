package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor(force = true) // Чтобы избежать ошибок при получении запросов без mpa
@RequiredArgsConstructor // чтобы оставить обычную логику
public class FilmRatingReceiveDTO {
    @NotNull(message = "ИД рейтинга не может быть null")
    private final Integer id;
}

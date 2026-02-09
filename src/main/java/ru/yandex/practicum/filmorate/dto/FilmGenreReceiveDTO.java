package ru.yandex.practicum.filmorate.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Data
@NoArgsConstructor(force = true) // Чтобы избежать ошибок при получении запросов без жанров
@RequiredArgsConstructor // чтобы оставить обычную логику
public class FilmGenreReceiveDTO {
    @NotNull(message = "ИД жанра не может быть null")
    private final Integer id;
}
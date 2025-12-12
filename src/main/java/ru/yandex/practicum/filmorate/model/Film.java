package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.annotation.AfterSpecifiedDate;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.validator.FilmValidator.MIN_RELEASE_DATE_STR;

@Data
public class Film {
    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Описание не может быть длиннее {max} символов")
    String description;

    @AfterSpecifiedDate(minDate = MIN_RELEASE_DATE_STR, message = "Дата релиза не может быть раньше {minDate}")
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    Integer duration;
}

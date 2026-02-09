package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.annotation.AfterSpecifiedDate;
import ru.yandex.practicum.filmorate.validator.annotation.NoNullElements;

import java.time.LocalDate;
import java.util.List;

import static ru.yandex.practicum.filmorate.validator.Validator.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.validator.Validator.MIN_RELEASE_DATE_STR;

@Data
public class FilmSendDTO {
    private final Long id;

    @NotBlank(message = "Название не может быть пустым")
    private final String name;

    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Описание не может быть длиннее {max} символов")
    private final String description;

    @AfterSpecifiedDate(minDate = MIN_RELEASE_DATE_STR, message = "Дата релиза не может быть раньше {minDate}")
    private final LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private final Long duration;

    @NoNullElements(message = "Жанры не могут содержать null-значения")
    @JsonSetter(nulls = Nulls.SKIP)  // Пропускаем null при десериализации
    private final List<FilmGenreSendDTO> genres;

    @JsonSetter(nulls = Nulls.SKIP)
    private final FilmRatingSendDTO mpa;

    @NoNullElements(message = "Лайки не могут содержать null-значения")
    private final List<Long> likes;
}

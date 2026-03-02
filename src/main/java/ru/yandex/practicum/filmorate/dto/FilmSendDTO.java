package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.filmorate.validator.annotation.AfterSpecifiedDate;
import ru.yandex.practicum.filmorate.validator.annotation.NoNullElements;

import java.time.LocalDate;
import java.util.List;

import static ru.yandex.practicum.filmorate.validator.Validator.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.validator.Validator.MIN_RELEASE_DATE_STR;

public record FilmSendDTO(
        Long id,

        @NotBlank(message = "Название не может быть пустым")
        String name,

        @Size(max = MAX_DESCRIPTION_LENGTH, message = "Описание не может быть длиннее {max} символов")
        String description,

        @AfterSpecifiedDate(minDate = MIN_RELEASE_DATE_STR, message = "Дата релиза не может быть раньше {minDate}")
        LocalDate releaseDate,

        @Positive(message = "Продолжительность фильма должна быть положительным числом")
        Long duration,

        @NoNullElements(message = "Жанры не могут содержать null-значения")
        @JsonSetter(nulls = Nulls.SKIP)
        List<FilmGenreSendDTO> genres,

        @JsonSetter(nulls = Nulls.SKIP)
        FilmRatingSendDTO mpa,

        @NoNullElements(message = "Лайки не могут содержать null-значения")
        List<Long> likes,

        @NoNullElements(message = "Режиссеры не могут содержать null-значения")
        List<DirectorSendDTO> directors) {
}

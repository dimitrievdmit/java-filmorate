package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmRating;
import ru.yandex.practicum.filmorate.validator.annotation.AfterSpecifiedDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static ru.yandex.practicum.filmorate.validator.Validator.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.validator.Validator.MIN_RELEASE_DATE_STR;

@Data
@Slf4j
public class Film {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Описание не может быть длиннее {max} символов")
    private String description;

    @AfterSpecifiedDate(minDate = MIN_RELEASE_DATE_STR, message = "Дата релиза не может быть раньше {minDate}")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

    private FilmGenre genre;

    private FilmRating rating;

    private Set<Long> likes = new HashSet<>();

    public void addLike(Long id) {
        log.info("Добавление пользователем {} лайка фильму {}", id, this.id);
        likes.add(id);
    }

    public void removeLike(Long id) {
        log.info("Удаление пользователем {} лайка фильму {}", id, this.id);
        likes.remove(id);
    }
}

package ru.yandex.practicum.filmorate.mock;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class MockFilms {
    public static final String VALID_NAME = "Inception";
    public static final String VALID_DESCRIPTION = "A mind-bending thriller";
    public static final LocalDate VALID_RELEASE_DATE = LocalDate.of(2010, 7, 16);
    public static final Integer VALID_DURATION = 148;

    public static Film getValidFilm() {
        Film film = new Film();
        film.setName(VALID_NAME);
        film.setDescription(VALID_DESCRIPTION);
        film.setReleaseDate(VALID_RELEASE_DATE);
        film.setDuration(VALID_DURATION);
        return film;
    }

    public static Film getValidFilm(Long id) {
        Film film = getValidFilm();
        film.setId(id);
        return film;
    }
}

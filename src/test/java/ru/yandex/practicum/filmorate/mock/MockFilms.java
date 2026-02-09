package ru.yandex.practicum.filmorate.mock;

import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmRating;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MockFilms {
    public static final String VALID_NAME = "Inception";
    public static final String VALID_DESCRIPTION = "A mind-bending thriller";
    public static final LocalDate VALID_RELEASE_DATE = LocalDate.of(2010, 7, 16);
    public static final Long VALID_DURATION = 148L;
    public static final Set<FilmGenre> VALID_GENRES = new HashSet<>(Arrays.asList(FilmGenre.ACTION, FilmGenre.COMEDY));
    public static final FilmRating VALID_RATING = FilmRating.PG;

    public static Film getValidFilm() {
        Film film = new Film();
        film.setName(VALID_NAME);
        film.setDescription(VALID_DESCRIPTION);
        film.setReleaseDate(VALID_RELEASE_DATE);
        film.setDuration(VALID_DURATION);
        film.setGenres(VALID_GENRES);
        film.setRating(VALID_RATING);
        return film;
    }

    public static Film getValidFilm(Long id) {
        Film film = getValidFilm();
        film.setId(id);
        return film;
    }
}

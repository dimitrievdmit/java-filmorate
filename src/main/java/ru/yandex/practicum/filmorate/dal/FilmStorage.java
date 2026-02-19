package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Collection<Film> getFilms(List<Long> filmIds);

    Film getFilm(Long id);

    Film createFilm(Film film);

    Film filmAddGenre(Long id, Integer genreId);

    Film removeGenre(Long id, Integer genreId);

    Film updateFilm(Film newFilm);

    void deleteFilm(Long id);

    Collection<Film> getPopularFilms(Long count);

    boolean checkIfNotExists(Long id);

}

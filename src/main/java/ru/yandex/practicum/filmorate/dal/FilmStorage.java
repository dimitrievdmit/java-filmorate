package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film createFilm(Film film);

    Film getFilm(Long id);

    Film filmAddGenre(Long id, Integer genreId);

    Film removeGenre(Long id, Integer genreId);

    Film updateFilm(Film newFilm);

    void deleteFilm(Long id);

    Film filmAddLike(Long id, Long userId);

    Film removeLike(Long id, Long userId);

    Collection<Film> getPopularFilms(Long count);

    boolean checkIfNotExists(Long id);
}

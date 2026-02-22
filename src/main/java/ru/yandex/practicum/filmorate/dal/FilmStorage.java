package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film createFilm(Film film);

    Film getFilm(Long id);

    Collection<Film> getFilms(List<Long> filmIds);

    Film filmAddGenre(Long id, Integer genreId);

    Film removeGenre(Long id, Integer genreId);

    Film updateFilm(Film newFilm);

    void deleteFilm(Long id);

    Film filmAddLike(Long id, Long userId);

    Film removeLike(Long id, Long userId);

    Collection<Film> getPopularFilms(Long count, Integer genreId, Integer year);

    boolean checkIfNotExists(Long id);

    List<Film> getDirectorFilms(long directorId);
}

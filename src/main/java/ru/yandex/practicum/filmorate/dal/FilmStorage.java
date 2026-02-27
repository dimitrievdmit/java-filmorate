package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.enums.FilmSearchType;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film createFilm(Film film);

    Film getFilm(Long id);

    Collection<Film> getFilms(List<Long> filmIds);

    Film filmAddGenre(Long id, Integer genreId);

    Film removeGenre(Long id, Integer genreId);

    Film updateFilm(Film newFilm);

    void deleteFilm(Long id);

    Collection<Film> getPopularFilms(Long count, Integer genreId, Integer year);

    Collection<Film> getFilmsByTitleAndDirectorName(String query, FilmSearchType filmSearchType);

    boolean checkIfFilmNotExists(Long id);

    boolean checkIfDirectorNotExists(Long id);

    List<Film> getDirectorFilms(long directorId);

    Collection<Film> getCommonFilms(Long userId, Long friendId);
}

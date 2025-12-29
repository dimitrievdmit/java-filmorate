package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film createFilm(Film film);

    Film getFilm(Long id);

    Film updateFilm(Film newFilm);

    void deleteFilm(Long id);
}

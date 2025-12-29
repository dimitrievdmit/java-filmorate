package ru.yandex.practicum.filmorate.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.yandex.practicum.filmorate.mock.MockFilms.getValidFilm;


class FilmServiceTest {
    private FilmStorage filmStorage;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmStorage = Mockito.mock(FilmStorage.class);
        UserService userService = Mockito.mock(UserService.class);
        filmService = new FilmService(filmStorage, userService);
    }

    @Test
    void shouldGetAllFilms() {
        List<Film> films = List.of(getValidFilm(1L), getValidFilm(2L));
        when(filmStorage.getAllFilms()).thenReturn(films);

        Collection<Film> result = filmService.getAllFilms();

        assertEquals(films, result);
        verify(filmStorage, times(1)).getAllFilms();
    }

    @Test
    void shouldCreateFilm() {
        Film film = getValidFilm(1L);
        when(filmStorage.createFilm(film)).thenReturn(film);

        Film result = filmService.createFilm(film);

        assertEquals(film, result);
        verify(filmStorage, times(1)).createFilm(film);
    }

    @Test
    void shouldGetFilm() {
        Film film = getValidFilm(1L);
        when(filmStorage.getFilm(1L)).thenReturn(film);

        Film result = filmService.getFilm(1L);

        assertEquals(film, result);
        verify(filmStorage, times(1)).getFilm(1L);
    }

    @Test
    void shouldUpdateFilm() {
        Film film = getValidFilm(1L);
        when(filmStorage.updateFilm(film)).thenReturn(film);

        Film result = filmService.updateFilm(film);

        assertEquals(film, result);
        verify(filmStorage, times(1)).updateFilm(film);
    }

    @Test
    void shouldAddLike() {
        Film film = getValidFilm(1L);
        when(filmStorage.getFilm(1L)).thenReturn(film);

        Film result = filmService.filmAddLike(1L, 2L);

        assertTrue(film.getLikes().contains(2L));
        assertEquals(film, result);
    }

    @Test
    void shouldDeleteLike() {
        Film film = getValidFilm(1L);
        film.addLike(2L);
        when(filmStorage.getFilm(1L)).thenReturn(film);

        filmService.filmDeleteLike(1L, 2L);

        assertFalse(film.getLikes().contains(2L));
    }

    @Test
    void shouldGetPopularFilms() {
        Film film1 = new Film();
        film1.addLike(1L);
        Film film2 = new Film();
        film2.addLike(2L);
        film2.addLike(3L);
        List<Film> films = List.of(film1, film2);
        when(filmStorage.getAllFilms()).thenReturn(films);

        Collection<Film> result = filmService.getPopularFilms(1L);

        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(film2);
    }

}

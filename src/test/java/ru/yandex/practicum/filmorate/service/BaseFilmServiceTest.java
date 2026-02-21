package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.mock.MockFilms;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmRating;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.mock.MockUsers.getValidUser;

public abstract class BaseFilmServiceTest {

    protected abstract FilmService getFilmService();

    protected abstract UserService getUserService();

    @Test
    void shouldCreateAndGetFilm() {
        // Создаем валидный фильм
        Film film = MockFilms.getValidFilm(1L);

        // Создаем фильм через сервис
        Film created = getFilmService().createFilm(film);
        assertNotNull(created.getId());
        assertEquals(film.getName(), created.getName());

        // Проверяем получение фильма
        Film retrieved = getFilmService().getFilm(created.getId());
        assertEquals(film.getName(), retrieved.getName());
        assertEquals(film.getDescription(), retrieved.getDescription());
        assertEquals(film.getReleaseDate(), retrieved.getReleaseDate());
        assertEquals(film.getDuration(), retrieved.getDuration());
    }

    @Test
    void shouldUpdateFilm() {
        // Создаем фильм
        Film film = MockFilms.getValidFilm(1L);
        Film created = getFilmService().createFilm(film);

        // Обновляем данные
        Film updatedFilm = new Film(
                created.getId(),
                "Новое название",
                "Новое описание",
                LocalDate.of(2023, 10, 1),
                200L,
                null,
                FilmRating.PG_13,
                null,
                new HashSet<>()
        );

        Film updated = getFilmService().updateFilm(updatedFilm);
        assertEquals("Новое название", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
    }

    @Test
    void shouldDeleteFilm() {
        // Создаем фильм
        Film film = MockFilms.getValidFilm(1L);
        Film created = getFilmService().createFilm(film);

        // Удаляем фильм
        getFilmService().deleteFilm(created.getId());

        // Проверяем, что фильм удален
        assertThrows(NotFoundException.class, () -> getFilmService().getFilm(created.getId()));
    }

    @Test
    void shouldAddAndDeleteLike() {
        // Создаем фильм
        Film film = MockFilms.getValidFilm(1L);
        Film created = getFilmService().createFilm(film);

        Long userId = getUserService().createUser(getValidUser()).getId();

        // Добавляем лайк
        Film liked = getFilmService().filmAddLike(created.getId(), userId);
        assertTrue(liked.getLikes().contains(userId));

        // Удаляем лайк
        Film unliked = getFilmService().filmDeleteLike(created.getId(), userId);
        assertFalse(unliked.getLikes().contains(userId));
    }

    @Test
    void shouldAddAndRemoveGenre() {
        // 1. Создаём фильм без жанров
        Film film = MockFilms.getValidFilm(1L);
        film.setGenres(new HashSet<>());
        Film created = getFilmService().createFilm(film);

        // Используем существующие ID жанров
        int genreId1 = FilmGenre.DRAMA.getId();
        int genreId2 = FilmGenre.COMEDY.getId();

        // 2. Добавляем первый жанр
        Film withFirstGenre = getFilmService().filmAddGenre(created.getId(), genreId1);
        assertTrue(withFirstGenre.getGenres().stream()
                .anyMatch(g -> g.getId() == genreId1));

        // 3. Добавляем второй жанр
        Film withTwoGenres = getFilmService().filmAddGenre(created.getId(), genreId2);
        assertTrue(withTwoGenres.getGenres().stream()
                .anyMatch(g -> g.getId() == genreId1));
        assertTrue(withTwoGenres.getGenres().stream()
                .anyMatch(g -> g.getId() == genreId2));

        // 4. Проверяем через getFilm
        Film refreshed = getFilmService().getFilm(created.getId());
        assertTrue(refreshed.getGenres().stream()
                .anyMatch(g -> g.getId() == genreId1));
        assertTrue(refreshed.getGenres().stream()
                .anyMatch(g -> g.getId() == genreId2));

        // 5. Удаляем первый жанр
        Film afterRemoveFirst = getFilmService().filmDeleteGenre(created.getId(), genreId1);
        assertFalse(afterRemoveFirst.getGenres().stream()
                .anyMatch(g -> g.getId() == genreId1));
        assertTrue(afterRemoveFirst.getGenres().stream()
                .anyMatch(g -> g.getId() == genreId2));

        // 6. Удаляем второй жанр
        Film afterRemoveSecond = getFilmService().filmDeleteGenre(created.getId(), genreId2);
        assertTrue(afterRemoveSecond.getGenres().isEmpty());
    }

    @Test
    void shouldCheckRating() {
        // Создаем фильм с рейтингом
        Film film = MockFilms.getValidFilm(1L);
        film.setRating(FilmRating.PG_13);
        Film created = getFilmService().createFilm(film);

        // Проверяем рейтинг
        assertEquals(FilmRating.PG_13, created.getRating());
    }

    @Test
    void shouldUpdateRating() {
        // Создаем фильм
        Film film = MockFilms.getValidFilm(1L);
        Film created = getFilmService().createFilm(film);

        // Обновляем рейтинг
        Film updatedFilm = new Film(
                created.getId(),
                created.getName(),
                created.getDescription(),
                created.getReleaseDate(),
                created.getDuration(),
                created.getGenres(),
                FilmRating.R,
                created.getLikes(),
                created.getDirectors()
        );

        Film updated = getFilmService().updateFilm(updatedFilm);
        assertEquals(FilmRating.R, updated.getRating());
    }

    @Test
    void shouldHandleInvalidGenreId() {
        // Создаем фильм
        Film film = MockFilms.getValidFilm(1L);
        Film created = getFilmService().createFilm(film);

        // Пытаемся добавить несуществующий жанр
        assertThrows(NotFoundException.class, () ->
                getFilmService().filmAddGenre(created.getId(), 999)
        );
    }
}

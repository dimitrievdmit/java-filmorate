package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mock.MockUsers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.mock.MockFilms;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

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

    @Test
    void shouldGetCommonFilmsSortedByPopularity_WhenCommonLikesExist() {
        // Создаем 3 пользователя
        User u1 = MockUsers.getValidUser(1L);
        u1.setLogin("common_user_1");
        u1.setEmail("common_user_1@mail.com");
        u1.setName("Common User 1");
        u1 = getUserService().createUser(u1);

        User u2 = MockUsers.getValidUser(2L);
        u2.setLogin("common_user_2");
        u2.setEmail("common_user_2@mail.com");
        u2.setName("Common User 2");
        u2 = getUserService().createUser(u2);

        User u3 = MockUsers.getValidUser(3L);
        u3.setLogin("common_user_3");
        u3.setEmail("common_user_3@mail.com");
        u3.setName("Common User 3");
        u3 = getUserService().createUser(u3);

        // Создаем два фильма
        Film filmA = getFilmService().createFilm(MockFilms.getValidFilm(1L));
        Film filmB = getFilmService().createFilm(MockFilms.getValidFilm(2L));

        // Лайки: filmA — 3 лайка (u1, u2, u3), filmB — 2 лайка (u1, u2)
        getFilmService().filmAddLike(filmA.getId(), u1.getId());
        getFilmService().filmAddLike(filmA.getId(), u2.getId());
        getFilmService().filmAddLike(filmA.getId(), u3.getId());

        getFilmService().filmAddLike(filmB.getId(), u1.getId());
        getFilmService().filmAddLike(filmB.getId(), u2.getId());

        List<Film> common = getFilmService().getCommonFilms(u1.getId(), u2.getId())
                .stream()
                .toList();

        assertEquals(2, common.size());
        assertEquals(filmA.getId(), common.get(0).getId());
        assertEquals(filmB.getId(), common.get(1).getId());
    }

    @Test
    void shouldReturnEmptyList_WhenNoCommonLikes() {
        // Создаем двух пользователей с уникальными данными
        User ua = MockUsers.getValidUser(10L);
        ua.setLogin("common_no1");
        ua.setEmail("common_no1@mail.com");
        ua.setName("Common No1");
        ua = getUserService().createUser(ua);

        User ub = MockUsers.getValidUser(11L);
        ub.setLogin("common_no2");
        ub.setEmail("common_no2@mail.com");
        ub.setName("Common No2");
        ub = getUserService().createUser(ub);

        // Создаем две разные пары фильмов
        Film filmC = getFilmService().createFilm(MockFilms.getValidFilm(3L));
        Film filmD = getFilmService().createFilm(MockFilms.getValidFilm(4L));

        // Лайки: filmC - ua; filmD - ub
        getFilmService().filmAddLike(filmC.getId(), ua.getId());
        getFilmService().filmAddLike(filmD.getId(), ub.getId());

        // Нет общих фильмов
        List<Film> common = getFilmService().getCommonFilms(ua.getId(), ub.getId())
                .stream()
                .toList();

        assertTrue(common.isEmpty());
    }

    @Test
    void shouldThrowNotFoundException_WhenOneUserNotExists() {
        // Создаем одного пользователя
        User ua = MockUsers.getValidUser(20L);
        ua.setLogin("common_missing");
        ua.setEmail("common_missing@mail.com");
        ua.setName("Common Missing");
        ua = getUserService().createUser(ua);

        Long nonExistentUserId = 999999L;

        // Второй пользователь не существует
        User finalUa = ua;
        assertThrows(NotFoundException.class, () ->
                getFilmService().getCommonFilms(finalUa.getId(), nonExistentUserId)
        );
    }
}
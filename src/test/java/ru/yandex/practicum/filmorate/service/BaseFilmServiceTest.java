package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.DirectorReceiveDTO;
import ru.yandex.practicum.filmorate.dto.FilmSendDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.mock.MockFilms;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmRating;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.mock.MockUsers.getValidUser;

public abstract class BaseFilmServiceTest {

    protected abstract FilmService getFilmService();

    protected abstract UserService getUserService();

    protected abstract DirectorService getDirectorService();

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
        assertTrue(withFirstGenre.getGenres().stream().anyMatch(g -> g.getId() == genreId1));

        // 3. Добавляем второй жанр
        Film withTwoGenres = getFilmService().filmAddGenre(created.getId(), genreId2);
        assertTrue(withTwoGenres.getGenres().stream().anyMatch(g -> g.getId() == genreId1));
        assertTrue(withTwoGenres.getGenres().stream().anyMatch(g -> g.getId() == genreId2));

        // 4. Проверяем через getFilm
        Film refreshed = getFilmService().getFilm(created.getId());
        assertTrue(refreshed.getGenres().stream().anyMatch(g -> g.getId() == genreId1));
        assertTrue(refreshed.getGenres().stream().anyMatch(g -> g.getId() == genreId2));

        // 5. Удаляем первый жанр
        Film afterRemoveFirst = getFilmService().filmDeleteGenre(created.getId(), genreId1);
        assertFalse(afterRemoveFirst.getGenres().stream().anyMatch(g -> g.getId() == genreId1));
        assertTrue(afterRemoveFirst.getGenres().stream().anyMatch(g -> g.getId() == genreId2));

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
        assertThrows(NotFoundException.class, () -> getFilmService().filmAddGenre(created.getId(), 999));
    }

    @Test
    void shouldGetFilmWithGenres() {
        // Создаём фильм с жанрами
        Film film = MockFilms.getValidFilm(1L);
        Set<FilmGenre> expectedGenres = film.getGenres();
        Film created = getFilmService().createFilm(film);

        // Получаем фильм через сервис
        Film retrieved = getFilmService().getFilm(created.getId());

        // Проверяем, что жанры сохранились и корректно получены
        assertNotNull(retrieved.getGenres());
        assertEquals(expectedGenres.size(), retrieved.getGenres().size());
        assertTrue(retrieved.getGenres().containsAll(expectedGenres));
    }

    @Test
    void shouldGetFilmWithLikes() {
        // Создаём фильм
        Film film = MockFilms.getValidFilm(1L);
        Film created = getFilmService().createFilm(film);

        // Добавляем несколько лайков
        Long userId1 = getUserService().createUser(getValidUser()).getId();
        Long userId2 = getUserService().createUser(getValidUser()).getId();

        getFilmService().filmAddLike(created.getId(), userId1);
        getFilmService().filmAddLike(created.getId(), userId2);

        // Получаем фильм и проверяем лайки
        Film retrieved = getFilmService().getFilm(created.getId());

        assertNotNull(retrieved.getLikes());
        assertEquals(2, retrieved.getLikes().size());
        assertTrue(retrieved.getLikes().contains(userId1));
        assertTrue(retrieved.getLikes().contains(userId2));
    }

    @Test
    void shouldGetFilmWithDirectors() {
        // Создаём фильм без режиссёров
        Film film = MockFilms.getValidFilm(1L);
        film.setDirectors(new HashSet<>());
        Film created = getFilmService().createFilm(film);

        // Добавляем нескольких режиссёров (используем ID пользователей как ID режиссёров)
        DirectorReceiveDTO director1 = new DirectorReceiveDTO(1L, "Test Director");
        Long directorId1 = getDirectorService().createDirector(director1).getId();
        DirectorReceiveDTO director2 = new DirectorReceiveDTO(2L, "Test Director");
        Long directorId2 = getDirectorService().createDirector(director2).getId();

        // Предполагаем, что есть метод добавления режиссёров — реализуем через обновление фильма
        Set<Long> directors = new HashSet<>(Arrays.asList(directorId1, directorId2));
        Film updatedFilm = new Film(created.getId(), created.getName(), created.getDescription(), created.getReleaseDate(), created.getDuration(), created.getGenres(), created.getRating(), created.getLikes(), directors);

        getFilmService().updateFilm(updatedFilm);

        // Получаем фильм и проверяем режиссёров
        Film retrieved = getFilmService().getFilm(created.getId());

        assertNotNull(retrieved.getDirectors());
        assertEquals(directors.size(), retrieved.getDirectors().size());
        assertTrue(retrieved.getDirectors().containsAll(directors));
    }

    @Test
    void shouldHandleEmptyGenresLikesDirectors() {
        // Создаём фильм без жанров, лайков и режиссёров
        Film film = MockFilms.getValidFilm(1L);
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());
        film.setDirectors(new HashSet<>());

        Film created = getFilmService().createFilm(film);

        // Получаем фильм
        Film retrieved = getFilmService().getFilm(created.getId());

        // Проверяем пустые коллекции
        assertNotNull(retrieved.getGenres());
        assertTrue(retrieved.getGenres().isEmpty());

        assertNotNull(retrieved.getLikes());
        assertTrue(retrieved.getLikes().isEmpty());
    }

    @Test
    void shouldGetAllFilmsWithGenresLikesDirectors() {
        // Создаём несколько фильмов с разными данными
        Film film1 = MockFilms.getValidFilm(1L);
        film1.setGenres(new HashSet<>(Arrays.asList(FilmGenre.ACTION, FilmGenre.DRAMA)));
        DirectorReceiveDTO director1 = new DirectorReceiveDTO(1L, "Test Director");
        Long directorId1 = getDirectorService().createDirector(director1).getId();
        DirectorReceiveDTO director2 = new DirectorReceiveDTO(2L, "Test Director");
        Long directorId2 = getDirectorService().createDirector(director2).getId();
        film1.setDirectors(new HashSet<>(Arrays.asList(directorId1, directorId2)));

        Film film2 = MockFilms.getValidFilm(2L);
        film2.setGenres(new HashSet<>(List.of(FilmGenre.COMEDY)));
        DirectorReceiveDTO director3 = new DirectorReceiveDTO(3L, "Test Director");
        Long directorId3 = getDirectorService().createDirector(director3).getId();
        film2.setDirectors(new HashSet<>(List.of(directorId3)));

        Long userId1 = getUserService().createUser(getValidUser()).getId();
        Long userId2 = getUserService().createUser(getValidUser()).getId();

        Film created1 = getFilmService().createFilm(film1);
        Film created2 = getFilmService().createFilm(film2);

        // Добавляем лайки
        getFilmService().filmAddLike(created1.getId(), userId1);
        getFilmService().filmAddLike(created2.getId(), userId2);

        // Получаем все фильмы
        Collection<Film> allFilms = getFilmService().getAllFilms();
        List<Film> filmsList = new ArrayList<>(allFilms);

        assertEquals(2, filmsList.size());

        // Проверяем первый фильм
        Film retrieved1 = filmsList.getFirst();
        assertEquals(created1.getId(), retrieved1.getId());
        assertEquals(2, retrieved1.getGenres().size());
        assertTrue(retrieved1.getGenres().contains(FilmGenre.ACTION));
        assertTrue(retrieved1.getGenres().contains(FilmGenre.DRAMA));
        assertEquals(2, retrieved1.getDirectors().size());
        assertTrue(retrieved1.getDirectors().contains(1L));
        assertTrue(retrieved1.getDirectors().contains(2L));
        assertEquals(1, retrieved1.getLikes().size());
        assertTrue(retrieved1.getLikes().contains(userId1));

        // Проверяем второй фильм
        Film retrieved2 = filmsList.get(1);
        assertEquals(created2.getId(), retrieved2.getId());
        assertEquals(1, retrieved2.getGenres().size());
        assertTrue(retrieved2.getGenres().contains(FilmGenre.COMEDY));
        assertEquals(1, retrieved2.getDirectors().size());
        assertTrue(retrieved2.getDirectors().contains(3L));
        assertEquals(1, retrieved2.getLikes().size());
        assertTrue(retrieved2.getLikes().contains(userId2));
    }

    @Test
    void shouldGetFilmsByIdsWithGenresLikesDirectors() {
        // Создаём фильмы
        Film film1 = MockFilms.getValidFilm(1L);
        film1.setGenres(new HashSet<>(Arrays.asList(FilmGenre.ACTION, FilmGenre.DRAMA)));
        DirectorReceiveDTO director1 = new DirectorReceiveDTO(1L, "Test Director");
        Long directorId1 = getDirectorService().createDirector(director1).getId();
        DirectorReceiveDTO director2 = new DirectorReceiveDTO(2L, "Test Director");
        Long directorId2 = getDirectorService().createDirector(director2).getId();
        film1.setDirectors(new HashSet<>(Arrays.asList(directorId1, directorId2)));

        Film film2 = MockFilms.getValidFilm(2L);
        film2.setGenres(new HashSet<>(List.of(FilmGenre.COMEDY)));
        DirectorReceiveDTO director3 = new DirectorReceiveDTO(3L, "Test Director");
        Long directorId3 = getDirectorService().createDirector(director3).getId();
        film2.setDirectors(new HashSet<>(List.of(directorId3)));

        Film created1 = getFilmService().createFilm(film1);
        Film created2 = getFilmService().createFilm(film2);

        // Добавляем лайки
        Long userId = getUserService().createUser(getValidUser()).getId();
        getFilmService().filmAddLike(created1.getId(), userId);

        // Получаем фильмы по ID
        List<Long> filmIds = Arrays.asList(created1.getId(), created2.getId());
        Collection<Film> retrievedFilms = getFilmService().getFilms(filmIds);
        List<Film> filmsList = new ArrayList<>(retrievedFilms);

        assertEquals(2, filmsList.size());

        // Проверяем фильм 1
        Film retrieved1 = filmsList.getFirst();
        assertEquals(film1.getGenres(), retrieved1.getGenres());
        assertEquals(film1.getDirectors(), retrieved1.getDirectors());
        assertTrue(retrieved1.getLikes().contains(userId));

        // Проверяем фильм 2
        Film retrieved2 = filmsList.get(1);
        assertEquals(film2.getGenres(), retrieved2.getGenres());
        assertEquals(film2.getDirectors(), retrieved2.getDirectors());
        assertTrue(retrieved2.getLikes().isEmpty());
    }

    @Test
    void shouldHandleNotFoundFilmsInGetFilms() {
        // Создаём один фильм
        Film film = MockFilms.getValidFilm(1L);
        getFilmService().createFilm(film);

        // Пытаемся получить фильмы с несуществующими ID
        List<Long> nonExistentIds = Arrays.asList(999L, 1000L);

        assertThrows(NotFoundException.class, () -> getFilmService().getFilms(nonExistentIds));
    }

    @Test
    void shouldGetPopularFilmsWithGenresLikesDirectors() {
        // Создаём фильмы с разным количеством лайков
        Film film1 = MockFilms.getValidFilm(1L);
        film1.setGenres(new HashSet<>(Arrays.asList(FilmGenre.ACTION, FilmGenre.DRAMA)));
        DirectorReceiveDTO director1 = new DirectorReceiveDTO(1L, "Test Director");
        Long directorId1 = getDirectorService().createDirector(director1).getId();
        DirectorReceiveDTO director2 = new DirectorReceiveDTO(2L, "Test Director");
        Long directorId2 = getDirectorService().createDirector(director2).getId();
        film1.setDirectors(new HashSet<>(Arrays.asList(directorId1, directorId2)));

        Film film2 = MockFilms.getValidFilm(2L);
        film2.setGenres(new HashSet<>(List.of(FilmGenre.COMEDY)));
        DirectorReceiveDTO director3 = new DirectorReceiveDTO(3L, "Test Director");
        Long directorId3 = getDirectorService().createDirector(director3).getId();
        film2.setDirectors(new HashSet<>(List.of(directorId3)));

        Film created1 = getFilmService().createFilm(film1);
        Film created2 = getFilmService().createFilm(film2);

        // Добавляем лайки (фильм 1 получит больше лайков)
        Long user1 = getUserService().createUser(getValidUser()).getId();
        Long user2 = getUserService().createUser(getValidUser()).getId();
        Long user3 = getUserService().createUser(getValidUser()).getId();

        getFilmService().filmAddLike(created1.getId(), user1);
        getFilmService().filmAddLike(created1.getId(), user2);
        getFilmService().filmAddLike(created2.getId(), user3);

        // Получаем популярные фильмы (топ-2)
        Collection<Film> popularFilms = getFilmService().getPopularFilms(2L, null, null);
        List<Film> filmsList = new ArrayList<>(popularFilms);

        assertEquals(2, filmsList.size());

        // Фильм с большим количеством лайков должен быть первым
        Film mostPopular = filmsList.getFirst();
        assertEquals(created1.getId(), mostPopular.getId());
        assertEquals(2, mostPopular.getLikes().size());
        assertEquals(film1.getGenres(), mostPopular.getGenres());
        assertEquals(film1.getDirectors(), mostPopular.getDirectors());

        Film lessPopular = filmsList.get(1);
        assertEquals(created2.getId(), lessPopular.getId());
        assertEquals(1, lessPopular.getLikes().size());
        assertEquals(film2.getGenres(), lessPopular.getGenres());
        assertEquals(film2.getDirectors(), lessPopular.getDirectors());
    }

    @Test
    void shouldReturnEmptyListWhenDirectorHasNoFilms() {
        long nonExistentDirectorId = 999L;

        List<FilmSendDTO> films = getFilmService().getSortedDirectorFilms(nonExistentDirectorId, "year");

        assertTrue(films.isEmpty());
    }

    @Test
    void shouldGetPopularFilmsWithGenreFilter() {
        // Создаём фильмы разных жанров
        Film dramaFilm = MockFilms.getValidFilm(1L);
        dramaFilm.setGenres(new HashSet<>(List.of(FilmGenre.DRAMA)));
        DirectorReceiveDTO director1 = new DirectorReceiveDTO(1L, "Test Director");
        Long directorId1 = getDirectorService().createDirector(director1).getId();
        DirectorReceiveDTO director2 = new DirectorReceiveDTO(2L, "Test Director");
        Long directorId2 = getDirectorService().createDirector(director2).getId();
        dramaFilm.setDirectors(new HashSet<>(Arrays.asList(directorId1, directorId2)));

        Film comedyFilm = MockFilms.getValidFilm(2L);
        comedyFilm.setGenres(new HashSet<>(List.of(FilmGenre.COMEDY)));
        DirectorReceiveDTO director3 = new DirectorReceiveDTO(3L, "Test Director");
        Long directorId3 = getDirectorService().createDirector(director3).getId();
        comedyFilm.setDirectors(new HashSet<>(List.of(directorId3)));

        Film createdDrama = getFilmService().createFilm(dramaFilm);
        getFilmService().createFilm(comedyFilm);

        // Добавляем лайки
        Long user = getUserService().createUser(getValidUser()).getId();
        getFilmService().filmAddLike(createdDrama.getId(), user);

        // Получаем популярные фильмы только жанра DRAMA
        Collection<Film> popularDrama = getFilmService().getPopularFilms(10L, FilmGenre.DRAMA.getId(), null);
        List<Film> filmsList = new ArrayList<>(popularDrama);

        assertEquals(1, filmsList.size());
        assertEquals(createdDrama.getId(), filmsList.getFirst().getId());
        assertEquals(FilmGenre.DRAMA, filmsList.getFirst().getGenres().iterator().next());
    }

    @Test
    void shouldGetPopularFilmsWithYearFilter() {
        // Создаём фильмы разных годов
        Film oldFilm = MockFilms.getValidFilm(1L);
        oldFilm.setReleaseDate(LocalDate.of(1980, 1, 1));
        oldFilm.setGenres(new HashSet<>(List.of(FilmGenre.ACTION)));
        DirectorReceiveDTO director1 = new DirectorReceiveDTO(1L, "Test Director");
        Long directorId1 = getDirectorService().createDirector(director1).getId();
        DirectorReceiveDTO director2 = new DirectorReceiveDTO(2L, "Test Director");
        Long directorId2 = getDirectorService().createDirector(director2).getId();
        oldFilm.setDirectors(new HashSet<>(Arrays.asList(directorId1, directorId2)));

        Film recentFilm = MockFilms.getValidFilm(2L);
        recentFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        recentFilm.setGenres(new HashSet<>(List.of(FilmGenre.COMEDY)));
        DirectorReceiveDTO director3 = new DirectorReceiveDTO(3L, "Test Director");
        Long directorId3 = getDirectorService().createDirector(director3).getId();
        recentFilm.setDirectors(new HashSet<>(List.of(directorId3)));

        getFilmService().createFilm(oldFilm);
        Film createdRecent = getFilmService().createFilm(recentFilm);

        // Добавляем лайки
        Long user = getUserService().createUser(getValidUser()).getId();
        getFilmService().filmAddLike(createdRecent.getId(), user);

        // Получаем популярные фильмы 2023 года
        Collection<Film> recentPopular = getFilmService().getPopularFilms(10L, null, 2023);
        List<Film> filmsList = new ArrayList<>(recentPopular);

        assertEquals(1, filmsList.size());
        assertEquals(createdRecent.getId(), filmsList.getFirst().getId());
        assertEquals(2023, filmsList.getFirst().getReleaseDate().getYear());

        // Проверяем, что жанры и режиссёры корректно возвращаются
        assertEquals(1, filmsList.getFirst().getGenres().size());
        assertEquals(FilmGenre.COMEDY, filmsList.getFirst().getGenres().iterator().next());
        assertEquals(1, filmsList.getFirst().getDirectors().size());
        assertTrue(filmsList.getFirst().getDirectors().contains(directorId3));
    }

}

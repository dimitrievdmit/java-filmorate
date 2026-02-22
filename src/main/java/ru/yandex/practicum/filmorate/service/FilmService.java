package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dal.LikeStorage;
import ru.yandex.practicum.filmorate.dto.FilmSendDTO;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final FeedService feedService;

    public FilmService(
            FilmStorage filmStorage,
            LikeStorage likeStorage,
            UserService userService,
            GenreService genreService,
            FeedService feedService
    ) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
        this.genreService = genreService;
        this.feedService = feedService;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Collection<Film> getFilms(List<Long> filmIds) {
        Collection<Film> films = filmStorage.getFilms(filmIds);
        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с filmIds = " + filmIds + " не найдены");
        }
        return films;
    }

    public Film getFilm(Long id) {
        log.info("Получение фильма по id {}", id);
        Validator.validateId(id, "Id фильма должен быть указан");
        checkThatFilmExists(id);
        return filmStorage.getFilm(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        checkThatFilmExists(newFilm.getId());
        return filmStorage.updateFilm(newFilm);
    }

    public void deleteFilm(Long id) {
        log.info("Удаление фильма по id {}", id);
        Film film = getFilm(id);
        log.info("Удаление фильма {}", film.getName());
        filmStorage.deleteFilm(id);
    }

    public Film filmAddLike(Long id, Long userId) {
        log.info("Добавление лайка фильму {} пользователем {}", id, userId);
        Film film = filmStorage.getFilm(id);
        userService.checkThatUserExists(userId);
        feedService.logEvent(userId, EventType.LIKE, EventOperation.ADD, id);
        return likeStorage.filmAddLike(film, userId);
    }

    public Film filmDeleteLike(Long id, Long userId) {
        log.info("Удаление лайка фильму {} пользователем {}", id, userId);
        Film film = filmStorage.getFilm(id);
        userService.checkThatUserExists(userId);
        feedService.logEvent(userId, EventType.LIKE, EventOperation.REMOVE, id);

        return likeStorage.filmRemoveLike(film, userId);
    }

    public Film filmAddGenre(Long id, Integer genreId) {
        log.info("Добавление жанра {} фильму {}", genreId, id);
        checkThatFilmExists(id);
        genreService.getGenre(genreId);
        return filmStorage.filmAddGenre(id, genreId);
    }

    public Film filmDeleteGenre(Long id, Integer genreId) {
        log.info("Удаление жанра {} из фильма {}", genreId, id);
        checkThatFilmExists(id);
        genreService.getGenre(genreId);
        return filmStorage.removeGenre(id, genreId);
    }

    public Collection<Film> getPopularFilms(Long count, Integer genreId, Integer year) {
        log.info("Получение первых {} фильмов по количеству лайков с фильтрами genreId={}, year={}", count, genreId, year);
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public void checkThatFilmExists(Long id) {
        log.info("Проверить, что фильм существует.");
        if (filmStorage.checkIfNotExists(id)) {
            String errText = "Фильм с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
    }

    public List<FilmSendDTO> getSortedDirectorFilms(long directorId, String sortBy) {
        Comparator<Film> comparator = null;

        List<Film> films = filmStorage.getDirectorFilms(directorId);
        if (films.isEmpty()) return new ArrayList<>();

        if (sortBy.equals("year")) comparator = Comparator.comparing(Film::getReleaseDate);
        if (sortBy.equals("likes")) comparator = Comparator.comparing(film -> film.getLikes().size());
        if (comparator == null) throw new IllegalArgumentException("атрибут сортировки задан неверно");

        return films.stream()
                .sorted(comparator)
                .map(FilmMapper::mapToSendDTO)
                .toList();
    }
}

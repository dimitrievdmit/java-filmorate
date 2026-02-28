package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dal.LikeStorage;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.FilmSearchType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unused"})
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final FeedService feedService;
    private final DirectorStorage directorStorage;

    public FilmService(
            FilmStorage filmStorage,
            LikeStorage likeStorage,
            UserService userService,
            GenreService genreService,
            FeedService feedService,
            DirectorStorage directorStorage
    ) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
        this.genreService = genreService;
        this.feedService = feedService;
        this.directorStorage = directorStorage;
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        return addDirectorsToFilms(films);
    }

    public Collection<Film> getFilms(List<Long> filmIds) {
        Collection<Film> films = filmStorage.getFilms(filmIds);
        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с filmIds = " + filmIds + " не найдены");
        }

        return addDirectorsToFilms(films);
    }

    public Film getFilm(Long id) {
        log.info("Получение фильма по id {}", id);
        Validator.validateId(id, "Id фильма должен быть указан");
        checkThatFilmExists(id);
        Film film = filmStorage.getFilm(id);
        List<Film> f = List.of(film);
        return addDirectorsToFilms(f).getFirst();
    }

    public Film createFilm(Film film) {

        Set<Long> directorIds = film.getDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toSet());

        filmStorage.createFilm(film);
        directorStorage.updateDirectorsForFilm(directorIds, film.getId(), false);
        return film;
    }

    public Film updateFilm(Film newFilm) {
        checkThatFilmExists(newFilm.getId());

        Set<Long> directorIds = newFilm.getDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toSet());
        directorStorage.updateDirectorsForFilm(directorIds, newFilm.getId(), true);
        filmStorage.updateFilm(newFilm);
        return newFilm;
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
        if (likeStorage.checkIfExists(film, userId)) return film;
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
        Collection<Film> films = filmStorage.getPopularFilms(count, genreId, year);
        return addDirectorsToFilms(films);
    }

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        log.info("Получение общих фильмов для пользователей {} и {}", userId, friendId);

        // Проверка существования пользователей
        userService.checkThatUserExists(userId);
        userService.checkThatUserExists(friendId);

        Collection<Film> films = filmStorage.getCommonFilms(userId, friendId);
        return addDirectorsToFilms(films);
    }

    public Collection<Film> getFilmsByTitleAndDirectorName(String query, FilmSearchType filmSearchType) {
        log.info("Поиск фильмов по названию и/или режиссёру с query={} и filmSearchType={}", query, filmSearchType);
        Collection<Film> films = filmStorage.getFilmsByTitleAndDirectorName(query, filmSearchType);
        return addDirectorsToFilms(films);
    }

    public void checkThatFilmExists(Long id) {
        log.info("Проверить, что фильм существует.");
        if (filmStorage.checkIfFilmNotExists(id)) {
            String errText = "Фильм с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
    }

    public void checkThatDirectorExists(Long id) {
        log.info("Проверить, что режиссер существует.");
        if (directorStorage.checkIfDirectorNotExists(id)) {
            String errText = "Режиссер с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
    }

    public List<Film> getSortedDirectorFilms(long directorId, String sortBy) {

        checkThatDirectorExists(directorId);
        List<Film> films = filmStorage.getSortedFilmsByDirectorId(directorId, sortBy);
        return addDirectorsToFilms(films);
    }

    private List<Film> addDirectorsToFilms(Collection<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();
        Map<Long, Set<Director>> directorsMap = directorStorage.getFilmDirectors(filmIds);

        return films.stream()
                .peek(f -> f.setDirectors(directorsMap.getOrDefault(f.getId(), new HashSet<>())))
                .toList();
    }
}

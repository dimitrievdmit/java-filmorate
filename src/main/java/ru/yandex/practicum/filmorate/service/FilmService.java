package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dal.LikeStorage;
import ru.yandex.practicum.filmorate.dto.FilmSendDTO;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.FilmSearchType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.*;

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
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        log.info("Получение общих фильмов для пользователей {} и {}", userId, friendId);

        // Проверка существования пользователей
        userService.checkThatUserExists(userId);
        userService.checkThatUserExists(friendId);

        // Получаем карты: userId -> Set<filmId>
        Map<Long, Set<Long>> filmsByUsers = likeStorage.getFilmLikesByUsers(List.of(userId, friendId));

        Set<Long> userLikes = new HashSet<>(filmsByUsers.getOrDefault(userId, Collections.emptySet()));
        Set<Long> friendLikes = new HashSet<>(filmsByUsers.getOrDefault(friendId, Collections.emptySet()));

        // Пересечение — общие фильмы
        userLikes.retainAll(friendLikes);
        if (userLikes.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<Film> films = filmStorage.getFilms(new ArrayList<>(userLikes));

        // Сортируем по популярности (кол-во лайков) по убыванию
        return films.stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes() == null ? 0 : f.getLikes().size()).reversed())
                .toList();
    }

    public Collection<Film> getFilmsByTitleAndDirectorName(String query, FilmSearchType filmSearchType) {
        log.info("Поиск фильмов по названию и/или режиссёру с query={} и filmSearchType={}", query, filmSearchType);
        return filmStorage.getFilmsByTitleAndDirectorName(query, filmSearchType);
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
        if (filmStorage.checkIfDirectorNotExists(id)) {
            String errText = "Режиссер с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
    }

    public List<Film> getSortedDirectorFilms(long directorId, String sortBy) {

        checkThatDirectorExists(directorId);
        return filmStorage.getDirectorFilms(directorId, sortBy);
    }
}

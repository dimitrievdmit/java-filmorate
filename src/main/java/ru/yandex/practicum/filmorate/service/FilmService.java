package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(@Valid Film film) {
        return filmStorage.createFilm(film);
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }

    public Film updateFilm(@Valid Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public void deleteFilm(Long id) {
        filmStorage.deleteFilm(id);
    }

    public Film filmAddLike(Long id, Long userId) {
        log.info("Добавление лайка фильму {} пользователем {}", id, userId);
        Film film = filmStorage.getFilm(id);
        checkThatUserExists(userId);
        film.addLike(userId);
        return film;
    }

    public void filmDeleteLike(Long id, Long userId) {
        log.info("Удаление лайка фильму {} пользователем {}", id, userId);
        Film film = filmStorage.getFilm(id);
        checkThatUserExists(userId);
        film.removeLike(userId);
    }

    public Collection<Film> getPopularFilms(Long count) {
        log.info("Получение первых {} фильмов по количеству лайков", count);
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private void checkThatUserExists(Long userId) {
        log.info("Проверить, что пользователь существует.");
        userService.getUser(userId);
    }
}

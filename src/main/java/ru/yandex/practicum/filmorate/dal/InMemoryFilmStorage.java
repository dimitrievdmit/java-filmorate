package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@SuppressWarnings("unused")
@Component
@Slf4j
@Profile("inmemory")  // аннотация @Qualifier в сервисах мешала настроить тесты сразу на обе реализации
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Создание фильма {}", film.getName());
        // формируем дополнительные данные
        log.info("Формируем id фильма {}", film.getName());
        film.setId(getNextId());
        // сохраняем в памяти приложения
        log.info("Сохраняем фильм {} в памяти приложения", film.getName());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        log.info("Обновление фильма {}", newFilm.getName());
        // проверяем дополнительные необходимые условия
        Film oldFilm = getFilm(newFilm.getId());
        // если найден и все условия соблюдены, обновляем содержимое
        return updateFilmFields(oldFilm, newFilm);
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
    }

    @Override
    public Film filmAddLike(Long id, Long userId) {
        Film film = getFilm(id);
        film.addLike(userId);
        return film;
    }

    @Override
    public Film removeLike(Long id, Long userId) {
        Film film = getFilm(id);
        film.removeLike(userId);
        return film;
    }

    @Override
    public Film filmAddGenre(Long id, Integer genreId) {
        Film film = getFilm(id);
        film.addGenre(FilmGenre.fromId(genreId));
        return film;
    }

    @Override
    public Film removeGenre(Long id, Integer genreId) {
        Film film = getFilm(id);
        film.removeGenre(FilmGenre.fromId(genreId));
        return film;
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return getAllFilms()
                .stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    @Override
    public boolean checkIfNotExists(Long id) {
        return !films.containsKey(id);
    }

    @Override
    //заглушка
    public List<Film> getDirectorFilms(long directorId) {
        return List.of();
    }

    @Override
    //заглушка
    public List<Long> getFilmDirectors(Long filmId) {
        return List.of();
    }

    private Film updateFilmFields(Film oldFilm, Film newFilm) {
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        oldFilm.setGenres(newFilm.getGenres());
        oldFilm.setRating(newFilm.getRating());
        return oldFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}

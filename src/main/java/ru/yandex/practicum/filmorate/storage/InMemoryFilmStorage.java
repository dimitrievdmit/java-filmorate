package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.IdValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@Component
@Slf4j
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
        log.debug("Формируем id фильма {}", film.getName());
        film.setId(getNextId());
        // сохраняем в памяти приложения
        log.debug("Сохраняем фильм {} в памяти приложения", film.getName());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        log.debug("Получение фильма по id {}", id);
        IdValidator.validateId(id, "Id фильма должен быть указан");
        checkIfExists(id);
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
        log.info("Удаление фильма по id {}", id);
        Film film = getFilm(id);
        log.info("Удаление фильма {}", film.getName());
        films.remove(id);
    }

    private void checkIfExists(long id) {
        if (!films.containsKey(id)) {
            String errText = "Фильм с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
    }

    private Film updateFilmFields(Film oldFilm, Film newFilm) {
        log.trace("setName");
        oldFilm.setName(newFilm.getName());
        log.trace("setDescription");
        oldFilm.setDescription(newFilm.getDescription());
        log.trace("setReleaseDate");
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        log.trace("setDuration");
        oldFilm.setDuration(newFilm.getDuration());
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

package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Создание фильма {}", film.getName());
        // формируем дополнительные данные
        log.debug("Формируем id фильма {}", film.getName());
        film.setId(getNextId());
        // сохраняем в памяти приложения
        log.debug("Сохраняем фильм {} в памяти приложения", film.getName());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Обновление фильма {}", newFilm.getName());
        // проверяем дополнительные необходимые условия
        FilmValidator.validateId(newFilm);

        if (!films.containsKey(newFilm.getId())) {
            String errText = "Фильм с id = " + newFilm.getId() + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }

        Film oldFilm = films.get(newFilm.getId());
        // если найден и все условия соблюдены, обновляем содержимое
        log.debug("setName");
        oldFilm.setName(newFilm.getName());
        log.debug("setDescription");
        oldFilm.setDescription(newFilm.getDescription());
        log.debug("setReleaseDate");
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        log.debug("setDuration");
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

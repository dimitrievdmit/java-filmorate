package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreStorage;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;

@Service
@Slf4j
public class GenreService {

    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<FilmGenre> getAllGenres() {
        return genreStorage.getAll();
    }

    public FilmGenre getGenre(Integer id) {
        log.info("Получение жанра по id {}", id);
        Validator.validateId(id, "Id жанра должен быть указан");
        FilmGenre genre = genreStorage.getOne(id);

        if (genre == null) {
            String errText = "Жанр с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
        return genre;
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.RatingStorage;
import ru.yandex.practicum.filmorate.enums.FilmRating;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;

@SuppressWarnings("unused")
@Service
@Slf4j
public class RatingService {

    private final RatingStorage ratingStorage;

    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Collection<FilmRating> getAllRatings() {
        return ratingStorage.getAll();
    }

    public FilmRating getRating(Integer id) {
        log.info("Получение рэйтинга по id {}", id);
        Validator.validateId(id, "Id рэйтинга должен быть указан");
        FilmRating rating = ratingStorage.getOne(id);

        if (rating == null) {
            String errText = "Рэйтинг с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
        return rating;
    }
}

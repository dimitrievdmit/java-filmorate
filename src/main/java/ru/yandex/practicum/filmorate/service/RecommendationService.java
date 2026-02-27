package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@SuppressWarnings("unused")
@Service
@Slf4j
public class RecommendationService {
    private final FilmStorage filmStorage;

    private record UserWithSimilarity(Long userId, int similarity) {
    }

    public RecommendationService(
            FilmStorage filmStorage
    ) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getRecommendedFilms(Long userId, Long count) {
        log.info("Получение {} рекомендованных фильмов пользователю {}", count, userId);
        if (count <= 0) {
            return Collections.emptyList();
        }
        return filmStorage.getRecommendedFilms(userId, count);
    }

}

package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.enums.FilmRating;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseRatingServiceTest {

    protected abstract RatingService getRatingService();

    @Test
    void shouldGetAllRatings() {
        // Получаем все рейтинги
        Collection<FilmRating> allRatings = getRatingService().getAllRatings();

        // Проверяем, что все ожидаемые рейтинги присутствуют
        assertTrue(allRatings.contains(FilmRating.G));
        assertTrue(allRatings.contains(FilmRating.PG));
        assertTrue(allRatings.contains(FilmRating.PG_13));
        assertTrue(allRatings.contains(FilmRating.R));
        assertTrue(allRatings.contains(FilmRating.NC_17));
    }

    @Test
    void shouldGetRatingById() {
        // Проверяем получение существующего рейтинга
        FilmRating rating = getRatingService().getRating(FilmRating.PG.getId());
        assertEquals(FilmRating.PG, rating);

        rating = getRatingService().getRating(FilmRating.R.getId());
        assertEquals(FilmRating.R, rating);
    }

    @Test
    void shouldThrowNotFoundException() {
        // Проверяем обработку несуществующего рейтинга
        assertThrows(NotFoundException.class, () -> getRatingService().getRating(999));
    }

    @Test
    void shouldValidateRatingId() {
        // Проверяем валидацию ID
        assertThrows(ValidationException.class, () -> getRatingService().getRating(null));

        assertThrows(NotFoundException.class, () -> getRatingService().getRating(-1));
    }

    @Test
    void shouldGetRatingDetails() {
        // Проверяем получение деталей рейтинга
        FilmRating rating = getRatingService().getRating(FilmRating.PG_13.getId());

        assertEquals(FilmRating.PG_13.getId(), rating.getId());
        assertEquals(FilmRating.PG_13.getNameWithDash(), rating.getNameWithDash());
    }
}

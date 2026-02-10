package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseGenreServiceTest {

    protected abstract GenreService getGenreService();

    @Test
    void shouldGetAllGenres() {
        // Получаем все жанры
        Collection<FilmGenre> allGenres = getGenreService().getAllGenres();

        // Проверяем, что все ожидаемые жанры присутствуют
        assertTrue(allGenres.contains(FilmGenre.COMEDY));
        assertTrue(allGenres.contains(FilmGenre.DRAMA));
        assertTrue(allGenres.contains(FilmGenre.CARTOON));
        assertTrue(allGenres.contains(FilmGenre.THRILLER));
        assertTrue(allGenres.contains(FilmGenre.DOCUMENTARY));
        assertTrue(allGenres.contains(FilmGenre.ACTION));
    }

    @Test
    void shouldGetGenreById() {
        // Проверяем получение существующего жанра
        FilmGenre genre = getGenreService().getGenre(FilmGenre.COMEDY.getId());
        assertEquals(FilmGenre.COMEDY, genre);

        genre = getGenreService().getGenre(FilmGenre.DRAMA.getId());
        assertEquals(FilmGenre.DRAMA, genre);
    }

    @Test
    void shouldThrowNotFoundException() {
        // Проверяем обработку несуществующего жанра
        assertThrows(NotFoundException.class, () -> getGenreService().getGenre(999));
    }

    @Test
    void shouldValidateGenreId() {
        // Проверяем валидацию ID
        assertThrows(ValidationException.class, () -> getGenreService().getGenre(null));

        assertThrows(NotFoundException.class, () -> getGenreService().getGenre(-1));
    }

    @Test
    void shouldGetGenreDetails() {
        // Проверяем получение деталей жанра
        FilmGenre genre = getGenreService().getGenre(FilmGenre.ACTION.getId());

        assertEquals(FilmGenre.ACTION.getId(), genre.getId());
        assertEquals(FilmGenre.ACTION.getLocalisedName(), genre.getLocalisedName());
    }
}

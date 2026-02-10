package ru.yandex.practicum.filmorate.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmSendDTO;
import ru.yandex.practicum.filmorate.dto.FilmGenreSendDTO;
import ru.yandex.practicum.filmorate.dto.FilmGenreReceiveDTO;
import ru.yandex.practicum.filmorate.dto.FilmRatingReceiveDTO;
import ru.yandex.practicum.filmorate.dto.FilmReceiveDTO;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmRating;
import ru.yandex.practicum.filmorate.mock.MockFilms;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FilmMapperTest {

    @Test
    void mapToDTO_shouldConvertFilmToFilmSendDTOWithCorrectGenreAndRatingDtos() {
        Film film = MockFilms.getValidFilm(1L);

        FilmSendDTO dto = FilmMapper.mapToSendDTO(film);

        assertEquals(1L, dto.getId());
        assertEquals(MockFilms.VALID_NAME, dto.getName());
        assertEquals(MockFilms.VALID_DESCRIPTION, dto.getDescription());
        assertEquals(MockFilms.VALID_RELEASE_DATE, dto.getReleaseDate());
        assertEquals(MockFilms.VALID_DURATION, dto.getDuration());

        Set<Integer> genreIds = dto.getGenres().stream()
                .map(FilmGenreSendDTO::getId)
                .collect(Collectors.toSet());
        assertEquals(Set.of(FilmGenre.ACTION.getId(), FilmGenre.COMEDY.getId()), genreIds);

        assertEquals(FilmRating.PG.getId(), dto.getMpa().getId());
        assertTrue(dto.getLikes().isEmpty());  // В моке likes не заданы
    }

    @Test
    void mapToReceiveDTO_shouldConvertFilmToFilmReceiveDTOWithCorrectGenreAndRatingDtos() {
        Film film = MockFilms.getValidFilm(1L);

        FilmReceiveDTO dto = FilmMapper.mapToReceiveDTO(film);

        assertEquals(1L, dto.getId());
        assertEquals(MockFilms.VALID_NAME, dto.getName());
        assertEquals(MockFilms.VALID_DESCRIPTION, dto.getDescription());
        assertEquals(MockFilms.VALID_RELEASE_DATE, dto.getReleaseDate());
        assertEquals(MockFilms.VALID_DURATION, dto.getDuration());

        Set<Integer> genreIds = dto.getGenres().stream()
                .map(FilmGenreReceiveDTO::getId)
                .collect(Collectors.toSet());
        assertEquals(Set.of(FilmGenre.ACTION.getId(), FilmGenre.COMEDY.getId()), genreIds);
        assertEquals(FilmRating.PG.getId(), dto.getMpa().getId());
        assertTrue(dto.getLikes().isEmpty());
    }

    @Test
    void mapToDomain_shouldConvertFilmReceiveDTOToFilmWithCorrectEnumValues() {
        List<FilmGenreReceiveDTO> genreDtos = List.of(
                new FilmGenreReceiveDTO(FilmGenre.THRILLER.getId()),
                new FilmGenreReceiveDTO(FilmGenre.DOCUMENTARY.getId())
        );
        FilmRatingReceiveDTO mpaDto = new FilmRatingReceiveDTO(FilmRating.R.getId());

        FilmReceiveDTO dto = new FilmReceiveDTO(
                2L,
                "The Matrix",
                "A cyberpunk action film.",
                LocalDate.of(1999, 3, 31),
                136L,
                genreDtos,
                mpaDto,
                List.of(201L, 202L)
        );

        Film domain = FilmMapper.mapToDomain(dto);

        assertEquals(2L, domain.getId());
        assertEquals("The Matrix", domain.getName());
        assertEquals("A cyberpunk action film.", domain.getDescription());
        assertEquals(LocalDate.of(1999, 3, 31), domain.getReleaseDate());
        assertEquals(136L, domain.getDuration());


        Set<FilmGenre> expectedGenres = Set.of(FilmGenre.THRILLER, FilmGenre.DOCUMENTARY);
        assertEquals(expectedGenres, domain.getGenres());
        assertEquals(FilmRating.R, domain.getRating());
        assertEquals(Set.of(201L, 202L), domain.getLikes());
    }

    @Test
    void mapToSendDTO_shouldHandleNullGenresAndRating() {
        Film film = new Film();
        film.setId(3L);
        film.setName("Null Genres and Rating");
        film.setGenres(null);
        film.setRating(null);

        FilmSendDTO dto = FilmMapper.mapToSendDTO(film);
        assertNull(dto.getGenres());
        assertNull(dto.getMpa());
    }

    @Test
    void mapToReceiveDTO_shouldHandleNullGenresAndRating() {
        Film film = new Film();
        film.setId(3L);
        film.setName("Null Genres and Rating");
        film.setGenres(null);
        film.setRating(null);
        FilmReceiveDTO dto = FilmMapper.mapToReceiveDTO(film);
        assertNull(dto.getGenres());
        assertNull(dto.getMpa());
    }

    @Test
    void mapToDomain_shouldThrowExceptionOnNullGenreId() {
        List<FilmGenreReceiveDTO> genreDtos = List.of(new FilmGenreReceiveDTO(null));
        FilmReceiveDTO dto = new FilmReceiveDTO(
                6L,
                "Invalid Genre",
                null, null, null,
                genreDtos,
                new FilmRatingReceiveDTO(1),
                null
        );
        assertThrows(IllegalArgumentException.class,
                () -> FilmMapper.mapToDomain(dto),
                "Ожидалось исключение из‑за null ID жанра"
        );
    }

    @Test
    void mapToSendDTO_shouldPreserveEmptyCollections() {
        Film film = new Film();
        film.setId(6L);
        film.setName("Empty Collections");
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());
        film.setRating(FilmRating.G);

        FilmSendDTO dto = FilmMapper.mapToSendDTO(film);
        assertTrue(dto.getGenres().isEmpty());
        assertEquals(FilmRating.G.getId(), dto.getMpa().getId());
        assertTrue(dto.getLikes().isEmpty());
    }

    @Test
    void mapToReceiveDTO_shouldPreserveEmptyCollections() {
        Film film = new Film();
        film.setId(6L);
        film.setName("Empty Collections");
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());
        film.setRating(FilmRating.G);

        FilmReceiveDTO dto = FilmMapper.mapToReceiveDTO(film);
        assertTrue(dto.getGenres().isEmpty());
        assertEquals(FilmRating.G.getId(), dto.getMpa().getId());
        assertTrue(dto.getLikes().isEmpty());
    }

    @Test
    void mapToDomain_shouldCreateEmptyCollectionsIfNull() {
        FilmReceiveDTO dto = new FilmReceiveDTO(
                7L,
                "Null Collections",
                null,                   // description = null
                null,                   // releaseDate = null
                null,                   // duration = null
                null,                   // genres = null
                new FilmRatingReceiveDTO(FilmRating.PG.getId()),  // mpa = PG
                null                    // likes = null
        );

        Film domain = FilmMapper.mapToDomain(dto);

        // Проверяем базовые поля
        assertEquals(7L, domain.getId());
        assertEquals("Null Collections", domain.getName());
        assertNull(domain.getDescription());
        assertNull(domain.getReleaseDate());
        assertNull(domain.getDuration());

        // Проверяем, что genres стало пустым набором (не null)
        assertNotNull(domain.getGenres());
        assertTrue(domain.getGenres().isEmpty());

        // Проверяем рейтинг — должен быть корректно сконвертирован
        assertEquals(FilmRating.PG, domain.getRating());

        // Проверяем likes — должен быть пустой набор (не null)
        assertNotNull(domain.getLikes());
        assertTrue(domain.getLikes().isEmpty());
    }
}
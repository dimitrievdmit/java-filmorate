package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmRating;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    /**
     * Преобразует доменную модель Film в DTO.
     * Логика обработки null:
     * - null в коллекциях → фильтруем (не включаем в результат)
     * - null в отдельных полях → передаём как null в DTO
     */
    public static FilmSendDTO mapToSendDTO(Film film) {
        // Преобразуем жанры: FilmGenre → FilmGenreReceiveDTO
        List<FilmGenreSendDTO> genreDtos = film.getGenres() != null
                ? film.getGenres().stream()
                .filter(Objects::nonNull)  // Фильтруем null-элементы
                .map(FilmGenreMapper::mapToDTO)
                .sorted(Comparator.comparing(FilmGenreSendDTO::getId))  // сортировка для одинакового ответа
                .collect(Collectors.toList())
                : null;

        // Преобразуем рейтинг: FilmRating → FilmRatingReceiveDTO
        FilmRatingSendDTO mpaDto = film.getRating() != null
                ? FilmRatingMapper.mapToDTO(film.getRating())
                : null;

        // Likes: null → пустой список (контракт модели)
        List<Long> likes = film.getLikes() != null
                ? film.getLikes().stream().toList()
                : Collections.emptyList();

        List<FilmDirectorSendDTO> directors = film.getDirectors() != null ?
                film.getDirectors().stream()
                        .map(FilmDirectorSendDTO::new)
                        .toList()
                : Collections.emptyList();

        return new FilmSendDTO(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                genreDtos,
                mpaDto,
                likes,
                directors
        );
    }

    /**
     * Преобразует доменную модель Film в DTO.
     * Логика обработки null:
     * - null в коллекциях → фильтруем (не включаем в результат)
     * - null в отдельных полях → передаём как null в DTO
     */
    public static FilmReceiveDTO mapToReceiveDTO(Film film) {
        // Преобразуем жанры: FilmGenre → FilmGenreReceiveDTO
        List<FilmGenreReceiveDTO> genreDtos = film.getGenres() != null
                ? film.getGenres().stream()
                .filter(Objects::nonNull)  // Фильтруем null-элементы
                .map(genre -> new FilmGenreReceiveDTO(genre.getId()))
                .sorted(Comparator.comparing(FilmGenreReceiveDTO::getId))  // сортировка для одинакового ответа
                .collect(Collectors.toList())
                : null;

        // Преобразуем рейтинг: FilmRating → FilmRatingReceiveDTO
        FilmRatingReceiveDTO mpaDto = film.getRating() != null
                ? new FilmRatingReceiveDTO(film.getRating().getId())
                : null;

        // Likes: null → пустой список (контракт модели)
        List<Long> likes = film.getLikes() != null
                ? film.getLikes().stream().toList()
                : Collections.emptyList();

        List<FilmDirectorReceiveDTO> directors = film.getDirectors() != null
                ? film.getDirectors().stream()
                .filter(Objects::nonNull)
                .map(FilmDirectorReceiveDTO::new)
                .toList()
                : Collections.emptyList();

        return new FilmReceiveDTO(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                genreDtos,
                mpaDto,
                likes,
                directors
        );
    }

    /**
     * Преобразует DTO в доменную модель Film.
     * Логика обработки null:
     * - null в полях DTO → ошибка валидации
     * - null в коллекциях → ошибка валидации
     */
    public static Film mapToDomain(FilmReceiveDTO filmDTO) {
        // Проверка и преобразование жанров
        Set<FilmGenre> genres = filmDTO.getGenres() != null
                ? filmDTO.getGenres().stream()
                .peek(genreDto -> {
                    if (genreDto == null) {
                        throw new IllegalArgumentException("Элемент жанра не может быть null");
                    }
                    if (genreDto.getId() == null) {
                        throw new IllegalArgumentException("ИД жанра не может быть null");
                    }
                })
                .map(genreDto -> FilmGenre.fromId(genreDto.getId()))
                .collect(Collectors.toSet())
                : new HashSet<>();

        if (filmDTO.getMpa().getId() == null) {
            throw new IllegalArgumentException("ИД рейтинга не может быть null");
        }
        FilmRating rating = FilmRating.fromId(filmDTO.getMpa().getId());

        // Likes: null → пустое множество
        Set<Long> likes = filmDTO.getLikes() != null
                ? new HashSet<>(filmDTO.getLikes())
                : new HashSet<>();

        Set<Long> directors = filmDTO.getDirectors() != null ?
                filmDTO.getDirectors().stream()
                        .map(FilmDirectorReceiveDTO::getId)
                        .collect(Collectors.toSet())
                : new HashSet<>();

        return new Film(
                filmDTO.getId(),
                filmDTO.getName(),
                filmDTO.getDescription(),
                filmDTO.getReleaseDate(),
                filmDTO.getDuration(),
                genres,
                rating,
                likes,
                directors
        );
    }
}

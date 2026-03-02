package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmRating;
import ru.yandex.practicum.filmorate.model.Director;
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
                .sorted(Comparator.comparing(FilmGenreSendDTO::id))  // сортировка для одинакового ответа
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

        // Преобразуем режиссёров: Director → DirectorSendDTO
        List<DirectorSendDTO> directorDtos = film.getDirectors() != null
                ? film.getDirectors().stream()
                .filter(Objects::nonNull)  // Фильтруем null-элементы
                .map(DirectorMapper::mapToSendDTO)
                .sorted(Comparator.comparing(DirectorSendDTO::id))  // сортировка для одинакового ответа
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
                directorDtos
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
                .sorted(Comparator.comparing(FilmGenreReceiveDTO::id))  // сортировка для одинакового ответа
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

        // Преобразуем режиссёров: Director → FilmDirectorReceiveDTO
        List<FilmDirectorReceiveDTO> directorDtos = film.getDirectors() != null
                ? film.getDirectors().stream()
                .filter(Objects::nonNull)  // Фильтруем null-элементы
                .map(DirectorMapper::mapToFilmReceiveDTO)
                .sorted(Comparator.comparing(FilmDirectorReceiveDTO::id))  // сортировка для одинакового ответа
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
                directorDtos
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
        Set<FilmGenre> genres = filmDTO.genres() != null
                ? filmDTO.genres().stream()
                .peek(genreDto -> {
                    if (genreDto == null) {
                        throw new IllegalArgumentException("Элемент жанра не может быть null");
                    }
                    if (genreDto.id() == null) {
                        throw new IllegalArgumentException("ИД жанра не может быть null");
                    }
                })
                .map(genreDto -> FilmGenre.fromId(genreDto.id()))
                .collect(Collectors.toSet())
                : new HashSet<>();

        if (filmDTO.mpa().id() == null) {
            throw new IllegalArgumentException("ИД рейтинга не может быть null");
        }
        FilmRating rating = FilmRating.fromId(filmDTO.mpa().id());

        // Likes: null → пустое множество
        Set<Long> likes = filmDTO.likes() != null
                ? new HashSet<>(filmDTO.likes())
                : new HashSet<>();

        // Преобразуем режиссёров: FilmDirectorReceiveDTO → Director
        Set<Director> directors = filmDTO.directors() != null
                ? filmDTO.directors().stream()
                .filter(Objects::nonNull)  // Фильтруем null-элементы
                .map(DirectorMapper::mapFilmReceiveDTOToDomain)
                .collect(Collectors.toSet())
                : new HashSet<>();

        return new Film(
                filmDTO.id(),
                filmDTO.name(),
                filmDTO.description(),
                filmDTO.releaseDate(),
                filmDTO.duration(),
                genres,
                rating,
                likes,
                directors
        );
    }
}

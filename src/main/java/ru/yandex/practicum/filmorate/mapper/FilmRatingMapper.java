package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmRatingSendDTO;
import ru.yandex.practicum.filmorate.enums.FilmRating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmRatingMapper {
    public static FilmRatingSendDTO mapToDTO(FilmRating filmRating) {
        return new FilmRatingSendDTO(filmRating.getId(), filmRating.getNameWithDash());
    }
}

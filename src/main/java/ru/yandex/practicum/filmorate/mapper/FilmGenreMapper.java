package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmGenreReceiveDTO;
import ru.yandex.practicum.filmorate.dto.FilmGenreSendDTO;
import ru.yandex.practicum.filmorate.enums.FilmGenre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmGenreMapper {
    public static FilmGenreSendDTO mapToDTO(FilmGenre filmGenre) {
        return new FilmGenreSendDTO(filmGenre.getId(), filmGenre.getLocalisedName());
    }

    public static FilmGenreReceiveDTO mapDTOToReceiveDTO(FilmGenreSendDTO filmGenre) {
        return new FilmGenreReceiveDTO(filmGenre.id());
    }
}

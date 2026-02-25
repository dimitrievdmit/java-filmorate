package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.DirectorCreateDTO;
import ru.yandex.practicum.filmorate.dto.DirectorSendDTO;
import ru.yandex.practicum.filmorate.dto.DirectorUpdateDTO;
import ru.yandex.practicum.filmorate.dto.FilmDirectorReceiveDTO;
import ru.yandex.practicum.filmorate.model.Director;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectorMapper {

    public static FilmDirectorReceiveDTO mapToFilmReceiveDTO(Director director) {
        return FilmDirectorReceiveDTO.builder()
                .id(director.getId())
                .build();
    }

    @SuppressWarnings("unused")
    public static DirectorCreateDTO mapToCreateDTO(Director director) {
        return DirectorCreateDTO.builder()
                .name(director.getName())
                .build();
    }

    public static Director mapFilmReceiveDTOToDomain(FilmDirectorReceiveDTO directorDTO) {
        return Director.builder()
                .id(directorDTO.getId())
                .build();
    }

    public static Director mapCreateDTOToDomain(DirectorCreateDTO directorDTO) {
        return Director.builder()
                .id(null)
                .name(directorDTO.getName())
                .build();
    }

    public static Director mapUpdateDTOToDomain(DirectorUpdateDTO directorDTO) {
        return Director.builder()
                .id(directorDTO.getId())
                .name(directorDTO.getName())
                .build();
    }

    public static Director mapSendDTOToDomain(DirectorSendDTO directorDTO) {
        return Director.builder()
                .id(directorDTO.getId())
                .name(directorDTO.getName())
                .build();
    }

    public static DirectorSendDTO mapToSendDTO(Director director) {
        return new DirectorSendDTO(director.getId(), director.getName());
    }
}

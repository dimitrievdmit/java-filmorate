package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.DirectorReceiveDTO;
import ru.yandex.practicum.filmorate.dto.DirectorSendDTO;
import ru.yandex.practicum.filmorate.model.Director;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectorMapper {

    public static DirectorReceiveDTO mapToReceiveDTO(Director director) {
        return DirectorReceiveDTO.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }

    public static Director mapToCreateDomain(DirectorReceiveDTO directorDTO) {
        return Director.builder()
                .id(null)
                .name(directorDTO.getName())
                .build();
    }

    public static Director mapToUpdateDomain(DirectorReceiveDTO directorDTO) {
        return Director.builder()
                .id(directorDTO.getId())
                .name(directorDTO.getName())
                .build();
    }

    public static DirectorSendDTO mapToSendDTO(Director director) {
        return new DirectorSendDTO(director.getId(), director.getName());
    }
}

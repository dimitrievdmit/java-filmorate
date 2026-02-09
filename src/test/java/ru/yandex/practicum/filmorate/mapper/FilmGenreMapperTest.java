package ru.yandex.practicum.filmorate.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmGenreSendDTO;
import ru.yandex.practicum.filmorate.dto.FilmGenreReceiveDTO;
import ru.yandex.practicum.filmorate.enums.FilmGenre;

import static org.junit.jupiter.api.Assertions.*;

class FilmGenreMapperTest {

    @Test
    void mapToDTO_shouldConvertGenreCorrectly() {
        // Создаем жанр для конвертации
        FilmGenre genre = FilmGenre.COMEDY;

        // Конвертируем
        FilmGenreSendDTO dto = FilmGenreMapper.mapToDTO(genre);

        // Проверяем результат
        assertEquals(genre.getId(), dto.getId());
        assertEquals(genre.getLocalisedName(), dto.getName());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void mapToDTO_shouldHandleNull() {
        // Проверяем обработку null
        assertThrows(NullPointerException.class, () -> FilmGenreMapper.mapToDTO(null));
    }

    @Test
    void mapToDTO_shouldConvertAllGenres() {
        // Проверяем конвертацию всех возможных значений enum
        for (FilmGenre genre : FilmGenre.values()) {
            FilmGenreSendDTO dto = FilmGenreMapper.mapToDTO(genre);
            assertEquals(genre.getId(), dto.getId());
            assertEquals(genre.getLocalisedName(), dto.getName());
        }
    }

    @Test
    void mapDTOToReceiveDTO_shouldConvertCorrectly() {
        // Создаем DTO для конвертации
        FilmGenreSendDTO sendDTO = new FilmGenreSendDTO(1, "Комедия");

        // Конвертируем
        FilmGenreReceiveDTO receiveDTO = FilmGenreMapper.mapDTOToReceiveDTO(sendDTO);

        // Проверяем результат
        assertEquals(sendDTO.getId(), receiveDTO.getId());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void mapDTOToReceiveDTO_shouldHandleNull() {
        // Проверяем обработку null
        assertThrows(NullPointerException.class, () -> FilmGenreMapper.mapDTOToReceiveDTO(null));
    }
}

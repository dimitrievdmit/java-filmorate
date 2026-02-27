package ru.yandex.practicum.filmorate.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmRatingSendDTO;
import ru.yandex.practicum.filmorate.enums.FilmRating;

import static org.junit.jupiter.api.Assertions.*;

class FilmRatingMapperTest {

    @Test
    void mapToDTO_shouldConvertRatingCorrectly() {
        // Создаем рейтинг для конвертации
        FilmRating rating = FilmRating.PG_13;

        // Конвертируем
        FilmRatingSendDTO dto = FilmRatingMapper.mapToDTO(rating);

        // Проверяем результат
        assertEquals(rating.getId(), dto.id());
        assertEquals(rating.getNameWithDash(), dto.name());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void mapToDTO_shouldHandleNull() {
        // Проверяем обработку null
        assertThrows(NullPointerException.class, () -> FilmRatingMapper.mapToDTO(null));
    }

    @Test
    void mapToDTO_shouldConvertAllRatings() {
        // Проверяем конвертацию всех возможных значений enum
        for (FilmRating rating : FilmRating.values()) {
            FilmRatingSendDTO dto = FilmRatingMapper.mapToDTO(rating);
            assertEquals(rating.getId(), dto.id());
            assertEquals(rating.getNameWithDash(), dto.name());
        }
    }
}

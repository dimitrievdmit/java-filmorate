package ru.yandex.practicum.filmorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@RequiredArgsConstructor
public enum FilmRating {
    G(1, "G"),
    PG(2, "PG"),
    PG_13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");

    @Getter
    private final int id;
    @Getter
    private final String nameWithDash;

    // Статический метод для поиска enum по id
    public static FilmRating fromId(Integer id) {
        for (FilmRating rating : values()) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        throw new NotFoundException("Неизвестный FilmRating id: " + id);
    }
}

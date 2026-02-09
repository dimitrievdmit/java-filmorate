package ru.yandex.practicum.filmorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@RequiredArgsConstructor
public enum FilmGenre {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    CARTOON(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    @Getter
    private final int id;
    @Getter
    private final String localisedName;

    // Статический метод для поиска enum по id
    public static FilmGenre fromId(Integer id) {
        for (FilmGenre rating : values()) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        throw new NotFoundException("Неизвестный FilmGenre id: " + id);
    }
}

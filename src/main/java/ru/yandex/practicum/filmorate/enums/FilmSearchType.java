package ru.yandex.practicum.filmorate.enums;

@SuppressWarnings("unused")
public enum FilmSearchType {
    TITLE, DIRECTOR, TITLE_AND_DIRECTOR;

    public static FilmSearchType fromValue(String value) {
        return switch (value) {
            case "director" -> DIRECTOR;
            case "title" -> TITLE;
            case "director,title", "title,director" -> TITLE_AND_DIRECTOR;
            default -> throw new IllegalArgumentException("Некорректное значение параметра типа поиска by.");
        };
    }
}


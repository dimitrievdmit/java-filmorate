package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorResponse {
    // название ошибки
    private final String error;
    // подробное описание
    private final String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

}
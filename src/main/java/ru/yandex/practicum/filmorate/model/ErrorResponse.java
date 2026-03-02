package ru.yandex.practicum.filmorate.model;


/**
 * @param error       название ошибки
 * @param description подробное описание
 */
public record ErrorResponse(String error, String description) {

}
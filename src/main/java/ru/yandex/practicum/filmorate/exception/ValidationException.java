package ru.yandex.practicum.filmorate.exception;

@SuppressWarnings("unused")
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
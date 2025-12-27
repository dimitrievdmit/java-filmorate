package ru.yandex.practicum.filmorate.exception;

@SuppressWarnings("unused")
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}